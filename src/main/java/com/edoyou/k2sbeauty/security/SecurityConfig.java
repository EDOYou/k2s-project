package com.edoyou.k2sbeauty.security;

import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private static final Logger LOGGER = Logger.getLogger(SecurityConfig.class.getName());
  private final CustomUserDetailsService customUserDetailsService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public SecurityConfig(CustomUserDetailsService customUserDetailsService,
      PasswordEncoder passwordEncoder) {
    this.customUserDetailsService = customUserDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    System.out.println("configureGlobal called");
    auth.userDetailsService(customUserDetailsService)
        .passwordEncoder(passwordEncoder);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(requests -> requests
            .requestMatchers("/", "/home", "/register", "/guest", "/guest/**",
                "/hairdresser/register_hairdresser", "/client/feedback").permitAll()
            .requestMatchers(HttpMethod.GET, "/client/**").hasRole("CLIENT")
            .requestMatchers(HttpMethod.POST, "/client/book").hasRole("CLIENT")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/hairdresser/**").hasRole("HAIRDRESSER")
            .anyRequest().authenticated()
        )
        .formLogin(loginConfigurer -> loginConfigurer
            .loginPage("/login")
            .permitAll()
            .loginProcessingUrl("/perform_login") // Specify the URL to submit the login form
            .usernameParameter("email")
            .defaultSuccessUrl("/client/appointments",
                true)
            .failureUrl("/login?error=true")
            .successHandler((request, response, authentication) -> {
              LOGGER.info("User '{" + authentication.getName() + "}' logged in successfully");
              String redirectUrl = request.getParameter("redirectUrl");

              if (redirectUrl != null && !redirectUrl.isEmpty()) {
                response.sendRedirect(redirectUrl);
                return;
              }

              String defaultRedirectURL;
              if (authentication.getAuthorities().stream()
                  .anyMatch(
                      grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                defaultRedirectURL = "/admin/dashboard";
              } else if (authentication.getAuthorities().stream().anyMatch(
                  grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_HAIRDRESSER"))) {
                defaultRedirectURL = "/hairdresser/appointments";
              } else {
                defaultRedirectURL = "/client/appointments";
              }

              LOGGER.info("URL in success: " + defaultRedirectURL);
              response.sendRedirect(defaultRedirectURL);
            })
            .failureHandler((request, response, exception) -> {
              LOGGER.warning("Login failure: {" + exception.getMessage() + "}");
              response.sendRedirect("/login");
            })
        )

        .logout(LogoutConfigurer::permitAll);

    return http.build();
  }
}
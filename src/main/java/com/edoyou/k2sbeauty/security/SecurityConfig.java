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

  private final CustomUserDetailsService customUserDetailsService;
  private final PasswordEncoder passwordEncoder;
  private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());

  @Autowired
  public SecurityConfig(CustomUserDetailsService customUserDetailsService,
      PasswordEncoder passwordEncoder) {
    this.customUserDetailsService = customUserDetailsService;
    this.passwordEncoder = passwordEncoder;
    logger.info("SecurityConfig constructor called");
    logger.info("Using PasswordEncoder in Constructor: " + passwordEncoder);
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    System.out.println("configureGlobal called");
    auth.userDetailsService(customUserDetailsService)
        .passwordEncoder(passwordEncoder);
    //System.out.println("Using PasswordEncoder in ConfigureGlobal: " + passwordEncoder);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(requests -> requests
            .requestMatchers("/", "/home", "/register", "/guest", "/guest/**",
                "/hairdresser/register_hairdresser").permitAll()
            .requestMatchers(HttpMethod.GET, "/", "/client/book", "/client/appointments")
            .permitAll()
            .requestMatchers(HttpMethod.POST, "/client/book").authenticated()
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
              logger.info("User '{" + authentication.getName() + "}' logged in successfully");
              String redirectURL;

              if (authentication.getAuthorities().stream()
                  .anyMatch(
                      grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                redirectURL = "/admin/dashboard";
              } else if (authentication.getAuthorities().stream().anyMatch(
                  grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_HAIRDRESSER"))) {
                redirectURL = "/hairdresser/appointments";
              } else {
                redirectURL = "/client/appointments";
              }
              response.sendRedirect(redirectURL);

            })
            .failureHandler((request, response, exception) -> {
              logger.warning("Login failure: {" + exception.getMessage() + "}");
              response.sendRedirect("/login");
            })
        )

        .logout(LogoutConfigurer::permitAll);

    return http.build();
  }
}
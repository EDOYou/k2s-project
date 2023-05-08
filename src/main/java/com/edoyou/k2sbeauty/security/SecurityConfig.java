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
  public SecurityConfig(CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) {
    this.customUserDetailsService = customUserDetailsService;
    this.passwordEncoder = passwordEncoder;
    logger.info("SecurityConfig constructor called");
    logger.info("Using PasswordEncoder in Constructor: " + passwordEncoder);
  }

//  @Bean
//  public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
//    CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
//    filter.setAuthenticationManager(authenticationManager());
//    return filter;
//  }
//
//  @Bean
//  public AuthenticationManager authenticationManager() {
//    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//    provider.setUserDetailsService(customUserDetailsService);
//    provider.setPasswordEncoder(passwordEncoder);
//    return new ProviderManager(Collections.singletonList(provider));
//  }
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
        //.addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(requests -> requests
            .requestMatchers("/", "/guest", "/guest/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/", "/client/book", "/client/appointments").permitAll()
            .requestMatchers(HttpMethod.POST, "/client/book").authenticated()
            .anyRequest().authenticated()
        )
        .formLogin(loginConfigurer -> loginConfigurer
            .loginPage("/client/login") // Specify the login page URL
            .permitAll()
            .loginProcessingUrl("/perform_login") // Specify the URL to submit the login form
            .usernameParameter("email")
            .defaultSuccessUrl("/client/appointments",
                true) // Specify the URL to redirect to after successful login
            .failureUrl("/client/login?error=true")
            .successHandler((request, response, authentication) -> {
              logger.info("User '{" + authentication.getName() + "}' logged in successfully");
              response.sendRedirect("/client/appointments");
            })
            .failureHandler((request, response, exception) -> {
              logger.warning("Login failure: {" + exception.getMessage() + "}");
              response.sendRedirect("/client/login");
            })
        )

        .logout(LogoutConfigurer::permitAll);

    return http.build();
  }
}
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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security Configuration class for the application.
 * <p>
 * This class is annotated with @Configuration and {@code @EnableWebSecurity}, making it a central place
 * for security configuration. It is responsible for:
 * <ul>
 *     <li>Setting up user details service to fetch user details</li>
 *     <li>Setting up password encoding</li>
 *     <li>Setting up request authorizations</li>
 *     <li>Configuring form login</li>
 *     <li>Configuring logout behavior</li>
 * </ul>
 * <p>
 * The class works as follows:
 * <ol>
 * <li>An instance of CustomUserDetailsService and PasswordEncoder is injected via Spring's dependency injection mechanism.</li>
 * <li>The method configureGlobal is used to configure the AuthenticationManagerBuilder by setting the userDetailsService</li>
 *    and the passwordEncoder.
 * <li>The method securityFilterChain sets up the security filter chain for HttpSecurity. It does the following:</li>
 *    <ul>
 *     <li>Sets up path-based access restrictions (which roles can access which paths).</li>
 *     <li>Sets up the form login page and processing URL, as well as the success and failure handlers.</li>
 *     <li>Sets up logout behavior to permit all.</li>
 *    </ul>
 * </ol>
 *
 * @author Taghiyev Kanan
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 * @since 2023-05-28
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private static final Logger LOGGER = Logger.getLogger(SecurityConfig.class.getName());
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Configure global security settings.
     * <p>
     * This method sets the userDetailsService and the passwordEncoder for the authentication manager.
     *
     * @param auth AuthenticationManagerBuilder
     * @throws Exception if an error occurs when adding the UserDetailsService
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
    }


    /**
     * Configure the security filter chain.
     * <p>
     * This method sets the request authorizations, form login configurations, and logout behavior.
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception if an error occurs when configuring the filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/", "/home", "/changeLanguage", "/register", "/guest", "/guest/**",
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
                        .loginProcessingUrl("/perform_login")
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
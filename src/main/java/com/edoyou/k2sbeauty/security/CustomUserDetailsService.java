package com.edoyou.k2sbeauty.security;

import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.entities.model.User;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of the UserDetailsService Spring Security interface.
 * It provides a way to obtain a user for Spring Security based on the email of the user.
 * <p>
 * This service is used in the authentication and authorization process, where it
 * is necessary to fetch the user's details given their username (in this case, their email).
 * <p>
 * This class works in the following way:
 * <ol>
 * <li> An instance of UserRepository is injected via Spring's dependency injection mechanism.</li>
 * <li> When the method loadUserByUsername is called with an email as argument:</li>
 * <ul>
 * <li> The method uses the UserRepository to fetch the User entity associated with the provided email.</li>
 * <li> If the User entity is found, the method extracts the user's roles and transforms them into GrantedAuthority objects.</li>
 * <li> The method then constructs and returns a User object (from Spring Security) which includes the user's email, password, and authorities.</li>
 * <li> If the User entity is not found, the method throws a UsernameNotFoundException.
 * </ul>
 * </ol>
 * <p>
 * It is important to note that the User entity here belongs to the application's domain model, while
 * the User object returned by the loadUserByUsername method is a part of Spring Security's model.
 *
 * @author Taghiyev Kanan
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @since 2023-05-28
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger LOGGER = Logger.getLogger(CustomUserDetailsService.class.getName());

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a user record containing the user's credentials and access.
     * <p>
     * This method first tries to fetch a User entity using the UserRepository.
     * If a User entity is found, it then gets the roles associated with the User entity,
     * converts them into GrantedAuthority objects and constructs a User object (from Spring Security)
     * with the user's email, password, and authorities, which it then returns.
     * If no User entity is found with the provided email, this method throws a UsernameNotFoundException.
     *
     * @param email the email identifying the user
     * @return a fully populated user record (never {@code null})
     * @throws UsernameNotFoundException if the user could not be found or the user has no authorities
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.info("Loading user with email: {" + email + "}");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found."));
        LOGGER.info(
                "Stored password for user with email {" + email + "}: {" + user.getPassword() + "}");

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                authorities);
    }
}
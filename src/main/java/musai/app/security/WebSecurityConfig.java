package musai.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import musai.app.security.jwt.AuthEntryPointJwt;
import musai.app.security.jwt.AuthTokenFilter;
import musai.app.security.services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig { 

    @Autowired
    UserDetailsServiceImpl userDetailsService; // Custom implementation of UserDetailsService

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler; // Custom handler for unauthorized access

    /**
     * Bean to provide the custom JWT token filter.
     * This filter will process every request and validate the JWT token if present.
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
    
    /**
     * Configures the authentication provider, which retrieves user details and encodes passwords.
     *
     * @return DaoAuthenticationProvider instance
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
         
        authProvider.setUserDetailsService(userDetailsService); // Use custom UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder()); // Use BCrypt for password hashing
     
        return authProvider;
    }
    
    /**
     * Configures the AuthenticationManager, which is responsible for authentication processes.
     *
     * @param authConfig AuthenticationConfiguration object
     * @return AuthenticationManager instance
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configures the PasswordEncoder for password hashing. BCrypt is used here.
     *
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Configures the security filter chain, defining how requests are authorized and authenticated.
     *
     * @param http HttpSecurity object for building security rules
     * @return SecurityFilterChain instance
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(unauthorizedHandler)) // Handle unauthorized access
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session management
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers("/api/auth/**").permitAll() // Allow access to authentication-related APIs
                    .requestMatchers("/api/test/**").permitAll() // Allow access to test-related APIs
                    .anyRequest().authenticated() // Require authentication for all other requests
            );

        // Register the authentication provider
        http.authenticationProvider(authenticationProvider());

        // Add the JWT filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

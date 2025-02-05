package musai.app.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
	 * Bean to provide the custom JWT token filter. This filter will process every
	 * request and validate the JWT token if present.
	 */
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	/**
	 * Configures the authentication provider, which retrieves user details and
	 * encodes passwords.
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
	 * Configures the AuthenticationManager, which is responsible for authentication
	 * processes.
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
	 * Configures the security filter chain, defining how requests are authorized
	 * and authenticated.
	 *
	 * @param http HttpSecurity object for building security rules
	 * @return SecurityFilterChain instance
	 * @throws Exception if configuration fails
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
				.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/api/test/**").permitAll().requestMatchers("/api/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated());

		// Add CORS filter
		http.addFilterBefore(corsFilterWeb(), UsernamePasswordAuthenticationFilter.class);
		
		// Register the authentication provider
		http.authenticationProvider(authenticationProvider());

		// Add the JWT filter before the UsernamePasswordAuthenticationFilter
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// Remove pre ROLE_
	@Bean
	GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults("");
	}

	// Define CORS configuration
	@Bean
	public CorsFilter corsFilterWeb() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("http://localhost:5173"); // Frontend URL
		config.addAllowedHeader("*");
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowCredentials(true);
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}

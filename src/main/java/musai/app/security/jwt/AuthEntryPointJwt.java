package musai.app.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom AuthenticationEntryPoint that handles unauthorized access attempts by
 * returning a JSON response.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

	/**
	 * This method is triggered anytime an unauthenticated user tries to access a
	 * resource that requires authentication.
	 *
	 * @param request       The incoming HTTP request.
	 * @param response      The HTTP response to be sent back to the client.
	 * @param authException The exception thrown when authentication fails.
	 * @throws IOException      If an I/O error occurs.
	 * @throws ServletException If a servlet-related error occurs.
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// Đặt các header CORS
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		// Set the response content type and status code
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		// Create a JSON body to include in the response
		final Map<String, Object> body = new HashMap<>();
		body.put("status", HttpServletResponse.SC_UNAUTHORIZED); // HTTP status code
		body.put("error", "Unauthorized"); // Error description
		body.put("message", authException.getMessage()); // Exception message
		body.put("path", request.getServletPath()); // The path that caused the error

		// Serialize the JSON body and write it to the response's output stream
		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), body);
	}
}

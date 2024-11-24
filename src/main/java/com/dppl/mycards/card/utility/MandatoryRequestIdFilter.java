package com.dppl.mycards.card.utility;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dppl.mycards.card.repository.UserRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MandatoryRequestIdFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LogManager.getLogger();

	private JwtValidator jwtValidator;

	public MandatoryRequestIdFilter(JwtValidator jwtValidator) {
		super();
		this.jwtValidator = jwtValidator;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		System.out.println("\n\n");
		LOGGER.info("Incoming Request: {} {}", request.getMethod(), request.getRequestURI());
		long startTime = System.currentTimeMillis();
		String requestId = request.getHeader("requestId");
		MDC.put(Keys.REQUEST_ID, requestId);
		
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			filterChain.doFilter(request, response);
            return;
        }

		if (isSwaggerOrPublicEndpoint(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}

		if (requestId == null) {
			LOGGER.error("Unauthorized request - Missing requestID");
			throw new RuntimeException("Add requestId header");
		}

//		String token = getJwtFromRequest(request);
//		if (token != null) {
//			Claims claims = jwtValidator.parseToken(token);
//			if (!claims.containsKey("userMobile")) {
//				LOGGER.error("Unauthorized request - Missing or invalid mobile claim in token");
//				throw new RuntimeException("Missing or invalid mobile claim");
//			}
//
//			// TODO: Add code to check for the expiry of the token also
//			
//			String mobile = claims.get("userMobile", String.class);
//			
//			if (!userRepository.existsByMobile(mobile)) {
//				LOGGER.error("Unauthorized request - Mobile number not found in the database");
//				throw new RuntimeException("Mobile number not found");
//			}
//		} else {
//			LOGGER.error("Unauthorized request - Please provide bearer token");
//			throw new RuntimeException("Missing Authorization header");
//		}
//
		if (! jwtValidator.isTokenValid(request)) {
			LOGGER.error("Unauthorized request - Missing authToken");
			throw new RuntimeException("Add Authorization Header");
		}

		try {
			filterChain.doFilter(request, response);
		} finally {
			long processTime = System.currentTimeMillis() - startTime;
			LOGGER.info("Request ID: {} - Response Status: {} - Processing Time: {} ms", requestId, response.getStatus(), processTime);
			MDC.remove(Keys.REQUEST_ID);
		}
	}

	private boolean isSwaggerOrPublicEndpoint(String uri) {
		return uri.equals("/swagger-ui.html") || uri.startsWith("/swagger") || uri.startsWith("/v3/api-docs") ||
		       uri.equals("/api/users/signup") || uri.equals("/api/users/signIn") ||
		       uri.equals("/api/partners/banks/signUp") || uri.equals("/api/partners/banks/signIn") ||
		       uri.equals("/api/users/password/reset") || uri.equals("/api/users/otp/generate-register") ||
		       uri.equals("/api/users/otp/generate-reset") || uri.equals("/api/users/otp/verify") ||
		       uri.equals("/api/partners/banks/password/reset") || uri.equals("/api/partners/banks/otp/generate") ||
		       uri.equals("/api/partners/banks/otp/verify") || uri.equals("/api/partners/banks/otp/generate-register") ||
		       uri.equals("/api/partners/banks/otp/generate-reset") || uri.equals("/api/partners/banks/otp/generate-login") ||
		       uri.startsWith("/api/users/otp") || uri.startsWith("/api-docs");
	}
}

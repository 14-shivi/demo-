package com.dppl.mycards.card.utility;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.dppl.mycards.card.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtValidator {
	
	private static final Logger LOGGER = LogManager.getLogger(JwtValidator.class);

	private String jwtSecretKey = "mycards-secret-key-12345678910-mycards-secret-key-12345678910";
	
	@Autowired
	private UserRepository userRepository;

	public Claims parseToken(String token) {
		Claims claims = null;

			try {
				Jws<Claims> signedClaims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
				claims = signedClaims.getPayload();
			} catch (Exception e) {
				
				e.printStackTrace();
			} 
	
		return claims;
	}

	private SecretKey getSigningKey() {
		byte[] keyBytes = jwtSecretKey.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	 public boolean isTokenValid(HttpServletRequest request) {
		 
		 String token = getJwtFromRequest(request);
		 
	        if (token == null) {
	            LOGGER.error("Unauthorized request - Please provide bearer token");
	            throw new RuntimeException("Invalid request: Missing Authorization header");
	        }

	        Claims claims = parseToken(token);
	        if (!claims.containsKey("userMobile")) {
	            LOGGER.error("Unauthorized request - Missing or invalid mobile claim in token");
	            throw new RuntimeException("Invalid token: Missing mobile number claim");
	        }

	        // TODO: Add code to check for the expiry of the token also

	        String mobile = claims.get("userMobile", String.class);
	        if (!userRepository.existsByMobile(mobile)) {
	            LOGGER.error("Unauthorized request - Mobile number not found in the database");
	            throw new RuntimeException("Invalid token: Invalid mobile number claim");
	        }

	        return true;
	 }
	 
	 private String getJwtFromRequest(HttpServletRequest request) {
			String bearerToken = request.getHeader("Authorization");
			if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
				return bearerToken.substring(7);
			}
			return null;
		}
}

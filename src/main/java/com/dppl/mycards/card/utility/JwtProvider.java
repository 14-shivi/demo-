package com.dppl.mycards.card.utility;



import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dppl.mycards.card.repository.model.Partner;
import com.dppl.mycards.card.repository.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {
	private String jwtSecretKey = "mycards-secret-key-12345678910-mycards-secret-key-12345678910";
	private long jwtExpirationTime = 1000 * 60 * 60 * 24 * 7;             // Set to one week
	private String jwtIssuerClaim = "mycards-service";
	private static final Logger LOGGER = LogManager.getLogger();
	
	public String generateToken(User user) {
		LOGGER.info("Generating token for user");
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationTime);
		return Jwts.builder()
				.signWith(getSigningKey())
				.issuer(jwtIssuerClaim)
				.issuedAt(new Date())
				.expiration(expiryDate)
				.claim("userMobile", user.getMobile())
				.compact();
	}
	
	private SecretKey getSigningKey() {
		byte[] keyBytes = jwtSecretKey.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(Partner partner) {
		LOGGER.info("Generating token for partner");
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationTime);
		return Jwts.builder()
				.signWith(getSigningKey())
				.issuer(jwtIssuerClaim)
				.issuedAt(new Date())
				.expiration(expiryDate)
				.claim("userMobile", partner.getMobile())
				.compact();
	}
}

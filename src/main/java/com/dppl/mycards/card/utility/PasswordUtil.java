package com.dppl.mycards.card.utility;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordUtil {
	
	private static final int ITERATIONS = 3;
    private static final int MEMORY = 65536;  // 64MB
    private static final int PARALLELISM = 1;
	
    private PasswordUtil() {
    	super();
    }
    
	public static String encodePassword(String password) {
		Argon2 argon2 = Argon2Factory.create();
		return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password.toCharArray());
	}
	
}

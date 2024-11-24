package com.dppl.mycards.card.utility;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service
public class AESUtil {

	private static final Logger LOGGER = LogManager.getLogger(AESUtil.class);
    private static final String SECRET_KEY = "mycards-secret-key-12345678910-mycards-secret-key-12345678910";
    private static final String SALT = "mycards-secret-salt-12345678910";

    public String encrypt(String strToEncrypt) {
    	LOGGER.info("RequestID: {} :: Encrypting data", MDC.get(Keys.REQUEST_ID));
    	
        try {

        	byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeySpec secretKey = getSecretKeySpec(SECRET_KEY, SALT);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));

            byte[] ivAndEncrypted = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, ivAndEncrypted, 0, iv.length);
            System.arraycopy(encrypted, 0, ivAndEncrypted, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(ivAndEncrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: ", e);
        }
    }

    public String decrypt(String strToDecrypt) {
        try {
            byte[] ivAndEncrypted = Base64.getDecoder().decode(strToDecrypt);

            byte[] iv = new byte[16];
            System.arraycopy(ivAndEncrypted, 0, iv, 0, iv.length);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            byte[] encrypted = new byte[ivAndEncrypted.length - iv.length];
            System.arraycopy(ivAndEncrypted, iv.length, encrypted, 0, encrypted.length);

            SecretKeySpec secretKey = getSecretKeySpec(SECRET_KEY, SALT);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting: ", e);
        }
    }

    private SecretKeySpec getSecretKeySpec(String password, String salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
}

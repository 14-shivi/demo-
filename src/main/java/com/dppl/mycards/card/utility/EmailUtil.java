package com.dppl.mycards.card.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailUtil {

	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String OTP_MAIL_TEXT = """
			{} is your OTP. Don't share OTP with anyone.
			- Team MyCards
			""";
	
    private final JavaMailSender mailSender;
    String password = "MyCards@001";

    public EmailUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
//        message.setFrom("vishwasransing@gmail.com");

        mailSender.send(message);
    }

	public void sendOtp(String email, String otp) {
		
		String text = OTP_MAIL_TEXT.replace("{}", otp);
		try {
			sendEmail(email, "OTP", text);
		}
		catch(Exception e) {
			LOGGER.error("Error occured while sending mail");
		}
		LOGGER.info("OTP sent successfully.");
	}
}

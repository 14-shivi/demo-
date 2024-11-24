package com.dppl.mycards.card.utility;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RequestValidator {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(RequestValidator.class.getName());

	private Map<String, String> entity;

	public RequestValidator(Map<String, String> entity) {
		this.entity = entity;
	}

	public RequestValidator hasString(String key) {
		if (entity.get(key) == null || entity.get(key).trim().equals(""))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " missing!");
		return this;
	}

	public RequestValidator hasEitherString(String key1, String key2) {
		if ((entity.get(key1) == null || entity.get(key1).trim().equals(""))
				&& (entity.get(key2) == null || entity.get(key2).trim().equals("")))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key1 + " or " + key2 + " both missing!");
		return this;
	}

	public RequestValidator hasLong(String key) {
		try {
			if (!entity.containsKey(key))
				throw new NullPointerException();
			Long.parseLong(entity.get(key));
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value for " + key + "!");
		} catch (NullPointerException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " missing!");
		}
		return this;
	}

	public RequestValidator hasIntegerId(String key) {
		try {
			if (!entity.containsKey(key))
				throw new NullPointerException();
			Integer.parseInt(entity.get(key));
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value for " + key + "!");
		} catch (NullPointerException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " missing!");
		}
		return this;
	}

	public RequestValidator hasLongId(String key) {
		try {
			if (!entity.containsKey(key))
				throw new NullPointerException();
			Long.parseLong(entity.get(key));
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value for " + key + "!");
		} catch (NullPointerException npe) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " missing!");
		}
		return this;
	}

	public RequestValidator hasEmail(String key) {
		if ((entity.get(key) == null || entity.get(key).trim().equals("")))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email missing");
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		boolean isMatched = pat.matcher(entity.get(key)).matches();
		if (isMatched || (entity.get(key) == null))
			return this;
		else if (entity.get(key) != null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format!");
		else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email missing!");
	}

	public RequestValidator hasPhoneNumber(String key) {
		if ((entity.get(key) == null || entity.get(key).trim().equals("")))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number missing");
		String phoneRegex = "^[1-9]\\d{9}$";
		Pattern pat = Pattern.compile(phoneRegex);
		boolean isMatched = pat.matcher(entity.get(key)).matches();
		if (isMatched || (entity.get(key) == null))
			return this;
		else if (entity.get(key) != null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number format!");
		else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number missing!");
	}

	public RequestValidator hasDate(String key) {
		if (entity.get(key) == null || entity.get(key).trim().equals(""))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " missing!");
		try {
			LocalDate.parse(entity.get(key));
		} catch (DateTimeParseException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format!");
		} catch(NullPointerException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is null!");
		}
		return this;
	}

	public RequestValidator hasKey(String key) {
		if (!entity.containsKey(key))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " missing!");
		return this;
	}

	public RequestValidator hasEitherKey(String key1, String key2) {
		if (!entity.containsKey(key1) && !entity.containsKey(key2))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key1 + " & " + key2 + " both missing!");
		return this;
	}

	public RequestValidator hasAnyKey(Set<String> keys) {
		boolean hasKey = false;
		for (String key : keys) {
			if (entity.containsKey(key)) {
				hasKey = true;
				break;
			}
		}
		if (!hasKey)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data missing!");
		return this;
	}
	
	public RequestValidator hasMobile(String key) {
	    if (entity.get(key) == null || entity.get(key).trim().equals("")) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile number missing!");
	    }
	    String mobileRegex = "^[1-9]\\d{9}$";
	    Pattern pattern = Pattern.compile(mobileRegex);
	    boolean isValid = pattern.matcher(entity.get(key)).matches();
	    if (!isValid) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mobile number format!");
	    }
	    
	    return this;
	}
	
	public RequestValidator hasOtp(String key) {
	    if (entity.get(key) == null || entity.get(key).trim().equals("")) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP missing!");
	    }
	    String otpRegex = "^\\d{6}$";
	    Pattern pattern = Pattern.compile(otpRegex);
	    boolean isValid = pattern.matcher(entity.get(key)).matches();
	    if (!isValid) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP format!");
	    }
	    
	    return this;
	}
	
	public RequestValidator hasPassword(String key) {
	    if (entity.get(key) == null || entity.get(key).trim().equals("")) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password missing!");
	    }
	    String passwordRegex = "^[a-zA-Z0-9@$&]{5,50}$";
	    Pattern pattern = Pattern.compile(passwordRegex);
	    boolean isValid = pattern.matcher(entity.get(key)).matches();
	    if (!isValid) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password format!");
	    }

	    return this;
	}

	public RequestValidator hasName(String key) {
	    if (entity.get(key) == null || entity.get(key).trim().equals("")) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name missing!");
	    }
	    String nameRegex = "^[a-zA-Z][a-z]{0,49}$";
	    Pattern pattern = Pattern.compile(nameRegex);
	    boolean isValid = pattern.matcher(entity.get(key)).matches();
	    if (!isValid) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid $ value format!".replace("$", key));
	    }

	    return this;
	}

	public RequestValidator hasBoolean(String key) {
	    if (entity.get(key) == null || entity.get(key).trim().equals("")) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " missing!");
	    }
	    String value = entity.get(key).trim().toLowerCase();
	    if (!value.equals("true") && !value.equals("false")) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid boolean value for " + key + "!");
	    }

	    return this;
	}

	public RequestValidator hasGender(String key) {
	    if (entity.get(key) == null || entity.get(key).trim().equals("")) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gender missing!");
	    }
	    String value = entity.get(key).trim().toLowerCase();
	    if (!value.equals("male") && !value.equals("female") && !value.equals("other")) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid gender value! Allowed values are 'male', 'female', or 'other'.");
	    }

	    return this;
	}

	public RequestValidator hasSalutation(String key) {
	    if (entity.get(key) == null || entity.get(key).trim().equals("")) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salutation missing!");
	    }
	    String value = entity.get(key).trim().toLowerCase();
	    if (!value.equals("mr") && !value.equals("mrs")) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid salutation! Allowed values are 'mr' or 'mrs'.");
	    }

	    return this;
	}

	public RequestValidator hasCardNumber(String key) {
	    if (entity.get(key) == null || entity.get(key).trim().equals("")) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card number missing!");
	    }

	    String cardNumberRegex = "^[0-9]{16}$";
	    Pattern pattern = Pattern.compile(cardNumberRegex);
	    boolean isValid = pattern.matcher(entity.get(key)).matches();

	    if (!isValid) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid card number format! Must be exactly 16 digits.");
	    }

	    return this;
	}

}
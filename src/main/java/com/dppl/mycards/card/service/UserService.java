package com.dppl.mycards.card.service;

import java.util.Map;

import com.dppl.mycards.card.repository.model.User;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.utility.OtpContext;

public interface UserService {
	
	public User createUser(RequestDTO requestDTO);
	
	public Map<String, Object> loginUser(RequestDTO requestDTO);
	
	public Map<String, Object> getUserById(Long id);

	public User updateUser(RequestDTO requestDTO);
	
	public void deleteUser(Long id);
	
	public String generateOtp(RequestDTO requestDTO, OtpContext otpContext);
	
	public boolean verifyOtp(RequestDTO requestDTO);
	
	public String forgotPassword(RequestDTO requestDTO);

}

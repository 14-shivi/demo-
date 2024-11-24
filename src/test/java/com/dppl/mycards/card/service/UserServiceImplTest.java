package com.dppl.mycards.card.service;

import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateRequestDTO;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateUserRequestAttributes;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateUserUsingAttributes;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dppl.mycards.card.exception.NotFoundException;
import com.dppl.mycards.card.repository.OtpDetailRepository;
import com.dppl.mycards.card.repository.UserActivityRepository;
import com.dppl.mycards.card.repository.UserDetailsRepository;
import com.dppl.mycards.card.repository.UserRepository;
import com.dppl.mycards.card.repository.model.OtpDetail;
import com.dppl.mycards.card.repository.model.User;
import com.dppl.mycards.card.repository.model.UserDetails;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.service.dto.RequestData;
import com.dppl.mycards.card.service.impl.UserServiceImpl;
import com.dppl.mycards.card.utility.JwtProvider;
import com.dppl.mycards.card.utility.Keys;
import com.dppl.mycards.card.utility.OtpContext;
import com.dppl.mycards.card.utility.PasswordUtil;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private UserServiceImpl userServiceImpl;
	@Mock
	private UserActivityService userActivityService;
	@Mock
	private UserDetailsRepository userDetailsRepository;
	@Mock
	private UserActivityRepository userActivityRepository;
	@Mock
	private OtpDetailRepository otpDetailRepository;
	@Mock
	private JwtProvider jwtProvider;
	
	private User user;
	@Mock
	private OtpDetail otpDetail;
	@Mock
	private Argon2 argon2;
	
	@InjectMocks
	private UserServiceImpl userService;
	
	@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        otpDetail = OtpDetail.builder()
        		.emailOtp("123456")
        		.mobileOtp("123456")
        		.build();
        argon2 = Argon2Factory.create();
    }

	@Test
    void createUserTest() {
        Map<String, String> attributes = generateUserRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(attributes);
        User user = new User();
        user.setEmail(attributes.get(Keys.USER_EMAIL));
        user.setMobile(attributes.get(Keys.USER_MOBILE));

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
        when(userRepository.findByMobile(any(String.class))).thenReturn(Optional.empty());

        otpDetail.setExpiryTimestamp(LocalDateTime.now().plusMinutes(4));

        when(otpDetailRepository.findByEmail(any(String.class))).thenReturn(Optional.of(otpDetail));

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(requestDTO);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getPassword());
        assertNotEquals(createdUser.getPassword(), attributes.get("password")); // Check encoded password
    }

	@Test
	void loginUserTest() {
		String token = "aksjhdaklhsjdnkasjd";
        
		Map<String, String> attributes = generateUserRequestAttributes();
    	RequestDTO requestDTO = generateRequestDTO(attributes);
    	requestDTO.setData(new RequestData<>("1212", "user", attributes));
        
    	User loginUser = generateUserUsingAttributes(attributes);
    	
    	user = new User();
    	user.setUserId(1L);
    	user.setEmail(loginUser.getEmail());
    	user.setMobile(loginUser.getMobile());
    	user.setPassword(PasswordUtil.encodePassword(loginUser.getPassword()));

    	when(userRepository.findByEmail(loginUser.getEmail())).thenReturn(Optional.of(loginUser));
        when(jwtProvider.generateToken(loginUser)).thenReturn(token);
        when(userRepository.findByMobile(loginUser.getMobile())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(loginUser.getEmail())).thenReturn(Optional.of(user));
        when(userService.getRegisteredUserFromDB(loginUser)).thenReturn(Optional.of(user));
        
        otpDetail.setEmail(user.getEmail());
        otpDetail.setMobile(user.getMobile());
        otpDetail.setExpiryTimestamp(LocalDateTime.now().plusMinutes(4));
        when(otpDetailRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(otpDetail));
        when(jwtProvider.generateToken(user)).thenReturn(token);
        
        Map<String, Object> loginResponseAttributes = userService.loginUser(requestDTO);
   	 
    	String generatedToken = (String) loginResponseAttributes.get("token");
        
        assertNotNull(generatedToken);
        assertEquals(token, generatedToken);
    }
	
    	
    @Test
    void getUserByIdTest_Success() {
    	user = new User();
    	user.setEmail("vishwasransingh@gmail.com");
    	UserDetails userDetails = new UserDetails();
    	userDetails.setFirstName("vishwas-test");
    	
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(userDetailsRepository.findById(any(Long.class))).thenReturn(Optional.of(userDetails));
        
        Map<String, Object> userResponseAttributes = userService.getUserById(1L);

        assertNotNull(userResponseAttributes);
        assertEquals("vishwasransingh@gmail.com", userResponseAttributes.get(Keys.USER_EMAIL));
    }

    @Test
    void getUserByIdTest_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUserTest_Success() {
    	
    	Map<String, String> attributes = generateUserRequestAttributes();
    	RequestDTO requestDTO = generateRequestDTO(attributes);
    	
    	user = new User();
    	user.setEmail(attributes.get(Keys.USER_EMAIL));
    	user.setMobile(attributes.get(Keys.USER_MOBILE));
    	user.setPassword(PasswordUtil.encodePassword(Keys.USER_PASSWORD));
    	
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findByMobile(user.getMobile())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
		when(userService.getRegisteredUserFromDB(user)).thenReturn(Optional.of(user));
		user.setPassword(PasswordUtil.encodePassword(user.getPassword()));
        
        User updatedUser = userService.updateUser(requestDTO);
        
        assertNotNull(updatedUser.getUpdatedTimestamp());
    }

    @Test
    void deleteUserTest_Success() {
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
    }

    @Test
    void generateOtpTest_Success() {
    	Map<String, String> attributes = generateUserRequestAttributes();
    	RequestDTO requestDTO = generateRequestDTO(attributes);
    	
    	user = new User();
    	user.setEmail(attributes.get(Keys.USER_EMAIL));
    	user.setMobile(attributes.get(Keys.USER_MOBILE));
    	
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userService.getRegisteredUserFromDB(user)).thenReturn(Optional.of(user));
        
        String otp = userService.generateOtp(requestDTO, OtpContext.REGISTRATION);

        assertNotNull(otp);
    }

    @Test
    void verifyOtpTest_Success() {
    	Map<String, String> attributes = generateUserRequestAttributes();
    	RequestDTO requestDTO = generateRequestDTO(attributes);
    	
    	user = new User();
    	user.setEmail(attributes.get(Keys.USER_EMAIL));
    	user.setMobile(attributes.get(Keys.USER_MOBILE));
    	user.setPassword(PasswordUtil.encodePassword(Keys.USER_PASSWORD));
    	
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findByMobile(user.getMobile())).thenReturn(Optional.of(user));
        when(userService.getRegisteredUserFromDB(user)).thenReturn(Optional.of(user));
        otpDetail = OtpDetail.builder()
		.emailOtp("123456")
		.mobileOtp("123456")
		.expiryTimestamp(LocalDateTime.now().plusMinutes(2))
		.build();
		when(otpDetailRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(otpDetail));

        boolean isVerified = userService.verifyOtp(requestDTO);

        assertTrue(isVerified);
    }

    @Test
    void forgotPasswordTest_InvalidOtpException() {
    	
    	Map<String, String> attributes = generateUserRequestAttributes();	
    	RequestDTO requestDTO = generateRequestDTO(attributes);
    	
    	user = new User();
    	user.setEmail(attributes.get(Keys.USER_EMAIL));
    	user.setMobile(attributes.get(Keys.USER_MOBILE));
    	
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findByMobile(user.getMobile())).thenReturn(Optional.of(user));
        when(userService.getRegisteredUserFromDB(user)).thenReturn(Optional.of(user));
        when(otpDetailRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(
        		OtpDetail.builder()
                .userId(1L)
                .emailOtp("123456")
                .mobileOtp("123456")
                .createdTimestamp(LocalDateTime.now().minusMinutes(2))
                .expiryTimestamp(LocalDateTime.now().plusMinutes(2))
                .createdBy("vishwas-dev")
                .updatedBy("vishwas-dev")
                .build()));

        String token = userService.forgotPassword(requestDTO);

        assertNotNull(token);
    }
	
}

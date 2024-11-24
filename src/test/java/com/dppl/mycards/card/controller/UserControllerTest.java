package com.dppl.mycards.card.controller;

import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateRequestDTO;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateUserRequestAttributes;
import static com.dppl.mycards.card.util.RequestResponseDataGenerationUtil.generateUserResponseAttributes;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dppl.mycards.card.repository.model.User;
import com.dppl.mycards.card.service.UserService;
import com.dppl.mycards.card.service.dto.RequestDTO;
import com.dppl.mycards.card.utility.JwtValidator;
import com.dppl.mycards.card.utility.OtpContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtValidator jwtValidator;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        Mockito.when(jwtValidator.isTokenValid(any(HttpServletRequest.class))).thenReturn(true);

    }

    @Test
    public void createUserTest() {
        RequestDTO requestDTO = new RequestDTO();
        User user = new User();
        
        when(userService.createUser(requestDTO)).thenReturn(user);
        
        try {
			mockMvc.perform(post("/api/users/signup")
			        .contentType(MediaType.APPLICATION_JSON)
			        .content(objectMapper.writeValueAsString(requestDTO)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.data").exists())
			        .andExpect(jsonPath("$.data.password").doesNotExist());
//					.andDo(print());
		} catch (Exception e) {
			e.printStackTrace();
		}
//        verify(userService, times(1)).createUser(requestDTO);
    }

    @Test
    public void retrieveUserTest() {
        User user = new User();
        user.setUserId(1L);
        user.setPassword("password");
        
        Map<String, Object> userResponseAttributes = generateUserResponseAttributes(user);
        
        when(userService.getUserById(1L)).thenReturn(userResponseAttributes);

        try {
			mockMvc.perform(get("/api/users/1"))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.data").exists())
			        .andExpect(jsonPath("$.data.password").doesNotExist());
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }

    @Test
    public void updateUserTest() {
        RequestDTO requestDTO = new RequestDTO();
        User user = new User();
        user.setUserId(1L);

        when(userService.updateUser(requestDTO)).thenReturn(user);

        try {
			mockMvc.perform(put("/api/users/profile")
			        .contentType(MediaType.APPLICATION_JSON)
			        .content(objectMapper.writeValueAsString(requestDTO)))
			        .andExpect(status().isOk());
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    @Test
    public void deleteUserTest() {
        try {
			mockMvc.perform(delete("/api/users/1"))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.data.attributes.Status").value("Success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }

    @Test
    public void loginTest() {
        RequestDTO requestDTO = new RequestDTO();
        String token = "asdfghjkl";
        
        Map<String, Object> loginResponseAttributes = Map.of("token", token, "userId", "");
        
        when(userService.loginUser(requestDTO)).thenReturn(loginResponseAttributes);

        try {
			mockMvc.perform(post("/api/users/signIn")
			        .contentType(MediaType.APPLICATION_JSON)
			        .content(objectMapper.writeValueAsString(requestDTO)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.data.attributes.token").value(token));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }

    @Test
    public void generateOtpForNewRegistrationTest() {
        RequestDTO requestDTO = new RequestDTO();
        String otp = "1212";
        
        when(userService.generateOtp(requestDTO, OtpContext.REGISTRATION)).thenReturn(otp);

        try {
			mockMvc.perform(post("/api/users/otp/generate-register")
			        .contentType(MediaType.APPLICATION_JSON)
			        .content(objectMapper.writeValueAsString(requestDTO)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.data.attributes.OTP").value(otp));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }
    
    @Test
    public void generateOtpForLoginTest() {
        RequestDTO requestDTO = new RequestDTO();
        String otp = "1212";
        
        when(userService.generateOtp(requestDTO, OtpContext.LOGIN)).thenReturn(otp);

        try {
			mockMvc.perform(post("/api/users/otp/generate-login")
			        .contentType(MediaType.APPLICATION_JSON)
			        .content(objectMapper.writeValueAsString(requestDTO)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.data.attributes.OTP").value(otp));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }
    
    @Test
    public void generateOtpForPasswordResetTest() {
        RequestDTO requestDTO = new RequestDTO();
        String otp = "1212";
        
        when(userService.generateOtp(requestDTO, OtpContext.PASSWORD_RESET)).thenReturn(otp);

        try {
			mockMvc.perform(post("/api/users/otp/generate-reset")
			        .contentType(MediaType.APPLICATION_JSON)
			        .content(objectMapper.writeValueAsString(requestDTO)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.data.attributes.OTP").value(otp));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }

    @Test
    public void verifyOtpTest() {
        RequestDTO requestDTO = new RequestDTO();
        
        when(userService.verifyOtp(requestDTO)).thenReturn(true);
        
        try {
			mockMvc.perform(post("/api/users/otp/verify")
			        .contentType(MediaType.APPLICATION_JSON)
			        .content(objectMapper.writeValueAsString(requestDTO)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.data.attributes.isVerified").value("true"));
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    @Test
    public void forgotPasswordTest() throws Exception {
    	Map<String, String> attributes = generateUserRequestAttributes();
        RequestDTO requestDTO = generateRequestDTO(attributes);
        String tempToken = "qwertyuiop";
        
        when(userService.forgotPassword(requestDTO)).thenReturn(tempToken);

        mockMvc.perform(post("/api/users/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.attributes.token").value(tempToken));
    }
    
}

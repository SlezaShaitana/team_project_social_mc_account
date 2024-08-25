package com.social.mc_account;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.mc_account.controller.ApiController;
import com.social.mc_account.controller.GlobalExceptionHandler;
import com.social.mc_account.dto.*;
import com.social.mc_account.exception.ResourceNotFoundException;
import com.social.mc_account.kafka.KafkaProducer;
import com.social.mc_account.mapper.AccountMapper;
import com.social.mc_account.model.Account;
import com.social.mc_account.repository.AccountRepository;
import com.social.mc_account.security.JwtUtils;
import com.social.mc_account.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
public class ApiControllerTest {

    @Mock
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper mapper;

    @Mock
    private KafkaProducer producer;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private ApiController apiController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(apiController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getDataAccount_Success() throws Exception {
        String email = "test@mail.ru";
        String authorization = "bearer token";
        AccountDataDTO accountDataDTO = new AccountDataDTO();

        given(accountService.getDataAccount(authorization, email)).willReturn(accountDataDTO);

        mockMvc.perform(get("/api/v1/account")
                        .header("Authorization", authorization)
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(accountDataDTO)));
    }

    @Test
    void getDataAccount_NotFound() throws Exception {
        String email = "test@mail.ru";
        String authorization = "bearer token";

        doThrow(new ResourceNotFoundException("The account with email: " + email + " not found"))
                .when(accountService).getDataAccount(authorization, email);

        mockMvc.perform(get("/api/v1/account")
                        .header("Authorization", authorization)
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateDataAccount_Success() throws Exception {
        AccountMeDTO accountMeDTO = new AccountMeDTO();
        accountMeDTO.setEmail("test@mail.ru");

        given(accountService.updateAccount(accountMeDTO)).willReturn(accountMeDTO);

        mockMvc.perform(put("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountMeDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(accountMeDTO)));
    }

    @Test
    void createAccount_Success() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setEmail("test@mail.ru");

        AccountMeDTO accountMeDTO = new AccountMeDTO();
        accountMeDTO.setEmail("test@mail.ru");

        when(accountService.createAccount(any(RegistrationDto.class))).thenReturn(accountMeDTO);

        mockMvc.perform(post("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(accountMeDTO)));
    }

    @Test
    void getDataMyAccount_Success() throws Exception {
        UUID accountId = UUID.randomUUID();
        String authorization = "bearer token";
        AccountMeDTO accountMeDTO = new AccountMeDTO();
        accountMeDTO.setEmail("test@mail.ru");

        given(accountService.getDataMyAccount(authorization)).willReturn(accountMeDTO);

        mockMvc.perform(get("/api/v1/account/me")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(accountMeDTO)));
    }

    @Test
    void getDataMyAccount_NotFound() throws Exception {
        String authorization = "bearer token";
        UUID accountId = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("The account with id: " + accountId + " not found"))
                .when(accountService).getDataMyAccount(authorization);

        mockMvc.perform(get("/api/v1/account/me")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateDataMyAccount_Success() throws Exception {
        UUID accountId = UUID.randomUUID();
        String authorization = "bearer token";
        AccountMeDTO requestDTO = new AccountMeDTO();
        requestDTO.setEmail("updated@mail.ru");

        AccountMeDTO responseDTO = new AccountMeDTO();
        responseDTO.setEmail("updated@mail.ru");


        given(accountService.updateAuthorizeAccount(authorization, requestDTO)).willReturn(responseDTO);

        mockMvc.perform(put("/api/v1/account/me")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDTO)));
    }

    @Test
    void deleteMyAccount_Success() throws Exception {
        String authorization = "bearer token";

        doNothing().when(accountService).deleteAccount(authorization);

        mockMvc.perform(delete("/api/v1/account/me")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteMyAccount_NotFound() throws Exception {
        String authorization = "bearer token";

        doThrow(new ResourceNotFoundException("Account not found")).when(accountService).deleteAccount(authorization);

        mockMvc.perform(delete("/api/v1/account/me")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }



    @Test
    void putNotificationsForFriends_Success() throws Exception {
        doNothing().when(accountService).putNotification();

        mockMvc.perform(put("/api/v1/account/birthdays"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getDataMyAccountById_Success() throws Exception {
        UUID accountId = UUID.randomUUID();
        AccountMeDTO accountMeDTO = new AccountMeDTO();
        accountMeDTO.setEmail("test@mail.ru");

        given(accountService.getDataById(accountId)).willReturn(accountMeDTO);

        mockMvc.perform(get("/api/v1/account/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(accountMeDTO)));
    }

    @Test
    void getDataMyAccountById_NotFound() throws Exception {
        UUID accountId = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("Account not found")).when(accountService).getDataById(accountId);

        mockMvc.perform(get("/api/v1/account/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteMyAccountById_Success() throws Exception {
        UUID accountId = UUID.randomUUID();

        doNothing().when(accountService).deleteAccountById(accountId);

        mockMvc.perform(delete("/api/v1/account/{id}", accountId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void deleteMyAccountById_NotFound() throws Exception {
        UUID accountId = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("Account not found")).when(accountService).deleteAccountById(accountId);

        mockMvc.perform(delete("/api/v1/account/{id}", accountId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getStatistic_Success() throws Exception {
        StatisticRequestDTO requestDTO = new StatisticRequestDTO();
        requestDTO.setDate(LocalDate.now());
        requestDTO.setFirstMonth(LocalDate.now().minusMonths(1));
        requestDTO.setLastMonth(LocalDate.now());

        StatisticDTO statisticDTO = new StatisticDTO();
        statisticDTO.setCount(10);
        statisticDTO.setCountPerAgeDTO(new CountPerAgeDTO());
        statisticDTO.setCountPerMonthDTO(new CountPerMonthDTO());

        when(accountService.getStatistic(requestDTO)).thenReturn(statisticDTO);

        mockMvc.perform(get("/api/v1/account/statistic")
                        .queryParam("date", requestDTO.getDate().toString())
                        .queryParam("firstMonth", requestDTO.getFirstMonth().toString())
                        .queryParam("lastMonth", requestDTO.getLastMonth().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(statisticDTO)));
    }
}
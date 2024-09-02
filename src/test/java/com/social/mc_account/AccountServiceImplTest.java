package com.social.mc_account;

import com.social.mc_account.dto.*;
import com.social.mc_account.dto.Page;
import com.social.mc_account.exception.ResourceNotFoundException;
import com.social.mc_account.feign.StorageClient;
import com.social.mc_account.kafka.KafkaProducer;
import com.social.mc_account.mapper.AccountMapper;
import com.social.mc_account.model.Account;
import com.social.mc_account.repository.AccountRepository;
import com.social.mc_account.security.JwtUtils;
import com.social.mc_account.service.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private StorageClient storageClient;

    @Mock
    private AccountMapper mapper;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    @DisplayName("Test getDataAccount")
    public void testGetDataAccount() {
        String email = "test@gmail.ru";
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .firstName("Tester")
                .lastName("App")
                .password("12345678")
                .role(Role.USER)
                .regDate(LocalDate.now())
                .isDeleted(false)
                .email(email)
                .build();

        AccountDataDTO accountDataDTO = AccountDataDTO.builder()
                .id(account.getId())
                .firstName(account.getFirstName())
                .email(account.getEmail())
                .isDeleted(account.isDeleted())
                .role(account.getRole().name())
                .build();

        when(accountRepository.findByEmail(email)).thenReturn(account);
        when(mapper.toAccountDataDtoFromAccount(account)).thenReturn(accountDataDTO);

        AccountDataDTO result = accountService.getDataAccount(null, email);

        assertEquals(email, result.getEmail());
        assertEquals(account.getFirstName(), result.getFirstName());
        verify(accountRepository, times(1)).findByEmail(email);
        verify(mapper, times(1)).toAccountDataDtoFromAccount(account);
    }

   @Test
    @DisplayName("Test updateAuthorizeAccount")
    public void testUpdateAuthorizeAccount() {
        String authorization = "Bearer mockJwtToken";
        UUID accountId = UUID.randomUUID();
        String email = "old_email@gmail.com";

        AccountMeDTO accountMeDTO = AccountMeDTO.builder()
                .firstName("UpdatedName")
                .lastName("UpdatedLastName")
                .build();

        Account existingAccount = Account.builder()
                .id(accountId)
                .firstName("OldName")
                .lastName("OldLastName")
                .email(email)
                .password("existingPassword")
                .role(Role.USER)
                .isDeleted(false)
                .regDate(LocalDate.now().minusDays(30))
                .build();

        Account updatedAccount = Account.builder()
                .id(accountId)
                .firstName(accountMeDTO.getFirstName())
                .lastName(accountMeDTO.getLastName())
                .email(email)
                .password(existingAccount.getPassword())
                .role(existingAccount.getRole())
                .isDeleted(existingAccount.isDeleted())
                .regDate(existingAccount.getRegDate())
                .updateOn(LocalDateTime.now())
                .build();

        AccountMeDTO returnedAccountMeDTO = AccountMeDTO.builder()
                .id(accountId)
                .firstName("UpdatedName")
                .lastName("UpdatedLastName")
                .email(email)
                .role(Role.USER)
                .build();


        MultipartFile file = mock(MultipartFile.class);
        //when(file.isEmpty()).thenReturn(true);

        when(jwtUtils.getId(authorization)).thenReturn(accountId.toString());
        when(jwtUtils.getEmail(authorization)).thenReturn(email);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(mapper.toAccountFromAccountMeDto(accountMeDTO)).thenReturn(updatedAccount);
        when(mapper.toAccountMeDtoForAccount(updatedAccount)).thenReturn(returnedAccountMeDTO);
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        AccountMeDTO result = accountService.updateAuthorizeAccount(authorization, accountMeDTO);

        assertNotNull(result);
        assertEquals(accountMeDTO.getFirstName(), result.getFirstName());
        assertEquals(accountMeDTO.getLastName(), result.getLastName());

        verify(jwtUtils, times(1)).getId(authorization);
        verify(jwtUtils, times(1)).getEmail(authorization);
        verify(accountRepository, times(1)).findById(accountId);
        verify(mapper, times(1)).toAccountFromAccountMeDto(accountMeDTO);
        verify(mapper, times(1)).toAccountMeDtoForAccount(updatedAccount);
        verify(accountRepository, times(1)).save(updatedAccount);

        verify(kafkaProducer, never()).sendMessageForAuth(any(RegistrationDto.class));
    }




    @Test
    @DisplayName("Test createAccount")
    public void testCreateAccount() {
        UUID accountId = UUID.randomUUID();
        String email = "new_account@gmail.com";
        Role role = Role.USER;

        RegistrationDto accountDtoRequest = RegistrationDto.builder()
                .uuid(accountId)
                .email(email)
                .role(role)
                .build();

        Account account = Account.builder()
                .id(accountId)
                .email(email)
                .role(role)
                .build();

        AccountMeDTO accountMeDTO = AccountMeDTO.builder()
                .id(accountId)
                .email(email)
                .firstName("New")
                .lastName("Account")
                .role(role)
                .build();

        when(accountRepository.save(account)).thenReturn(account);
        when(mapper.toAccountMeDtoForAccount(account)).thenReturn(accountMeDTO);

        AccountMeDTO result = accountService.createAccount(accountDtoRequest);

        assertEquals(accountMeDTO, result);
        verify(accountRepository, times(1)).save(account);
        verify(mapper, times(1)).toAccountMeDtoForAccount(account);
    }

    @Test
    @DisplayName("Test getDataMyAccount is Found")
    public void testGetDataMyAccount() {
        String authorization = "Bearer some-valid-jwt-token";
        UUID id = UUID.randomUUID();
        Account account = Account.builder()
                .id(id)
                .firstName("Test")
                .lastName("Account")
                .email("test@gmail.com")
                .role(Role.USER)
                .build();

        AccountMeDTO accountMeDTO = AccountMeDTO.builder()
                .id(id)
                .firstName("Test")
                .lastName("Account")
                .email("test@gmail.com")
                .role(Role.USER)
                .build();

        when(jwtUtils.getId(authorization)).thenReturn(String.valueOf(UUID.fromString(id.toString())));
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(mapper.toAccountMeDtoForAccount(account)).thenReturn(accountMeDTO);

        AccountMeDTO result = accountService.getDataMyAccount(authorization);

        assertEquals(accountMeDTO, result);
        verify(jwtUtils, times(1)).getId(authorization);
        verify(accountRepository, times(1)).findById(id);
        verify(mapper, times(1)).toAccountMeDtoForAccount(account);
    }

    @Test
    @DisplayName("Test getDataMyAccount not found")
    public void testGetDataMyAccount_NotFound() {
        String authorization = "Bearer some-valid-jwt-token";
        UUID id = UUID.randomUUID();

        when(jwtUtils.getId(authorization)).thenReturn(String.valueOf(UUID.fromString(id.toString())));
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getDataMyAccount(authorization);
        });

        assertEquals("The account with id: " + id + " not found", exception.getMessage());
        verify(jwtUtils, times(1)).getId(authorization);
        verify(accountRepository, times(1)).findById(id);
        verify(mapper, never()).toAccountMeDtoForAccount(any(Account.class));
    }

 @Test
    @DisplayName("Test updateAuthorizeAccount when account is found and image is uploaded")
    public void testUpdateAuthorizeAccount_Found() {
        String authorization = "Bearer some-valid-jwt-token";
        UUID accountId = UUID.randomUUID();
        String updatedEmail = "updated_email@gmail.com";

        AccountMeDTO accountMeDTO = AccountMeDTO.builder()
                .id(accountId)
                .firstName("UpdatedName")
                .lastName("UpdatedLastName")
                .email(updatedEmail)
                .role(Role.USER)
                .build();

        Account existingAccount = Account.builder()
                .id(accountId)
                .firstName("OldName")
                .lastName("OldLastName")
                .email("old_email@gmail.com")
                .role(Role.USER)
                .photo("http://old-url.com/old-image.jpg")
                .build();

        Account updatedAccount = Account.builder()
                .id(accountId)
                .firstName(accountMeDTO.getFirstName())
                .lastName(accountMeDTO.getLastName())
                .email(updatedEmail)
                .role(accountMeDTO.getRole())
                .photo("http://image-url.com/image.jpg")
                .build();

        RegistrationDto accountDtoRequest = RegistrationDto.builder()
                .uuid(updatedAccount.getId())
                .email(updatedEmail)
                .role(updatedAccount.getRole())
                .build();


        //MultipartFile file = mock(MultipartFile.class);
        //String imageUrl = "http://image-url.com/image.jpg";

        when(jwtUtils.getId(authorization)).thenReturn(String.valueOf(accountId));
        when(jwtUtils.getEmail(authorization)).thenReturn(updatedEmail);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(mapper.toAccountFromAccountMeDto(accountMeDTO)).thenReturn(updatedAccount);
        //when(storageClient.pathForImage(file)).thenReturn(imageUrl);
        when(accountRepository.save(updatedAccount)).thenReturn(updatedAccount);
        when(mapper.toAccountMeDtoForAccount(updatedAccount)).thenReturn(accountMeDTO);

        AccountMeDTO result = accountService.updateAuthorizeAccount(authorization, accountMeDTO);

        assertEquals(accountMeDTO, result);
        //assertEquals(imageUrl, updatedAccount.getPhoto());
        verify(jwtUtils, times(1)).getId(authorization);
        verify(jwtUtils, times(1)).getEmail(authorization);
        verify(accountRepository, times(1)).findById(accountId);
        verify(mapper, times(1)).toAccountFromAccountMeDto(accountMeDTO);
        //verify(storageClient, times(1)).pathForImage(file);
        verify(accountRepository, times(1)).save(updatedAccount);
        verify(kafkaProducer, times(1)).sendMessageForAuth(accountDtoRequest);
        verify(mapper, times(1)).toAccountMeDtoForAccount(updatedAccount);
    }


    @Test
    @DisplayName("Test deleteAccount when account is found and softly deleted")
    public void testDeleteAccount_Found() {
        String authorization = "Bearer some-valid-jwt-token";
        UUID accountId = UUID.randomUUID();

        Account account = Account.builder()
                .id(accountId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.USER)
                .isDeleted(false)
                .build();

        when(jwtUtils.getId(authorization)).thenReturn(String.valueOf(UUID.fromString(accountId.toString())));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.deleteAccount(authorization);

        assertTrue(account.isDeleted());
        verify(jwtUtils, times(1)).getId(authorization);
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(account);

        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    @DisplayName("Test deleteAccount when account is not found")
    public void testDeleteAccount_NotFound()  {
        String authorization = "Bearer some-valid-jwt-token";
        UUID accountId = UUID.randomUUID();

        when(jwtUtils.getId(authorization)).thenReturn(String.valueOf(UUID.fromString(accountId.toString())));
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.deleteAccount(authorization);
        });

        assertEquals("The account with id: " + accountId + " not found", exception.getMessage());
        verify(jwtUtils, times(1)).getId(authorization);
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, never()).save(any(Account.class));
        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    @DisplayName("Test putNotification when there are accounts with birthdays")
    public void testPutNotification_WithBirthdays() {
        LocalDate today = LocalDate.now();
        Account account1 = Account.builder()
                .id(UUID.randomUUID())
                .birthDate(today)
                .build();
        Account account2 = Account.builder()
                .id(UUID.randomUUID())
                .birthDate(today)
                .build();

        List<Account> accounts = Arrays.asList(account1, account2);

        when(accountRepository.findAll()).thenReturn(accounts);

        accountService.putNotification();

        ArgumentCaptor<NotificationDTO> notificationCaptor = ArgumentCaptor.forClass(NotificationDTO.class);
        verify(kafkaProducer, times(2)).sendMessageForNotification(notificationCaptor.capture());

        List<NotificationDTO> capturedNotifications = notificationCaptor.getAllValues();
        assertEquals(2, capturedNotifications.size());

        for (NotificationDTO notification : capturedNotifications) {
            assertEquals(NotificationType.BIRTHDAY, notification.getNotificationType());
            assertEquals("С Днём Рождения!", notification.getContent());
            assertEquals(MicroServiceName.ACCOUNT, notification.getServiceName());
            assertFalse(notification.getIsReaded());
        }
    }

    @Test
    @DisplayName("Test putNotification when there are no accounts with birthdays")
    public void testPutNotification_NoBirthdays() {
        List<Account> accounts = Collections.emptyList();
        when(accountRepository.findAll()).thenReturn(accounts);

        accountService.putNotification();

        verify(kafkaProducer, never()).sendMessageForNotification(any(NotificationDTO.class));
    }

    //изменить метод
    @Test
    @DisplayName("Test getDataById when account is found")
    public void testGetDataById_Found() {
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder()
                .id(accountId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.USER)
                .build();

        AccountMeDTO accountMeDTO = AccountMeDTO.builder()
                .id(accountId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.USER)
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(mapper.toAccountMeDtoForAccount(account)).thenReturn(accountMeDTO);

        AccountMeDTO result = accountService.getDataById(accountId);

        assertEquals(accountMeDTO, result);
        verify(accountRepository, times(1)).findById(accountId);
        verify(mapper, times(1)).toAccountMeDtoForAccount(account);
    }

    @Test
    @DisplayName("Test getDataById when account is not found")
    public void testGetDataById_NotFound() {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getDataById(accountId);
        });

        assertEquals("The account with id: " + accountId + " not found", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(mapper, never()).toAccountDataDtoFromAccount(any(Account.class));
    }

    @Test
    @DisplayName("Test deleteAccountById when account is found")
    public void testDeleteAccountById_Found() {
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder()
                .id(accountId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.USER)
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.deleteAccountById(accountId);

        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    @DisplayName("Test deleteAccountById when account is not found")
    public void testDeleteAccountById_NotFound() {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.deleteAccountById(accountId);
        });

        assertEquals("The account with id: " + accountId + " not found", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    @DisplayName("Test getStatistic with valid data")
    public void testGetStatistic() {
        LocalDate birthDate = LocalDate.of(1990, 8, 13);
        LocalDate firstMonth = LocalDate.of(2023, 1, 1);
        LocalDate lastMonth = LocalDate.of(2023, 12, 31);

        StatisticRequestDTO statisticRequestDTO = new StatisticRequestDTO();
        statisticRequestDTO.setDate(birthDate);
        statisticRequestDTO.setFirstMonth(firstMonth);
        statisticRequestDTO.setLastMonth(lastMonth);

        List<Account> accounts = new ArrayList<>();
        accounts.add(Account.builder().birthDate(birthDate).regDate(LocalDate.of(2023, 3, 15)).build());
        accounts.add(Account.builder().birthDate(birthDate).regDate(LocalDate.of(2023, 7, 20)).build());
        accounts.add(Account.builder().birthDate(LocalDate.of(1985, 5, 10)).regDate(LocalDate.of(2022, 12, 25)).build());
        accounts.add(Account.builder().birthDate(LocalDate.of(1995, 9, 30)).regDate(LocalDate.of(2023, 5, 10)).build());

        when(accountRepository.findAll()).thenReturn(accounts);

        StatisticDTO result = accountService.getStatistic(statisticRequestDTO);

        assertEquals(2, result.getCountPerAgeDTO().getCount(), "Unexpected count for age");
        assertEquals(3, result.getCountPerMonthDTO().getCount(), "Unexpected count for month");
        assertEquals(3, result.getCount(), "Unexpected total count");
        assertEquals(birthDate, result.getDate());
        verify(accountRepository, times(1)).findAll();
    }


    @Test
    @DisplayName("Test getStatistic with no matching data")
    public void testGetListAccounts() {
        Page pageDto = new Page();
        pageDto.setPage(0);
        pageDto.setSize(2);

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCity("Moscow");

        List<Account> accounts = Arrays.asList(
                Account.builder().id(UUID.randomUUID()).email("test1@example.com").city("Moscow").build(),
                Account.builder().id(UUID.randomUUID()).email("test2@example.com").city("Moscow").build()
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        org.springframework.data.domain.Page<Account> page = new PageImpl<>(accounts, pageable, accounts.size());

        when(accountRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        AccountPageDTO result = accountService.getListAccounts(searchDTO, pageDto);

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.getSize(), "Unexpected number of accounts returned");
        assertEquals(0, result.getNumber(), "Unexpected page number");
        assertEquals(2, result.getNumberOfElements(), "Unexpected number of elements on page");
        assertEquals(1, result.getTotalPages(), "Unexpected total number of pages");
        assertEquals(2, result.getTotalElements(), "Unexpected total number of elements");

        assertTrue(result.isFirst(), "Expected to be the first page");
        assertTrue(result.isLast(), "Expected to be the last page");
    }
}
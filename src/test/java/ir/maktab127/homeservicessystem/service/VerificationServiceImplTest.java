package ir.maktab127.homeservicessystem.service;
import ir.maktab127.homeservicessystem.entity.Customer;
import ir.maktab127.homeservicessystem.entity.Person;
import ir.maktab127.homeservicessystem.entity.VerificationToken;
import ir.maktab127.homeservicessystem.exceptions.InvalidOperationException;
import ir.maktab127.homeservicessystem.exceptions.ResourceNotFoundException;
import ir.maktab127.homeservicessystem.repository.PersonRepository;
import ir.maktab127.homeservicessystem.repository.SpecialistRepository;
import ir.maktab127.homeservicessystem.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceImplTest {

    @Mock
    private VerificationTokenRepository tokenRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private SpecialistRepository specialistRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private VerificationServiceImpl verificationService;

    private Person testPerson;
    private VerificationToken testToken;

    @BeforeEach
    void setUp() {
        testPerson = new Customer();
        testPerson.setId(1L);
        testPerson.setEmail("test@example.com");
        testPerson.setEmailVerified(false);

        testToken = new VerificationToken("test-token", testPerson);
    }

    @Test
    @DisplayName("Test Create and Send Verification Token")
    void createAndSendVerificationToken_ShouldSaveTokenAndSendEmail() {
        verificationService.createAndSendVerificationCode(testPerson);

        verify(tokenRepository, times(1)).save(any(VerificationToken.class));
        verify(emailService, times(1)).sendVerificationEmail(eq("test@example.com"), anyString());
    }

    @Test
    @DisplayName("Test Verify User - Success")
    void verifyUser_WhenTokenIsValid_ShouldVerifyUserAndDeleteToken() {
        testToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        when(tokenRepository.findByToken("test-token")).thenReturn(Optional.of(testToken));

        String result = verificationService.verifyUser("test-token");

        assertTrue(testPerson.isEmailVerified());
        assertEquals("Email verified successfully. You can now login.", result);
        verify(personRepository, times(1)).save(testPerson);
        verify(tokenRepository, times(1)).delete(testToken);
    }

    @Test
    @DisplayName("Test Verify User - Expired Token")
    void verifyUser_WhenTokenIsExpired_ShouldThrowExceptionAndDeleteToken() {
        testToken.setExpiryDate(LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken("test-token")).thenReturn(Optional.of(testToken));

        assertThrows(InvalidOperationException.class, () -> verificationService.verifyUser("test-token"));

        verify(personRepository, never()).save(any(Person.class));
        verify(tokenRepository, times(1)).delete(testToken);
    }

    @Test
    @DisplayName("Test Verify User - Invalid Token")
    void verifyUser_WhenTokenIsInvalid_ShouldThrowException() {
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> verificationService.verifyUser("invalid-token"));
    }
}
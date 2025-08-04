package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.SpecialistRegistrationDto;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import ir.maktab127.homeservicessystem.exceptions.DuplicateResourceException;
import ir.maktab127.homeservicessystem.repository.SpecialistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialistServiceImplTest {

    @Mock
    private SpecialistRepository specialistRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private SpecialistServiceImpl specialistService;

    private SpecialistRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        registrationDto = new SpecialistRegistrationDto("Jane", "Doe", "jane.doe@example.com", "password123", "");
    }

    @Test
    @DisplayName("Test Register Specialist - Success")
    void register_WhenEmailIsUnique_ShouldSaveSpecialistEncodePasswordAndSendVerification() {
        when(specialistRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(specialistRepository.save(any(Specialist.class))).thenAnswer(inv -> inv.getArgument(0));

        specialistService.register(registrationDto);

        ArgumentCaptor<Specialist> specialistCaptor = ArgumentCaptor.forClass(Specialist.class);
        verify(specialistRepository, times(1)).save(specialistCaptor.capture());
        verify(verificationService, times(1)).createAndSendVerificationCode(any(Specialist.class));

        Specialist savedSpecialist = specialistCaptor.getValue();
        assertEquals("encodedPassword", savedSpecialist.getPassword());
        assertEquals(SpecialistStatus.NEW_AWAITING_PICTURE, savedSpecialist.getStatus());
        assertNotNull(savedSpecialist.getWallet());
    }

    @Test
    @DisplayName("Test Register Specialist - Failure (Duplicate Email)")
    void register_WhenEmailExists_ShouldThrowDuplicateResourceException() {
        when(specialistRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(new Specialist()));

        assertThrows(DuplicateResourceException.class, () -> specialistService.register(registrationDto));

        verify(specialistRepository, never()).save(any(Specialist.class));
        verify(verificationService, never()).createAndSendVerificationCode(any(Specialist.class));
    }
}
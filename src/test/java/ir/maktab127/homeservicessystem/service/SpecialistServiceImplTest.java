package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.SpecialistRegistrationDto;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.Wallet;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import ir.maktab127.homeservicessystem.exceptions.DuplicateResourceException;
import ir.maktab127.homeservicessystem.exceptions.InvalidOperationException;
import ir.maktab127.homeservicessystem.repository.SpecialistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialistServiceImplTest {

    @Mock
    private SpecialistRepository specialistRepository;


    @InjectMocks
    private SpecialistServiceImpl specialistService;

    private SpecialistRegistrationDto registrationDtoWithImage;
    private SpecialistRegistrationDto registrationDtoWithoutImage;

    @BeforeEach
    void setUp() {
        registrationDtoWithImage = new SpecialistRegistrationDto("Jane", "Doe", "jane.doe@example.com", "password123", "C:/fake/path.jpg");
        registrationDtoWithoutImage = new SpecialistRegistrationDto("Jack", "Smith", "jack.smith@example.com", "password456", "");
    }

    @Test
    void register_WhenEmailIsUniqueAndImagePathIsBlank_ShouldSetStatusToNewAwaitingPicture() {
        when(specialistRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(specialistRepository.save(any(Specialist.class))).thenAnswer(inv -> inv.getArgument(0));

        Specialist result = specialistService.register(registrationDtoWithoutImage);

        assertNotNull(result);
        assertNotNull(result.getWallet());
        assertEquals(SpecialistStatus.NEW_AWAITING_PICTURE, result.getStatus());
        assertNull(result.getProfilePicture());
        verify(specialistRepository, times(1)).save(any(Specialist.class));
    }


    @Test
    void register_WhenEmailIsUniqueAndImagePathExists_ShouldSetStatusToAwaitingConfirmation() {

        when(specialistRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(specialistRepository.save(any(Specialist.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThrows(InvalidOperationException.class, () -> {
            specialistService.register(registrationDtoWithImage);
        });
    }


    @Test
    void register_WhenEmailExists_ShouldThrowDuplicateResourceException() {
        when(specialistRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(new Specialist()));

        assertThrows(DuplicateResourceException.class, () -> specialistService.register(registrationDtoWithImage));
        verify(specialistRepository, never()).save(any(Specialist.class));
    }
}
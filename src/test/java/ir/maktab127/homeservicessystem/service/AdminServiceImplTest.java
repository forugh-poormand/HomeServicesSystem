package ir.maktab127.homeservicessystem.service;
import ir.maktab127.homeservicessystem.dto.MainServiceDto;
import ir.maktab127.homeservicessystem.entity.MainService;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.SubService;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import ir.maktab127.homeservicessystem.exceptions.DuplicateResourceException;
import ir.maktab127.homeservicessystem.exceptions.InvalidOperationException;
import ir.maktab127.homeservicessystem.exceptions.ResourceNotFoundException;
import ir.maktab127.homeservicessystem.repository.MainServiceRepository;
import ir.maktab127.homeservicessystem.repository.SpecialistRepository;
import ir.maktab127.homeservicessystem.repository.SubServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private MainServiceRepository mainServiceRepository;
    @Mock
    private SubServiceRepository subServiceRepository;
    @Mock
    private SpecialistRepository specialistRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Specialist specialist;
    private SubService subService;

    @BeforeEach
    void setUp() {
        specialist = new Specialist();
        specialist.setId(1L);

        subService = new SubService();
        subService.setId(1L);
    }

    @Test
    void createMainService_success() {
        MainServiceDto dto = new MainServiceDto("Cleaning");
        MainService savedService = new MainService();
        savedService.setId(1L);
        savedService.setName("Cleaning");

        when(mainServiceRepository.findByName("Cleaning")).thenReturn(Optional.empty());
        when(mainServiceRepository.save(any(MainService.class))).thenReturn(savedService);

        MainService result = adminService.createMainService(dto);

        assertNotNull(result);
        assertEquals("Cleaning", result.getName());
        verify(mainServiceRepository, times(1)).findByName("Cleaning");
        verify(mainServiceRepository, times(1)).save(any(MainService.class));
    }

    @Test
    void createMainService_throwsDuplicateResourceException() {
        MainServiceDto dto = new MainServiceDto("Cleaning");
        when(mainServiceRepository.findByName("Cleaning")).thenReturn(Optional.of(new MainService()));

        assertThrows(DuplicateResourceException.class, () -> adminService.createMainService(dto));

        verify(mainServiceRepository, times(1)).findByName("Cleaning");
        verify(mainServiceRepository, never()).save(any(MainService.class));
    }

    @Test
    void confirmSpecialist_success() {
        specialist.setStatus(SpecialistStatus.AWAITING_CONFIRMATION);
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(specialistRepository.save(any(Specialist.class))).thenReturn(specialist);

        Specialist result = adminService.confirmSpecialist(1L);

        assertEquals(SpecialistStatus.CONFIRMED, result.getStatus());
        verify(specialistRepository, times(1)).findById(1L);
        verify(specialistRepository, times(1)).save(specialist);
    }

    @Test
    void confirmSpecialist_throwsResourceNotFoundException() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.confirmSpecialist(1L));
        verify(specialistRepository, never()).save(any(Specialist.class));
    }

    @Test
    void assignSpecialistToSubService_success() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(subServiceRepository.findById(1L)).thenReturn(Optional.of(subService));

        adminService.assignSpecialistToSubService(1L, 1L);

        assertTrue(specialist.getExpertIn().contains(subService));
        verify(specialistRepository, times(1)).save(specialist);
    }

    @Test
    void removeSpecialistFromSubService_success() {
        specialist.getExpertIn().add(subService);
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(subServiceRepository.findById(1L)).thenReturn(Optional.of(subService));

        adminService.removeSpecialistFromSubService(1L, 1L);

        assertFalse(specialist.getExpertIn().contains(subService));
        verify(specialistRepository, times(1)).save(specialist);
    }

    @Test
    @Disabled("Test not fully implemented yet")
    void removeSpecialistFromSubService_throwsInvalidOperationException_withActiveOrders() {
        specialist.getExpertIn().add(subService);
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(subServiceRepository.findById(1L)).thenReturn(Optional.of(subService));

    }
}
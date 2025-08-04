package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.MainServiceDto;
import ir.maktab127.homeservicessystem.dto.SubServiceRequestDto;
import ir.maktab127.homeservicessystem.dto.UserSearchCriteriaDto;
import ir.maktab127.homeservicessystem.entity.*;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import ir.maktab127.homeservicessystem.exceptions.DuplicateResourceException;
import ir.maktab127.homeservicessystem.exceptions.ResourceNotFoundException;
import ir.maktab127.homeservicessystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
    @Mock
    private CustomerOrderRepository orderRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private MainService mainService;
    private SubService subService;
    private Specialist specialist;

    @BeforeEach
    void setUp() {
        mainService = new MainService();
        mainService.setId(1L);
        mainService.setName("Cleaning");
        mainService.setSubServices(new HashSet<>());

        subService = new SubService();
        subService.setId(1L);
        subService.setName("Carpet Cleaning");
        subService.setMainService(mainService);

        specialist = new Specialist();
        specialist.setId(1L);
        specialist.setStatus(SpecialistStatus.AWAITING_CONFIRMATION);
    }

    @Test
    @DisplayName("Test Create Main Service - Success")
    void createMainService_Success() {
        when(mainServiceRepository.findByName("New Service")).thenReturn(Optional.empty());
        when(mainServiceRepository.save(any(MainService.class))).thenAnswer(inv -> inv.getArgument(0));

        MainService result = adminService.createMainService(new MainServiceDto("New Service"));

        assertNotNull(result);
        assertEquals("New Service", result.getName());
    }

    @Test
    @DisplayName("Test Create Main Service - Failure (Duplicate)")
    void createMainService_ThrowsDuplicateResourceException() {
        when(mainServiceRepository.findByName("Existing Service")).thenReturn(Optional.of(new MainService()));
        assertThrows(DuplicateResourceException.class, () -> adminService.createMainService(new MainServiceDto("Existing Service")));
    }

    @Test
    @DisplayName("Test Create Sub Service - Success")
    void createSubService_Success() {
        SubServiceRequestDto dto = new SubServiceRequestDto("New Sub", BigDecimal.TEN, "Desc", 1L);
        when(mainServiceRepository.findById(1L)).thenReturn(Optional.of(mainService));
        when(subServiceRepository.save(any(SubService.class))).thenAnswer(inv -> inv.getArgument(0));

        SubService result = adminService.createSubService(dto);

        assertNotNull(result);
        assertEquals("New Sub", result.getName());
    }

    @Test
    @DisplayName("Test Confirm Specialist - Success")
    void confirmSpecialist_Success() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(specialistRepository.save(any(Specialist.class))).thenReturn(specialist);

        Specialist result = adminService.confirmSpecialist(1L);

        assertEquals(SpecialistStatus.CONFIRMED, result.getStatus());
    }

    @Test
    @DisplayName("Test Confirm Specialist - Failure (Not Found)")
    void confirmSpecialist_ThrowsResourceNotFoundException() {
        when(specialistRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> adminService.confirmSpecialist(99L));
    }
}
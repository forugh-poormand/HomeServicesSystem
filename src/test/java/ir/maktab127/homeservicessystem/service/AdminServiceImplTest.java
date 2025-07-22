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
    private CustomerRepository customerRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Specialist specialist;
    private MainService mainService;
    private SubService subService;

    @BeforeEach
    void setUp() {
        specialist = new Specialist();
        specialist.setId(1L);
        specialist.setExpertIn(new HashSet<>());
        specialist.setOrders(new HashSet<>());
        specialist.setStatus(SpecialistStatus.AWAITING_CONFIRMATION);

        mainService = new MainService();
        mainService.setId(1L);
        mainService.setName("Cleaning");
        mainService.setSubServices(new HashSet<>());

        subService = new SubService();
        subService.setId(1L);
        subService.setName("Carpet Cleaning");
        subService.setMainService(mainService);
    }

    @Test
    @DisplayName("Test Create Main Service - Success")
    void createMainService_WhenNameIsUnique_ShouldSaveAndReturnMainService() {
        MainServiceDto dto = new MainServiceDto("Cleaning");
        when(mainServiceRepository.findByName("Cleaning")).thenReturn(Optional.empty());
        when(mainServiceRepository.save(any(MainService.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MainService result = adminService.createMainService(dto);

        assertNotNull(result);
        assertEquals("Cleaning", result.getName());
        verify(mainServiceRepository, times(1)).save(any(MainService.class));
    }

    @Test
    @DisplayName("Test Create Main Service - Failure (Duplicate)")
    void createMainService_WhenNameExists_ShouldThrowDuplicateResourceException() {
        MainServiceDto dto = new MainServiceDto("Cleaning");
        when(mainServiceRepository.findByName("Cleaning")).thenReturn(Optional.of(new MainService()));

        assertThrows(DuplicateResourceException.class, () -> adminService.createMainService(dto));
        verify(mainServiceRepository, never()).save(any(MainService.class));
    }

    @Test
    @DisplayName("Test Create Sub-Service - Success")
    void createSubService_WhenDataIsValid_ShouldSaveAndReturnSubService() {
        SubServiceRequestDto dto = new SubServiceRequestDto("Carpet Cleaning", BigDecimal.TEN, "desc", 1L);
        when(mainServiceRepository.findById(1L)).thenReturn(Optional.of(mainService));
        when(subServiceRepository.save(any(SubService.class))).thenAnswer(inv -> inv.getArgument(0));

        SubService result = adminService.createSubService(dto);

        assertNotNull(result);
        assertEquals("Carpet Cleaning", result.getName());
        assertEquals(mainService, result.getMainService());
        verify(subServiceRepository, times(1)).save(any(SubService.class));
    }

    @Test
    @DisplayName("Test Create Sub-Service - Failure (Main Service Not Found)")
    void createSubService_WhenMainServiceNotFound_ShouldThrowResourceNotFoundException() {
        SubServiceRequestDto dto = new SubServiceRequestDto("Carpet Cleaning", BigDecimal.TEN, "desc", 1L);
        when(mainServiceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.createSubService(dto));
        verify(subServiceRepository, never()).save(any(SubService.class));
    }

    @Test
    @DisplayName("Test Confirm Specialist - Success")
    void confirmSpecialist_WhenSpecialistExists_ShouldConfirmAndReturnSpecialist() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(specialistRepository.save(any(Specialist.class))).thenReturn(specialist);

        ArgumentCaptor<Specialist> specialistCaptor = ArgumentCaptor.forClass(Specialist.class);

        adminService.confirmSpecialist(1L);

        verify(specialistRepository, times(1)).save(specialistCaptor.capture());
        assertEquals(SpecialistStatus.CONFIRMED, specialistCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("Test Confirm Specialist - Failure (Not Found)")
    void confirmSpecialist_WhenSpecialistNotFound_ShouldThrowResourceNotFoundException() {
        when(specialistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.confirmSpecialist(99L));
        verify(specialistRepository, never()).save(any(Specialist.class));
    }

    @Test
    @DisplayName("Test Assign Specialist To Sub-Service - Success")
    void assignSpecialistToSubService_WhenBothExist_ShouldSucceed() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(subServiceRepository.findById(1L)).thenReturn(Optional.of(subService));

        adminService.assignSpecialistToSubService(1L, 1L);

        verify(specialistRepository, times(1)).save(specialist);
        assertTrue(specialist.getExpertIn().contains(subService));
    }

    @Test
    @DisplayName("Test Remove Specialist From Sub-Service - Success")
    void removeSpecialistFromSubService_WhenNoActiveOrders_ShouldSucceed() {
        specialist.getExpertIn().add(subService);
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(subServiceRepository.findById(1L)).thenReturn(Optional.of(subService));

        adminService.removeSpecialistFromSubService(1L, 1L);

        verify(specialistRepository, times(1)).save(specialist);
        assertFalse(specialist.getExpertIn().contains(subService));
    }

    @Test
    @DisplayName("Test Find All Unconfirmed Specialists - Success")
    void findAllUnconfirmedSpecialists_ShouldReturnOnlyUnconfirmed() {
        Specialist confirmedSpecialist = new Specialist();
        confirmedSpecialist.setStatus(SpecialistStatus.CONFIRMED);

        when(specialistRepository.findAll()).thenReturn(List.of(specialist, confirmedSpecialist));

        List<Specialist> result = adminService.findAllUnconfirmedSpecialists();

        assertEquals(1, result.size());
        assertEquals(SpecialistStatus.AWAITING_CONFIRMATION, result.get(0).getStatus());
    }

    @Test
    @DisplayName("Test Search Users - Role Specialist")
    void searchUsers_WhenRoleIsSpecialist_ShouldOnlyCallSpecialistRepo() {
        UserSearchCriteriaDto criteria = new UserSearchCriteriaDto("specialist", null, null, null, null, null);
        when(specialistRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(specialist));

        List<Person> result = adminService.searchUsers(criteria);

        assertEquals(1, result.size());
        verify(specialistRepository, times(1)).findAll(any(Specification.class));
        verify(customerRepository, never()).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Test Search Users - Role Customer")
    void searchUsers_WhenRoleIsCustomer_ShouldOnlyCallCustomerRepo() {
        UserSearchCriteriaDto criteria = new UserSearchCriteriaDto("customer", null, null, null, null, null);
        Customer customer = new Customer();
        when(customerRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(customer));

        List<Person> result = adminService.searchUsers(criteria);

        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findAll(any(Specification.class));
        verify(specialistRepository, never()).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Test Search Users - Role Null")
    void searchUsers_WhenRoleIsNull_ShouldCallBothRepos() {
        UserSearchCriteriaDto criteria = new UserSearchCriteriaDto(null, null, null, null, null, null);
        Customer customer = new Customer();
        when(customerRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(customer));
        when(specialistRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(specialist));

        List<Person> result = adminService.searchUsers(criteria);

        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll(any(Specification.class));
        verify(specialistRepository, times(1)).findAll(any(Specification.class));
    }
}
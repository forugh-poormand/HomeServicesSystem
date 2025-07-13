package ir.maktab127.homeservicessystem.service;
import ir.maktab127.homeservicessystem.dto.SuggestionRequestDto;
import ir.maktab127.homeservicessystem.entity.CustomerOrder;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.SubService;
import ir.maktab127.homeservicessystem.entity.Suggestion;
import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import ir.maktab127.homeservicessystem.exceptions.InvalidOperationException;
import ir.maktab127.homeservicessystem.repository.CustomerOrderRepository;
import ir.maktab127.homeservicessystem.repository.SpecialistRepository;
import ir.maktab127.homeservicessystem.repository.SuggestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialistServiceImplTest {

    @Mock
    private SpecialistRepository specialistRepository;
    @Mock
    private CustomerOrderRepository orderRepository;
    @Mock
    private SuggestionRepository suggestionRepository;

    @InjectMocks
    private SpecialistServiceImpl specialistService;

    private Specialist specialist;
    private CustomerOrder order;
    private SubService subService;
    private SuggestionRequestDto suggestionDto;

    @BeforeEach
    void setUp() {
        subService = new SubService();
        subService.setId(1L);

        specialist = new Specialist();
        specialist.setId(1L);
        specialist.setStatus(SpecialistStatus.CONFIRMED);
        specialist.setExpertIn(Set.of(subService));

        order = new CustomerOrder();
        order.setId(1L);
        order.setStatus(OrderStatus.WAITING_FOR_SUGGESTIONS);
        order.setSubService(subService);
        order.setProposedPrice(new BigDecimal("100"));

        suggestionDto = new SuggestionRequestDto(new BigDecimal("120"), 2, LocalDateTime.now().plusHours(1));
    }

    @Test
    void submitSuggestion_success() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(suggestionRepository.save(any(Suggestion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Suggestion result = specialistService.submitSuggestion(1L, 1L, suggestionDto);

        assertEquals(specialist, result.getSpecialist());
        assertEquals(order, result.getOrder());
        assertEquals(OrderStatus.WAITING_FOR_SELECTION, order.getStatus());
        verify(suggestionRepository, times(1)).save(any(Suggestion.class));
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void submitSuggestion_throwsInvalidOperationException_whenNotAnExpert() {
        specialist.setExpertIn(Set.of()); // Specialist is not an expert in anything
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(InvalidOperationException.class, () -> {
            specialistService.submitSuggestion(1L, 1L, suggestionDto);
        });

        assertEquals("You are not an expert for this service type.", exception.getMessage());
        verify(suggestionRepository, never()).save(any(Suggestion.class));
    }

    @Test
    void submitSuggestion_throwsInvalidOperationException_whenPriceIsTooLow() {
        SuggestionRequestDto lowPriceDto = new SuggestionRequestDto(new BigDecimal("90"), 2, null);
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(InvalidOperationException.class, () -> {
            specialistService.submitSuggestion(1L, 1L, lowPriceDto);
        });

        assertEquals("Specialist's price cannot be less than the customer's proposed price.", exception.getMessage());
    }

    @Test
    void submitSuggestion_throwsInvalidOperationException_whenOrderNotInCorrectState() {
        order.setStatus(OrderStatus.DONE);
        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(InvalidOperationException.class, () -> {
            specialistService.submitSuggestion(1L, 1L, suggestionDto);
        });

        assertEquals("Order is not accepting suggestions at the moment.", exception.getMessage());
    }
}

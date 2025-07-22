package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.OrderRequestDto;
import ir.maktab127.homeservicessystem.dto.UserRegistrationDto;
import ir.maktab127.homeservicessystem.entity.Customer;
import ir.maktab127.homeservicessystem.entity.CustomerOrder;
import ir.maktab127.homeservicessystem.entity.SubService;
import ir.maktab127.homeservicessystem.entity.Wallet;
import ir.maktab127.homeservicessystem.exceptions.DuplicateResourceException;
import ir.maktab127.homeservicessystem.exceptions.InvalidOperationException;
import ir.maktab127.homeservicessystem.repository.CustomerRepository;
import ir.maktab127.homeservicessystem.repository.CustomerOrderRepository;
import ir.maktab127.homeservicessystem.repository.SubServiceRepository;
import ir.maktab127.homeservicessystem.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private SubServiceRepository subServiceRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private CustomerOrderRepository orderRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private UserRegistrationDto userRegistrationDto;
    private OrderRequestDto orderRequestDto;
    private Customer customer;
    private SubService subService;

    @BeforeEach
    void setUp() {
        userRegistrationDto = new UserRegistrationDto("John", "Doe", "john.doe@example.com", "password123");

        customer = new Customer();
        customer.setId(1L);

        subService = new SubService();
        subService.setId(1L);
        subService.setBasePrice(new BigDecimal("100"));

        orderRequestDto = new OrderRequestDto(1L, new BigDecimal("120"), "description", "address", LocalDateTime.now().plusDays(1));
    }

    @Test
    void register_WhenEmailIsUnique_ShouldSaveCustomerAndWallet() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        Customer result = customerService.register(userRegistrationDto);

        assertNotNull(result);
        assertNotNull(result.getWallet());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void register_WhenEmailExists_ShouldThrowDuplicateResourceException() {
        when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(new Customer()));

        assertThrows(DuplicateResourceException.class, () -> customerService.register(userRegistrationDto));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void placeOrder_WhenPriceIsAboveBasePrice_ShouldSaveOrder() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(subServiceRepository.findById(1L)).thenReturn(Optional.of(subService));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerOrder result = customerService.placeOrder(1L, orderRequestDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("120"), result.getProposedPrice());
        verify(orderRepository, times(1)).save(any(CustomerOrder.class));
    }

    @Test
    void placeOrder_WhenPriceIsBelowBasePrice_ShouldThrowInvalidOperationException() {
        OrderRequestDto lowPriceDto = new OrderRequestDto(1L, new BigDecimal("90"), "desc", "addr", LocalDateTime.now().plusDays(1));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(subServiceRepository.findById(1L)).thenReturn(Optional.of(subService));

        assertThrows(InvalidOperationException.class, () -> customerService.placeOrder(1L, lowPriceDto));
        verify(orderRepository, never()).save(any(CustomerOrder.class));
    }
}
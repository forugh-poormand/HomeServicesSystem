package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.OrderRequestDto;
import ir.maktab127.homeservicessystem.dto.UserRegistrationDto;
import ir.maktab127.homeservicessystem.entity.*;
import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;
import ir.maktab127.homeservicessystem.exceptions.DuplicateResourceException;
import ir.maktab127.homeservicessystem.exceptions.InvalidOperationException;
import ir.maktab127.homeservicessystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private VerificationService verificationService;
    @Mock
    private SubServiceRepository subServiceRepository;
    @Mock
    private CustomerOrderRepository orderRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private UserRegistrationDto registrationDto;
    private Customer customer;
    private Wallet wallet;
    private SubService subService;
    private CustomerOrder order;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto("John", "Doe", "john.doe@example.com", "password123");

        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(new BigDecimal("1000"));

        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("john.doe@example.com");
        customer.setWallet(wallet);

        subService = new SubService();
        subService.setId(1L);
        subService.setName("Test Sub Service");
        subService.setBasePrice(new BigDecimal("100"));

        order = new CustomerOrder();
        order.setId(1L);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.WAITING_FOR_SUGGESTIONS);
    }

    @Test
    @DisplayName("Test Successful Customer Registration")
    void register_Success() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(1L); // Simulate saving and getting an ID
            return c;
        });

        customerService.register(registrationDto);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository, times(1)).save(customerCaptor.capture());
        verify(verificationService, times(1)).createAndSendVerificationCode(any(Customer.class));

        Customer savedCustomer = customerCaptor.getValue();
        assertEquals("encodedPassword", savedCustomer.getPassword());
        assertNotNull(savedCustomer.getWallet());
    }

    @Test
    @DisplayName("Test Registration Failure Due to Duplicate Email")
    void register_Failure_DuplicateEmail() {
        when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(new Customer()));

        assertThrows(DuplicateResourceException.class, () -> customerService.register(registrationDto));

        verify(customerRepository, never()).save(any());
        verify(verificationService, never()).createAndSendVerificationCode(any());
    }

    @Test
    @DisplayName("Test Place Order Failure - Proposed Price is Less Than Base Price")
    void placeOrder_Failure_ProposedPriceLessThanBasePrice() {
        OrderRequestDto orderDto = new OrderRequestDto(1L, new BigDecimal("50"), "description", "address", LocalDateTime.now());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(subServiceRepository.findById(1L)).thenReturn(Optional.of(subService));

        assertThrows(InvalidOperationException.class, () -> customerService.placeOrder(1L, orderDto));

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test Select Suggestion Failure - Invalid Order Status")
    void selectSuggestion_Failure_InvalidOrderStatus() {
        order.setStatus(OrderStatus.DONE); // Set status to something other than WAITING_FOR_SELECTION

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class, () -> customerService.selectSuggestion(1L, 1L, 1L));
    }

    @Test
    @DisplayName("Test Payment Failure - Insufficient Funds")
    void payForOrder_Failure_InsufficientFunds() {
        order.setStatus(OrderStatus.DONE);
        order.setProposedPrice(new BigDecimal("2000")); // price > balance
        order.setSelectedSpecialist(new Specialist()); // Needs a non-null specialist

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class, () -> customerService.payForOrder(1L, 1L));
    }
}
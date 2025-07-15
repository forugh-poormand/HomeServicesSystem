package ir.maktab127.homeservicessystem.service;


import ir.maktab127.homeservicessystem.dto.OrderRequestDto;
import ir.maktab127.homeservicessystem.dto.UserRegistrationDto;
import ir.maktab127.homeservicessystem.entity.Customer;
import ir.maktab127.homeservicessystem.entity.CustomerOrder;
import ir.maktab127.homeservicessystem.entity.SubService;
import ir.maktab127.homeservicessystem.entity.Wallet;
import ir.maktab127.homeservicessystem.exceptions.InvalidOperationException;
import ir.maktab127.homeservicessystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private SubServiceRepository subServiceRepository;
    @Mock
    private CustomerOrderRepository orderRepository;
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private SubService subService;
    private OrderRequestDto orderRequestDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setWallet(new Wallet());
        customer.getWallet().setBalance(new BigDecimal("100.00"));

        subService = new SubService();
        subService.setId(1L);
        subService.setBasePrice(new BigDecimal("50.00"));

        orderRequestDto = new OrderRequestDto(1L, new BigDecimal("60.00"), "description", "address", LocalDateTime.now().plusDays(1));
    }

    @Test
    void register_success() {
        UserRegistrationDto dto = new UserRegistrationDto("John", "Doe", "john.doe@test.com", "password123");
        when(customerRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        Customer result = customerService.register(dto);

        assertEquals(dto.email(), result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void placeOrder_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(subServiceRepository.findById(1L)).thenReturn(Optional.of(subService));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerOrder result = customerService.placeOrder(1L, orderRequestDto);

        assertEquals(customer, result.getCustomer());
        assertEquals(subService, result.getSubService());
        assertEquals(orderRequestDto.proposedPrice(), result.getProposedPrice());
        verify(orderRepository, times(1)).save(any(CustomerOrder.class));
    }

    @Test
    void placeOrder_throwsInvalidOperationException_whenPriceIsTooLow() {
        OrderRequestDto lowPriceDto = new OrderRequestDto(1L, new BigDecimal("40.00"), "desc", "addr", null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(subServiceRepository.findById(1L)).thenReturn(Optional.of(subService));

        Exception exception = assertThrows(InvalidOperationException.class, () -> {
            customerService.placeOrder(1L, lowPriceDto);
        });

        assertEquals("Proposed price must be equal or greater than the base price.", exception.getMessage());
        verify(orderRepository, never()).save(any(CustomerOrder.class));
    }
}
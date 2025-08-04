package ir.maktab127.homeservicessystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktab127.homeservicessystem.config.security.JwtService;
import ir.maktab127.homeservicessystem.controller.CustomerController;
import ir.maktab127.homeservicessystem.dto.OrderRequestDto;
import ir.maktab127.homeservicessystem.dto.UserRegistrationDto;
import ir.maktab127.homeservicessystem.entity.Customer;
import ir.maktab127.homeservicessystem.entity.CustomerOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("Test Register Customer Endpoint - Success")
    void register_ShouldReturnCreated() throws Exception {
        // Given
        UserRegistrationDto registrationDto = new UserRegistrationDto("New", "User", "new@user.com", "password123");
        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setFirstName("New");
        savedCustomer.setLastName("User");
        savedCustomer.setEmail("new@user.com");

        given(customerService.register(any(UserRegistrationDto.class))).willReturn(savedCustomer);

        // When
        ResultActions response = mockMvc.perform(post("/api/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto))
                .with(csrf()));

        // Then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("new@user.com"));
    }

    @Test
    @DisplayName("Test Register Endpoint with Invalid Input - Should Return Bad Request")
    void register_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given: password is too short
        UserRegistrationDto registrationDto = new UserRegistrationDto("New", "User", "new@user.com", "123");

        // When
        ResultActions response = mockMvc.perform(post("/api/customers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto))
                .with(csrf()));

        // Then
        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test Place Order Endpoint - Requires Authentication - Should Return 401 Unauthorized")
    void placeOrder_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        // Given
        OrderRequestDto orderDto = new OrderRequestDto(1L, BigDecimal.TEN, "Desc", "Addr", LocalDateTime.now());

        // When
        ResultActions response = mockMvc.perform(post("/api/customers/1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDto))
                .with(csrf()));

        // Then
        response.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="customer@test.com", roles = "CUSTOMER")
    @DisplayName("Test Place Order Endpoint - Success")
    void placeOrder_Success() throws Exception {
        long customerId = 1L;
        OrderRequestDto orderDto = new OrderRequestDto(1L, BigDecimal.TEN, "Desc", "Addr", LocalDateTime.now());

        given(customerService.placeOrder(eq(customerId), any(OrderRequestDto.class))).willReturn(new CustomerOrder());

        mockMvc.perform(post("/api/customers/{customerId}/orders", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto))
                        .with(csrf()))
                .andExpect(status().isCreated());
    }
}

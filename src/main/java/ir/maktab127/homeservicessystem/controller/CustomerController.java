package ir.maktab127.homeservicessystem.controller;

import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.dto.mapper.UserMapper;
import ir.maktab127.homeservicessystem.entity.Customer;
import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;
import ir.maktab127.homeservicessystem.service.CaptchaService;
import ir.maktab127.homeservicessystem.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegistrationDto dto) {
        Customer newCustomer = customerService.register(dto);
        return new ResponseEntity<>(UserMapper.toUserResponseDto(newCustomer), HttpStatus.CREATED);
    }

    @PostMapping("/{customerId}/orders")
    @PreAuthorize("hasRole('CUSTOMER') and #customerId == authentication.principal.id")
    public ResponseEntity<?> placeOrder(@PathVariable Long customerId, @Valid @RequestBody OrderRequestDto dto) {
        return new ResponseEntity<>(customerService.placeOrder(customerId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/services") // This should be public
    public ResponseEntity<?> getAllServices() {
        return ResponseEntity.ok(customerService.viewAllServices());
    }

    @GetMapping("/{customerId}/orders/{orderId}/suggestions")
    @PreAuthorize("hasRole('CUSTOMER') and #customerId == authentication.principal.id")
    public ResponseEntity<?> getSuggestions(@PathVariable Long customerId, @PathVariable Long orderId, @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(customerService.viewSuggestionsForOrder(customerId, orderId, sortBy));
    }

    @PutMapping("/{customerId}/orders/{orderId}/select-suggestion/{suggestionId}")
    @PreAuthorize("hasRole('CUSTOMER') and #customerId == authentication.principal.id")
    public ResponseEntity<OrderResponseDto> selectSuggestion(@PathVariable Long customerId, @PathVariable Long orderId, @PathVariable Long suggestionId) {
        return ResponseEntity.ok(customerService.selectSuggestion(customerId, orderId, suggestionId));
    }


    @GetMapping("/{customerId}/orders/history")
    @PreAuthorize("hasRole('CUSTOMER') and #customerId == authentication.principal.id")
    public ResponseEntity<List<OrderResponseDto>> getOrderHistory(@PathVariable Long customerId, @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(customerService.getOrderHistory(customerId, status));
    }

    @GetMapping("/{customerId}/wallet/balance")
    @PreAuthorize("hasRole('CUSTOMER') and #customerId == authentication.principal.id")
    public ResponseEntity<WalletDto> getWalletBalance(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getWalletBalance(customerId));
    }
}
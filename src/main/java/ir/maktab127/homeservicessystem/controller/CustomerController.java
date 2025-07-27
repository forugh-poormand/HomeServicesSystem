package ir.maktab127.homeservicessystem.controller;

import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.dto.mapper.UserMapper;
import ir.maktab127.homeservicessystem.entity.Customer;
import ir.maktab127.homeservicessystem.service.CaptchaService;
import ir.maktab127.homeservicessystem.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class CustomerController {

    private final CustomerService customerService;
    private final CaptchaService captchaService;

    public CustomerController(CustomerService customerService, CaptchaService captchaService) {
        this.customerService = customerService;
        this.captchaService = captchaService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegistrationDto dto) {
        Customer newCustomer = customerService.register(dto);
        return new ResponseEntity<>(UserMapper.toUserResponseDto(newCustomer), HttpStatus.CREATED);
    }

    @PostMapping("/{customerId}/orders")
    public ResponseEntity<?> placeOrder(
            @PathVariable Long customerId,
            @Valid @RequestBody OrderRequestDto dto) {
        return new ResponseEntity<>(customerService.placeOrder(customerId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/services")
    public ResponseEntity<?> getAllServices() {
        return ResponseEntity.ok(customerService.viewAllServices());
    }

    @GetMapping("/{customerId}/orders/{orderId}/suggestions")
    public ResponseEntity<?> getSuggestions(
            @PathVariable Long customerId,
            @PathVariable Long orderId,
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(customerService.viewSuggestionsForOrder(customerId, orderId, sortBy));
    }

    @PutMapping("/{customerId}/orders/{orderId}/select-suggestion/{suggestionId}")
    public ResponseEntity<OrderResponseDto> selectSuggestion(@PathVariable Long customerId, @PathVariable Long orderId, @PathVariable Long suggestionId) {
        return ResponseEntity.ok(customerService.selectSuggestion(customerId, orderId, suggestionId));
    }

    @PutMapping("/{customerId}/orders/{orderId}/start")
    public ResponseEntity<OrderResponseDto> startOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        return ResponseEntity.ok(customerService.startOrder(customerId, orderId));
    }

    @PutMapping("/{customerId}/orders/{orderId}/complete")
    public ResponseEntity<OrderResponseDto> completeOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        return ResponseEntity.ok(customerService.completeOrder(customerId, orderId));
    }

    @PostMapping("/{customerId}/orders/{orderId}/pay")
    public ResponseEntity<OrderResponseDto> payForOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        return ResponseEntity.ok(customerService.payForOrder(customerId, orderId));
    }

    @PostMapping("/{customerId}/orders/{orderId}/comments")
    public ResponseEntity<?> leaveComment(
            @PathVariable Long customerId,
            @PathVariable Long orderId,
            @Valid @RequestBody CommentRequestDto dto) {
        return new ResponseEntity<>(customerService.leaveComment(customerId, orderId, dto), HttpStatus.CREATED);
    }

    @PutMapping("/{customerId}/profile")
    public ResponseEntity<UserResponseDto> updateProfile(
            @PathVariable Long customerId,
            @Valid @RequestBody UserProfileUpdateDto dto) {
        return ResponseEntity.ok(customerService.updateProfile(customerId, dto));
    }

    @PostMapping("/{customerId}/wallet/charge")
    public ResponseEntity<?> chargeWallet(
            @PathVariable Long customerId,
            @RequestBody ChargeRequestDto dto) {
        return new ResponseEntity<>(customerService.chargeWallet(customerId, dto), HttpStatus.OK);
    }

    @GetMapping("/payment/captcha")
    public ResponseEntity<String> getCaptcha() {

        return ResponseEntity.ok(captchaService.generateCaptcha());
    }
}
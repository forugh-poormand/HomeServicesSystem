package ir.maktab127.homeservicessystem.controller;

import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.dto.mapper.UserMapper;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.service.SpecialistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialists")

public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody SpecialistRegistrationDto dto) {
        Specialist newSpecialist = specialistService.register(dto);
        return new ResponseEntity<>(UserMapper.toUserResponseDto(newSpecialist), HttpStatus.CREATED);
    }

    @GetMapping("/{specialistId}/orders/available")
    public ResponseEntity<?> getAvailableOrders(@PathVariable Long specialistId) {
        return ResponseEntity.ok(specialistService.findAvailableOrders(specialistId));
    }

    @PostMapping("/{specialistId}/orders/{orderId}/suggestions")
    public ResponseEntity<?> submitSuggestion(
            @PathVariable Long specialistId,
            @PathVariable Long orderId,
            @Valid @RequestBody SuggestionRequestDto dto) {
        return new ResponseEntity<>(specialistService.submitSuggestion(specialistId, orderId, dto), HttpStatus.CREATED);
    }

    @PutMapping("/{specialistId}/profile")
    public ResponseEntity<UserResponseDto> updateProfile(
            @PathVariable Long specialistId,
            @Valid @RequestBody UserProfileUpdateDto dto) {

        UserResponseDto updatedUser = specialistService.updateProfile(specialistId, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{specialistId}/wallet/history")
    public ResponseEntity<List<TransactionDto>> getWalletHistory(@PathVariable Long specialistId) {
        return ResponseEntity.ok(specialistService.getWalletHistory(specialistId));
    }

    @GetMapping("/{specialistId}/orders/history")
    public ResponseEntity<List<OrderResponseDto>> getOrderHistory(@PathVariable Long specialistId) {
        return ResponseEntity.ok(specialistService.getOrderHistory(specialistId));
    }

    @GetMapping("/{specialistId}/score/average")
    public ResponseEntity<AverageScoreDto> getAverageScore(@PathVariable Long specialistId) {
        return ResponseEntity.ok(specialistService.getAverageScore(specialistId));
    }
}
package ir.maktab127.homeservicessystem.controller;

import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.dto.mapper.UserMapper;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.service.SpecialistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialists")
@RequiredArgsConstructor
public class SpecialistController {

    private final SpecialistService specialistService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody SpecialistRegistrationDto dto) {
        Specialist newSpecialist = specialistService.register(dto);
        return new ResponseEntity<>(UserMapper.toUserResponseDto(newSpecialist), HttpStatus.CREATED);
    }

    @GetMapping("/{specialistId}/orders/available")
    @PreAuthorize("hasRole('SPECIALIST') and #specialistId == authentication.principal.id")
    public ResponseEntity<?> getAvailableOrders(@PathVariable Long specialistId) {
        return ResponseEntity.ok(specialistService.findAvailableOrders(specialistId));
    }

    @PostMapping("/{specialistId}/orders/{orderId}/suggestions")
    @PreAuthorize("hasRole('SPECIALIST') and #specialistId == authentication.principal.id")
    public ResponseEntity<?> submitSuggestion(
            @PathVariable Long specialistId,
            @PathVariable Long orderId,
            @Valid @RequestBody SuggestionRequestDto dto) {
        return new ResponseEntity<>(specialistService.submitSuggestion(specialistId, orderId, dto), HttpStatus.CREATED);
    }

    @PutMapping("/{specialistId}/profile")
    @PreAuthorize("hasRole('SPECIALIST') and #specialistId == authentication.principal.id")
    public ResponseEntity<UserResponseDto> updateProfile(
            @PathVariable Long specialistId,
            @Valid @RequestBody UserProfileUpdateDto dto) {
        UserResponseDto updatedUser = specialistService.updateProfile(specialistId, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{specialistId}/wallet/history")
    @PreAuthorize("hasRole('SPECIALIST') and #specialistId == authentication.principal.id")
    public ResponseEntity<List<TransactionDto>> getWalletHistory(@PathVariable Long specialistId) {
        return ResponseEntity.ok(specialistService.getWalletHistory(specialistId));
    }

    @GetMapping("/{specialistId}/orders/history")
    @PreAuthorize("hasRole('SPECIALIST') and #specialistId == authentication.principal.id")
    public ResponseEntity<List<OrderResponseDto>> getOrderHistory(@PathVariable Long specialistId) {
        return ResponseEntity.ok(specialistService.getOrderHistory(specialistId));
    }

    @GetMapping("/{specialistId}/score/average")
    @PreAuthorize("hasRole('SPECIALIST') and #specialistId == authentication.principal.id")
    public ResponseEntity<AverageScoreDto> getAverageScore(@PathVariable Long specialistId) {
        return ResponseEntity.ok(specialistService.getAverageScore(specialistId));
    }
}
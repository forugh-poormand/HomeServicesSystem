package ir.maktab127.homeservicessystem.controller;

import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.dto.mapper.UserMapper;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.Transaction;
import ir.maktab127.homeservicessystem.service.SpecialistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/specialists")

public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestPart("dto") SpecialistRegistrationDto dto,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {
        Specialist newSpecialist = specialistService.register(dto, profilePicture);
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

    @PutMapping(value = "/{specialistId}/profile", consumes = "multipart/form-data")
    public ResponseEntity<UserResponseDto> updateProfile(
            @PathVariable Long specialistId,
            @Valid @RequestPart("dto") UserProfileUpdateDto dto,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {

        UserResponseDto updatedUser = specialistService.updateProfile(specialistId, dto, profilePicture);
        return ResponseEntity.ok(updatedUser);
    }
    @GetMapping("/{specialistId}/wallet/history")
    public ResponseEntity<List<Transaction>> getWalletHistory(@PathVariable Long specialistId) {
        return ResponseEntity.ok(specialistService.getWalletHistory(specialistId));
    }
}
package ir.maktab127.homeservicessystem.controller;

import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.dto.mapper.UserMapper;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.service.SpecialistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/specialists")
@RequiredArgsConstructor
public class SpecialistController {

    private final SpecialistService specialistService;

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

    @PutMapping("/{specialistId}/profile")
    public ResponseEntity<UserResponseDto> updateProfile(
            @PathVariable Long specialistId,
            @Valid @RequestBody UserProfileUpdateDto dto) {
        return ResponseEntity.ok(specialistService.updateProfile(specialistId, dto));
    }
}
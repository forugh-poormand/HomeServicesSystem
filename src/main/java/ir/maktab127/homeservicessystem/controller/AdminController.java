package ir.maktab127.homeservicessystem.controller;

import ir.maktab127.homeservicessystem.dto.MainServiceDto;
import ir.maktab127.homeservicessystem.dto.SubServiceRequestDto;
import ir.maktab127.homeservicessystem.dto.UserResponseDto;
import ir.maktab127.homeservicessystem.dto.mapper.UserMapper;
import ir.maktab127.homeservicessystem.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")

public class AdminController {

    private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/services/main")
    public ResponseEntity<?> createMainService(@RequestBody MainServiceDto dto) {
        return new ResponseEntity<>(adminService.createMainService(dto), HttpStatus.CREATED);
    }

    @PostMapping("/services/sub")
    public ResponseEntity<?> createSubService(@RequestBody SubServiceRequestDto dto) {
        return new ResponseEntity<>(adminService.createSubService(dto), HttpStatus.CREATED);
    }

    @GetMapping("/specialists/unconfirmed")
    public ResponseEntity<List<UserResponseDto>> getUnconfirmedSpecialists() {
        List<UserResponseDto> list = adminService.findAllUnconfirmedSpecialists().stream()
                .map(UserMapper::toUserResponseDto).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/specialists/{id}/confirm")
    public ResponseEntity<?> confirmSpecialist(@PathVariable Long id) {
        adminService.confirmSpecialist(id);
        return ResponseEntity.ok("Specialist confirmed successfully.");
    }

    @PostMapping("/specialists/{specialistId}/assign/{subServiceId}")
    public ResponseEntity<?> assignSpecialistToSubService(@PathVariable Long specialistId, @PathVariable Long subServiceId) {
        adminService.assignSpecialistToSubService(specialistId, subServiceId);
        return ResponseEntity.ok("Specialist assigned to service successfully.");
    }
}
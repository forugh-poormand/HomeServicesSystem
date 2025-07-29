package ir.maktab127.homeservicessystem.controller;

import ir.maktab127.homeservicessystem.config.security.JwtService;
import ir.maktab127.homeservicessystem.dto.LoginRequestDto;
import ir.maktab127.homeservicessystem.dto.LoginResponseDto;
import ir.maktab127.homeservicessystem.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final VerificationService verificationService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        String result = verificationService.verifyUser(token);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponseDto(jwtToken));
    }
}
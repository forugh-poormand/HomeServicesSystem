package ir.maktab127.homeservicessystem.controller;

import ir.maktab127.homeservicessystem.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final VerificationService verificationService;

    /**
     * Endpoint to verify a user's email address using a token.
     * This is the link that would be sent to the user's email.
     * @param token The verification token sent to the user.
     * @return A confirmation message.
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        String result = verificationService.verifyUser(token);
        return ResponseEntity.ok(result);
    }
}

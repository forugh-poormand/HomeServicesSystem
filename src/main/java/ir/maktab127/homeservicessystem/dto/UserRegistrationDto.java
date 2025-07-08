package ir.maktab127.homeservicessystem.dto;

public record UserRegistrationDto(
        String firstName,
        String lastName,
        String email,
        @jakarta.validation.constraints.Size(min = 8, message = "Password must be at least 8 characters long.")
        @jakarta.validation.constraints.Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "Password must contain at least one letter and one number."
        )
        String password
) {
}

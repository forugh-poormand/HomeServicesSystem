package ir.maktab127.homeservicessystem.dto;

public record UserRegistrationDto(
        String firstName,
        String lastName,
        String email,
        String password
) {}

package ir.maktab127.homeservicessystem.dto;
public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {}
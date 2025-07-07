package ir.maktab127.homeservicessystem.dto;

import java.math.BigDecimal;

public record SubServiceResponseDto(
        Long id,
        String name,
        BigDecimal basePrice,
        String description
) {}
package ir.maktab127.homeservicessystem.dto;

import java.math.BigDecimal;

public record SubServiceRequestDto(
        String name,
        BigDecimal basePrice,
        String description,
        Long mainServiceId
) {}
package ir.maktab127.homeservicessystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderUpdateDto(
        BigDecimal proposedPrice,
        String description,
        String address,
        LocalDateTime requestedStartDate
) {}
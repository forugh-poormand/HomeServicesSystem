package ir.maktab127.homeservicessystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SuggestionResponseDto(
        Long id,
        String specialistName,
        BigDecimal proposedPrice,
        Integer durationInHours,
        LocalDateTime startTime
) {}
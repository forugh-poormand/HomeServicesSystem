package ir.maktab127.homeservicessystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SuggestionRequestDto(
        BigDecimal proposedPrice,
        Integer durationInHours,
        LocalDateTime startTime
) {}

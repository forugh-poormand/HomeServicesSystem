package ir.maktab127.homeservicessystem.dto;

import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryDto(
        Long id,
        String customerFullName,
        String specialistFullName,
        String subServiceName,
        OrderStatus status,
        LocalDateTime completionDate,
        BigDecimal finalPrice
) {
}

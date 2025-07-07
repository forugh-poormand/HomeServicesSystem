package ir.maktab127.homeservicessystem.dto;

import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponseDto(
        Long id,
        String customerName,
        String subServiceName,
        BigDecimal proposedPrice,
        OrderStatus status,
        LocalDateTime orderDate
) {}

package ir.maktab127.homeservicessystem.dto;

import ir.maktab127.homeservicessystem.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
        Long id,
        BigDecimal amount,
        TransactionType type,
        String description,
        LocalDateTime transactionDate
) {}
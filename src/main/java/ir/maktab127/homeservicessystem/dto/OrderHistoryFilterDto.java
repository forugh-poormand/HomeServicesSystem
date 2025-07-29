package ir.maktab127.homeservicessystem.dto;

import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record OrderHistoryFilterDto(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        OrderStatus orderStatus,

        Long subServiceId
) {
}

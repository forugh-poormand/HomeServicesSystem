package ir.maktab127.homeservicessystem.dto;

import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDetailDto(

        Long id,
        OrderStatus status,
        String description,
        String address,
        LocalDateTime orderDate,
        LocalDateTime requestedStartDate,
        LocalDateTime completionDate,


        String mainServiceName,
        String subServiceName,


        BigDecimal subServiceBasePrice,
        BigDecimal proposedPriceByCustomer,
        BigDecimal finalPriceBySpecialist,

        Long customerId,
        String customerFirstName,
        String customerLastName,
        String customerEmail,

        Long specialistId,
        String specialistFirstName,
        String specialistLastName,
        String specialistEmail,
        Double specialistScore,

        String commentText,
        Integer commentScore
) {
}

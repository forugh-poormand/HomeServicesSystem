package ir.maktab127.homeservicessystem.dto.mapper;

import ir.maktab127.homeservicessystem.dto.OrderDetailDto;
import ir.maktab127.homeservicessystem.dto.OrderSummaryDto;
import ir.maktab127.homeservicessystem.entity.Comment;
import ir.maktab127.homeservicessystem.entity.CustomerOrder;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.Suggestion;

import java.util.Optional;

public class OrderHistoryMapper {


    public static OrderSummaryDto toSummaryDto(CustomerOrder order) {
        if (order == null) {
            return null;
        }

        String specialistFullName = Optional.ofNullable(order.getSelectedSpecialist())
                .map(s -> s.getFirstName() + " " + s.getLastName())
                .orElse("N/A");

        return new OrderSummaryDto(
                order.getId(),
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                specialistFullName,
                order.getSubService().getName(),
                order.getStatus(),
                order.getCompletionDate(),
                order.getProposedPrice() // Assuming this is the final price for summary
        );
    }


    public static OrderDetailDto toDetailDto(CustomerOrder order) {
        if (order == null) {
            return null;
        }

        Specialist specialist = order.getSelectedSpecialist();
        Comment comment = order.getComment();


        var finalPrice = Optional.ofNullable(specialist)
                .flatMap(spec -> order.getSuggestions().stream()
                        .filter(s -> s.getSpecialist().equals(spec))
                        .findFirst()
                        .map(Suggestion::getProposedPrice))
                .orElse(order.getProposedPrice());


        return new OrderDetailDto(
                order.getId(),
                order.getStatus(),
                order.getDescription(),
                order.getAddress(),
                order.getOrderDate(),
                order.getRequestedStartDate(),
                order.getCompletionDate(),
                order.getSubService().getMainService().getName(),
                order.getSubService().getName(),
                order.getSubService().getBasePrice(),
                order.getProposedPrice(),
                finalPrice,
                order.getCustomer().getId(),
                order.getCustomer().getFirstName(),
                order.getCustomer().getLastName(),
                order.getCustomer().getEmail(),
                specialist != null ? specialist.getId() : null,
                specialist != null ? specialist.getFirstName() : null,
                specialist != null ? specialist.getLastName() : null,
                specialist != null ? specialist.getEmail() : null,
                specialist != null ? specialist.getAverageScore() : null,
                comment != null ? comment.getText() : null,
                comment != null ? comment.getScore() : null
        );
    }
}

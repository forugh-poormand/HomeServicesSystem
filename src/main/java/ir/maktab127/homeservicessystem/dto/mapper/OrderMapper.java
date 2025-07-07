package ir.maktab127.homeservicessystem.dto.mapper;
import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.entity.CustomerOrder;

public class OrderMapper {

    public static CustomerOrder toEntity(OrderRequestDto dto) {
        CustomerOrder order = new CustomerOrder();
        order.setProposedPrice(dto.proposedPrice());
        order.setDescription(dto.description());
        order.setAddress(dto.address());
        order.setRequestedStartDate(dto.requestedStartDate());
        return order;
    }

    public static OrderResponseDto toDto(CustomerOrder order) {
        return new OrderResponseDto(
                order.getId(),
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                order.getSubService().getName(),
                order.getProposedPrice(),
                order.getStatus(),
                order.getOrderDate()
        );
    }
}
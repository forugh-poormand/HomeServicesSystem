package ir.maktab127.homeservicessystem.specifications;

import ir.maktab127.homeservicessystem.dto.OrderHistoryFilterDto;
import ir.maktab127.homeservicessystem.entity.CustomerOrder;
import ir.maktab127.homeservicessystem.entity.SubService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderSpecification {


    public static Specification<CustomerOrder> filterBy(OrderHistoryFilterDto filterDto) {
        return (Root<CustomerOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (filterDto.subServiceId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("subService").get("id"), filterDto.subServiceId()));
            }


            if (filterDto.orderStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filterDto.orderStatus()));
            }


            if (filterDto.startDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("completionDate"),
                        filterDto.startDate().atStartOfDay()
                ));
            }
            if (filterDto.endDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("completionDate"),
                        filterDto.endDate().plusDays(1).atStartOfDay()
                ));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

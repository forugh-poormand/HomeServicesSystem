package ir.maktab127.homeservicessystem.specifications;

import ir.maktab127.homeservicessystem.dto.UserSearchCriteriaDto;
import ir.maktab127.homeservicessystem.entity.Customer;
import ir.maktab127.homeservicessystem.entity.Person;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.SubService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    private static void addCommonPredicates(Root<? extends Person> root, List<Predicate> predicates, CriteriaBuilder cb, UserSearchCriteriaDto criteria) {
        if (StringUtils.hasText(criteria.firstName())) {
            predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + criteria.firstName().toLowerCase() + "%"));
        }
        if (StringUtils.hasText(criteria.lastName())) {
            predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + criteria.lastName().toLowerCase() + "%"));
        }
    }

    public static Specification<Customer> getCustomerSpecification(UserSearchCriteriaDto criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            addCommonPredicates(root, predicates, cb, criteria);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Specialist> getSpecialistSpecification(UserSearchCriteriaDto criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            addCommonPredicates(root, predicates, cb, criteria);

            if (criteria.subServiceId() != null) {
                Join<Specialist, SubService> subServiceJoin = root.join("expertIn");
                predicates.add(cb.equal(subServiceJoin.get("id"), criteria.subServiceId()));
            }
            if (criteria.minScore() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("averageScore"), criteria.minScore()));
            }
            if (criteria.maxScore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("averageScore"), criteria.maxScore()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
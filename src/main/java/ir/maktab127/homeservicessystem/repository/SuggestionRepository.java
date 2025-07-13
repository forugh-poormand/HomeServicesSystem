package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.Customer;
import ir.maktab127.homeservicessystem.entity.CustomerOrder;
import ir.maktab127.homeservicessystem.entity.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    public List<Suggestion> findByOrderId(Long orderId);

 public List<Suggestion> findBySpecialistId(Long specialistId);
}

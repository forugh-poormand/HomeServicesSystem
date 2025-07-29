package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.CustomerOrder;
import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long>, JpaSpecificationExecutor<CustomerOrder> {

   List<CustomerOrder> findByCustomerId(Long customerId);

   List<CustomerOrder> findByStatusAndSubServiceIdIn(OrderStatus status, List<Long> subServiceIds);
}


package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.CustomerOrder;
import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
   public List<CustomerOrder> findByCustomerId(Long customerId);
   public List<CustomerOrder> findByOrderStatusAndSubServiceId(OrderStatus orderStatus, Long subServiceId);


}

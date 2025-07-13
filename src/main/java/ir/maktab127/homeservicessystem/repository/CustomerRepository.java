package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>,
        JpaSpecificationExecutor<Customer> {
    public Optional<Customer> findByEmail(String email);
}

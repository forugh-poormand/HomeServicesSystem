package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.SubService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubServiceRepository extends JpaRepository<SubService, Long> {
    public List<SubService> findBySubServiceId(Long subServiceId);

}

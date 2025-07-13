package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;


public interface SpecialistRepository extends JpaRepository<Specialist, Long>,
        JpaSpecificationExecutor<Specialist> {
    Optional<Specialist> findByEmail(String email);
    List<Specialist> findByStatus(SpecialistStatus status);
}

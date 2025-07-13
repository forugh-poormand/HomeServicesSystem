package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
    Optional<Specialist> findByEmail(String email);
    List<Specialist> findByStatus(SpecialistStatus status);
}

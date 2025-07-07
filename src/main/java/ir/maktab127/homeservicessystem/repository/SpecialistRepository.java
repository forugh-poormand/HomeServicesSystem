package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
    public Optional<Specialist> findByEmail(String email);
}

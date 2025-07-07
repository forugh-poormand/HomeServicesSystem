package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.MainService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MainServiceRepository extends JpaRepository<MainService, Long> {
    public Optional<MainService> findByName(String name);
}

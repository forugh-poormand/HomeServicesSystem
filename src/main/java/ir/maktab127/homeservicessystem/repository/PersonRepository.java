package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
}

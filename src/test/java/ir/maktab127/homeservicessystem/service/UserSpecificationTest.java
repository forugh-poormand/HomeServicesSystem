package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.UserSearchCriteriaDto;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.enums.Role;
import ir.maktab127.homeservicessystem.repository.SpecialistRepository;
import ir.maktab127.homeservicessystem.specifications.UserSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserSpecificationTest {

    @Autowired
    private SpecialistRepository specialistRepository;

    @BeforeEach
    void setUp() {
        specialistRepository.deleteAll();

        Specialist specialist1 = new Specialist();
        specialist1.setFirstName("Ali");
        specialist1.setLastName("Alavi");
        specialist1.setEmail("ali@test.com"); // Required field
        specialist1.setPassword("password"); // Required field
        specialist1.setRole(Role.ROLE_SPECIALIST); // Required field
        specialist1.setAverageScore(4.5);

        Specialist specialist2 = new Specialist();
        specialist2.setFirstName("Reza");
        specialist2.setLastName("Razavi");
        specialist2.setEmail("reza@test.com"); // Required field
        specialist2.setPassword("password"); // Required field
        specialist2.setRole(Role.ROLE_SPECIALIST); // Required field
        specialist2.setAverageScore(3.8);

        specialistRepository.saveAll(List.of(specialist1, specialist2));
    }

    @Test
    @DisplayName("Should find specialist by first name")
    void shouldFindSpecialistByFirstName() {
        // Given
        UserSearchCriteriaDto criteria = new UserSearchCriteriaDto(null, "ali", null, null, null, null);
        Specification<Specialist> spec = UserSpecification.getSpecialistSpecification(criteria);

        // When
        List<Specialist> results = specialistRepository.findAll(spec);

        // Then
        assertEquals(1, results.size());
        assertEquals("Ali", results.get(0).getFirstName());
    }

    @Test
    @DisplayName("Should find specialist by minimum score")
    void shouldFindSpecialistByMinScore() {
        // Given
        UserSearchCriteriaDto criteria = new UserSearchCriteriaDto(null, null, null, null, 4.0, null);
        Specification<Specialist> spec = UserSpecification.getSpecialistSpecification(criteria);

        // When
        List<Specialist> results = specialistRepository.findAll(spec);

        // Then
        assertEquals(1, results.size());
        assertTrue(results.get(0).getAverageScore() >= 4.0);
    }

    @Test
    @DisplayName("Should return all specialists when criteria is empty")
    void shouldReturnAllWhenCriteriaIsEmpty() {
        // Given
        UserSearchCriteriaDto criteria = new UserSearchCriteriaDto(null, null, null, null, null, null);
        Specification<Specialist> spec = UserSpecification.getSpecialistSpecification(criteria);

        // When
        List<Specialist> results = specialistRepository.findAll(spec);

        // Then
        assertEquals(2, results.size());
    }
}
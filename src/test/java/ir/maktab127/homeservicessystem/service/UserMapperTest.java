package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.UserRegistrationDto;
import ir.maktab127.homeservicessystem.dto.UserResponseDto;
import ir.maktab127.homeservicessystem.dto.mapper.UserMapper;
import ir.maktab127.homeservicessystem.entity.Customer;
import ir.maktab127.homeservicessystem.entity.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    @DisplayName("Should map UserRegistrationDto to Customer")
    void shouldMapUserRegistrationDtoToCustomer() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto("testF", "testL", "test@email.com", "pass123");

        // When
        Customer customer = UserMapper.toCustomer(dto);

        // Then
        assertEquals(dto.firstName(), customer.getFirstName());
        assertEquals(dto.lastName(), customer.getLastName());
        assertEquals(dto.email(), customer.getEmail());
        assertEquals(dto.password(), customer.getPassword());
    }

    @Test
    @DisplayName("Should map Person to UserResponseDto")
    void shouldMapPersonToUserResponseDto() {
        // Given
        Person person = new Customer();
        person.setId(10L);
        person.setFirstName("testF");
        person.setLastName("testL");
        person.setEmail("test@email.com");

        // When
        UserResponseDto dto = UserMapper.toUserResponseDto(person);

        // Then
        assertEquals(person.getId(), dto.id());
        assertEquals(person.getFirstName(), dto.firstName());
        assertEquals(person.getLastName(), dto.lastName());
        assertEquals(person.getEmail(), dto.email());
    }
}
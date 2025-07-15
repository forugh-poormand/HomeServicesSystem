package ir.maktab127.homeservicessystem.dto.mapper;

import ir.maktab127.homeservicessystem.dto.SpecialistRegistrationDto;
import ir.maktab127.homeservicessystem.dto.UserRegistrationDto;
import ir.maktab127.homeservicessystem.dto.UserResponseDto;
import ir.maktab127.homeservicessystem.entity.Customer;
import ir.maktab127.homeservicessystem.entity.Person;
import ir.maktab127.homeservicessystem.entity.Specialist;

public class UserMapper {

    public static Customer toCustomer(UserRegistrationDto dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.firstName());
        customer.setLastName(dto.lastName());
        customer.setEmail(dto.email());
        customer.setPassword(dto.password());
        return customer;
    }

    public static Specialist toSpecialist(SpecialistRegistrationDto dto) {
        Specialist specialist = new Specialist();
        specialist.setFirstName(dto.firstName());
        specialist.setLastName(dto.lastName());
        specialist.setEmail(dto.email());
        specialist.setPassword(dto.password());
        return specialist;
    }

    public static UserResponseDto toUserResponseDto(Person person) {
        return new UserResponseDto(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getEmail()
        );
    }
}
package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.dto.mapper.OrderHistoryMapper;
import ir.maktab127.homeservicessystem.entity.*;
import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import ir.maktab127.homeservicessystem.exceptions.DuplicateResourceException;
import ir.maktab127.homeservicessystem.exceptions.InvalidOperationException;
import ir.maktab127.homeservicessystem.exceptions.ResourceNotFoundException;
import ir.maktab127.homeservicessystem.repository.*;
import ir.maktab127.homeservicessystem.specifications.OrderSpecification;
import ir.maktab127.homeservicessystem.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final CustomerRepository customerRepository;
    private final SubServiceRepository subServiceRepository;
    private final MainServiceRepository mainServiceRepository;
    private final SpecialistRepository specialistRepository;
    private final CustomerOrderRepository orderRepository;

    @Override
    public MainService createMainService(MainServiceDto dto) {
        mainServiceRepository.findByName(dto.name()).ifPresent(s -> {
            throw new DuplicateResourceException("Main service with this name already exists.");
        });
        MainService mainService = new MainService();
        mainService.setName(dto.name());
        return mainServiceRepository.save(mainService);
    }

    @Override
    public SubService createSubService(SubServiceRequestDto dto) {
        MainService mainService = mainServiceRepository.findById(dto.mainServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Main service not found with id: " + dto.mainServiceId()));

        boolean isDuplicate = mainService.getSubServices().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(dto.name()));
        if (isDuplicate) {
            throw new DuplicateResourceException("Sub-service with this name already exists in this main service.");
        }

        SubService subService = new SubService();
        subService.setName(dto.name());
        subService.setBasePrice(dto.basePrice());
        subService.setDescription(dto.description());
        subService.setMainService(mainService);
        return subServiceRepository.save(subService);
    }

    @Override
    public Specialist confirmSpecialist(Long specialistId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id: " + specialistId));
        specialist.setStatus(SpecialistStatus.CONFIRMED);
        return specialistRepository.save(specialist);
    }

    @Override
    public void assignSpecialistToSubService(Long specialistId, Long subServiceId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id: " + specialistId));
        SubService subService = subServiceRepository.findById(subServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-service not found with id: " + subServiceId));

        specialist.getExpertIn().add(subService);
        specialistRepository.save(specialist);
    }

    @Override
    public void removeSpecialistFromSubService(Long specialistId, Long subServiceId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id: " + specialistId));
        SubService subService = subServiceRepository.findById(subServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-service not found with id: " + subServiceId));

        boolean hasActiveOrder = specialist.getOrders().stream()
                .anyMatch(order -> order.getSubService().equals(subService) &&
                        (order.getStatus() != OrderStatus.DONE && order.getStatus() != OrderStatus.PAID));

        if (hasActiveOrder) {
            throw new InvalidOperationException("Cannot remove specialist from a service with active orders.");
        }

        specialist.getExpertIn().remove(subService);
        specialistRepository.save(specialist);
    }
    public Customer login(LoginRequestDto dto) {
        Customer customer = customerRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));
        if (!customer.getPassword().equals(dto.password())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }
        return customer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Specialist> findAllUnconfirmedSpecialists() {
        return specialistRepository.findAll().stream()
                .filter(s -> s.getStatus() == SpecialistStatus.AWAITING_CONFIRMATION)
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> searchUsers(UserSearchCriteriaDto criteria) {
        List<Person> results = new ArrayList<>();

        if (!StringUtils.hasText(criteria.role()) || "customer".equalsIgnoreCase(criteria.role())) {
            results.addAll(customerRepository.findAll(UserSpecification.getCustomerSpecification(criteria)));
        }

        if (!StringUtils.hasText(criteria.role()) || "specialist".equalsIgnoreCase(criteria.role())) {
            results.addAll(specialistRepository.findAll(UserSpecification.getSpecialistSpecification(criteria)));
        }

        return results;
    }
    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryDto> searchOrderHistory(OrderHistoryFilterDto filterDto) {
        // 2. Use the specification to find orders
        Specification<CustomerOrder> spec = OrderSpecification.filterBy(filterDto);
        List<CustomerOrder> orders = orderRepository.findAll(spec);

        // 3. Map the results to the summary DTO
        return orders.stream()
                .map(OrderHistoryMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailDto getOrderDetails(Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return OrderHistoryMapper.toDetailDto(order);
    }

}
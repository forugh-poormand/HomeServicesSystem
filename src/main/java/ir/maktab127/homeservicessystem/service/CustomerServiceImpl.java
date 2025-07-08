package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.dto.mapper.OrderMapper;
import ir.maktab127.homeservicessystem.dto.mapper.SuggestionMapper;
import ir.maktab127.homeservicessystem.dto.mapper.UserMapper;
import ir.maktab127.homeservicessystem.entity.*;
import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;
import ir.maktab127.homeservicessystem.exceptions.DuplicateResourceException;
import ir.maktab127.homeservicessystem.exceptions.InvalidOperationException;
import ir.maktab127.homeservicessystem.exceptions.ResourceNotFoundException;
import ir.maktab127.homeservicessystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final SubServiceRepository subServiceRepository;
    private final WalletRepository walletRepository;
    private final CustomerOrderRepository orderRepository;
    private final MainServiceRepository mainServiceRepository;
    private final SuggestionRepository suggestionRepository;
    private final CommentRepository commentRepository;
    private final SpecialistRepository specialistRepository;

    @Override
    public Customer register(UserRegistrationDto dto) {
        customerRepository.findByEmail(dto.email()).ifPresent(c -> {
            throw new DuplicateResourceException("Email already exists: " + dto.email());
        });
        Customer customer = UserMapper.toCustomer(dto);
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(1_000_000_000));
        walletRepository.save(wallet);
        customer.setWallet(wallet);
        return customerRepository.save(customer);
    }

    @Override
    public CustomerOrder placeOrder(Long customerId, OrderRequestDto dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        SubService subService = subServiceRepository.findById(dto.subServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Sub-service not found with id: " + dto.subServiceId()));

        if (dto.proposedPrice().compareTo(subService.getBasePrice()) < 0) {
            throw new InvalidOperationException("Proposed price must be equal or greater than the base price.");
        }

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setSubService(subService);
        order.setProposedPrice(dto.proposedPrice());
        order.setDescription(dto.description());
        order.setAddress(dto.address());
        order.setRequestedStartDate(dto.requestedStartDate());
        order.setStatus(OrderStatus.WAITING_FOR_SUGGESTIONS);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MainService> viewAllServices() {
        return mainServiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuggestionResponseDto> viewSuggestionsForOrder(Long customerId, Long orderId, String sortBy) {
        CustomerOrder order = findOrderForCustomer(customerId, orderId);
        List<Suggestion> suggestions = suggestionRepository.findByOrderId(order.getId());

        if (sortBy != null) {
            if (sortBy.equalsIgnoreCase("price")) {
                suggestions.sort(Comparator.comparing(Suggestion::getProposedPrice));
            } else if (sortBy.equalsIgnoreCase("score")) {
                suggestions.sort(Comparator.comparing(s -> s.getSpecialist().getScore(), Comparator.reverseOrder()));
            }
        }
        return suggestions.stream().map(SuggestionMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto selectSuggestion(Long customerId, Long orderId, Long suggestionId) {
        CustomerOrder order = findOrderForCustomer(customerId, orderId);
        if (order.getStatus() != OrderStatus.WAITING_FOR_SELECTION) {
            throw new InvalidOperationException("Order is not in a state to select a suggestion.");
        }
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found with id: " + suggestionId));
        if (!suggestion.getOrder().getId().equals(orderId)) {
            throw new InvalidOperationException("This suggestion does not belong to this order.");
        }
        order.setSelectedSpecialist(suggestion.getSpecialist());
        order.setStatus(OrderStatus.WAITING_FOR_SPECIALIST_TO_COME);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponseDto startOrder(Long customerId, Long orderId) {
        CustomerOrder order = findOrderForCustomer(customerId, orderId);
        if (order.getStatus() != OrderStatus.WAITING_FOR_SPECIALIST_TO_COME) {
            throw new InvalidOperationException("Order cannot be started at this stage.");
        }
        Suggestion selectedSuggestion = order.getSuggestions().stream()
                .filter(s -> s.getSpecialist().equals(order.getSelectedSpecialist()))
                .findFirst()
                .orElseThrow(() -> new InvalidOperationException("Selected specialist's suggestion not found."));
        if (LocalDateTime.now().isBefore(selectedSuggestion.getStartTime())) {
            throw new InvalidOperationException("Cannot start the order before the specialist's proposed start time.");
        }
        order.setStatus(OrderStatus.STARTED);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponseDto completeOrder(Long customerId, Long orderId) {
        CustomerOrder order = findOrderForCustomer(customerId, orderId);
        if (order.getStatus() != OrderStatus.STARTED) {
            throw new InvalidOperationException("Order must be in STARTED state to be completed.");
        }
        order.setStatus(OrderStatus.DONE);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponseDto payForOrder(Long customerId, Long orderId) {
        CustomerOrder order = findOrderForCustomer(customerId, orderId);
        if (order.getStatus() != OrderStatus.DONE) {
            throw new InvalidOperationException("Order must be in DONE state to be paid.");
        }
        Customer customer = order.getCustomer();
        Specialist specialist = order.getSelectedSpecialist();
        BigDecimal amount = order.getProposedPrice();
        if (customer.getWallet().getBalance().compareTo(amount) < 0) {
            throw new InvalidOperationException("Insufficient funds.");
        }
        customer.getWallet().setBalance(customer.getWallet().getBalance().subtract(amount));
        specialist.getWallet().setBalance(specialist.getWallet().getBalance().add(amount));
        walletRepository.save(customer.getWallet());
        walletRepository.save(specialist.getWallet());
        order.setStatus(OrderStatus.PAID);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Comment leaveComment(Long customerId, Long orderId, CommentRequestDto dto) {
        CustomerOrder order = findOrderForCustomer(customerId, orderId);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOperationException("You can only comment on paid orders.");
        }
        commentRepository.findByOrderId(orderId).ifPresent(c -> {
            throw new DuplicateResourceException("A comment already exists for this order.");
        });
        Comment comment = new Comment();
        comment.setOrder(order);
        comment.setScore(dto.score());
        comment.setText(dto.text());
        Comment savedComment = commentRepository.save(comment);
        Specialist specialist = order.getSelectedSpecialist();
        specialist.setTotalScore(specialist.getTotalScore() + dto.score());
        specialist.setReviewCount(specialist.getReviewCount() + 1);
        specialistRepository.save(specialist);
        return savedComment;
    }

    @Override
    public UserResponseDto updateProfile(Long customerId, UserProfileUpdateDto dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        customer.setEmail(dto.email());
        customer.setPassword(dto.password());
        return UserMapper.toUserResponseDto(customerRepository.save(customer));
    }

    private CustomerOrder findOrderForCustomer(Long customerId, Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new InvalidOperationException("Access denied. You are not the owner of this order.");
        }
        return order;
    }
}
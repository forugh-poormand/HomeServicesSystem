package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.SpecialistRegistrationDto;
import ir.maktab127.homeservicessystem.dto.SuggestionRequestDto;
import ir.maktab127.homeservicessystem.dto.UserProfileUpdateDto;
import ir.maktab127.homeservicessystem.dto.UserResponseDto;
import ir.maktab127.homeservicessystem.dto.mapper.UserMapper;
import ir.maktab127.homeservicessystem.entity.*;
import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import ir.maktab127.homeservicessystem.exceptions.*;
import ir.maktab127.homeservicessystem.repository.CustomerOrderRepository;
import ir.maktab127.homeservicessystem.repository.SpecialistRepository;
import ir.maktab127.homeservicessystem.repository.SuggestionRepository;
import ir.maktab127.homeservicessystem.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialistServiceImpl implements SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final WalletRepository walletRepository;
    private final CustomerOrderRepository orderRepository;
    private final SuggestionRepository suggestionRepository;

    @Override
    public Specialist register(SpecialistRegistrationDto dto, MultipartFile profilePicture) {
        specialistRepository.findByEmail(dto.email()).ifPresent(s -> {
            throw new DuplicateResourceException("Email already exists: " + dto.email());
        });
        Specialist specialist = UserMapper.toSpecialist(dto);
        try {
            if (profilePicture != null && !profilePicture.isEmpty()) {
                specialist.setProfilePicture(profilePicture.getBytes());
            }
        } catch (IOException e) {
            throw new InvalidOperationException("Could not process the profile picture.");
        }
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);
        specialist.setWallet(wallet);
        return specialistRepository.save(specialist);
    }

    @Override
    public Suggestion submitSuggestion(Long specialistId, Long orderId, SuggestionRequestDto dto) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id: " + specialistId));

        if (specialist.getStatus() != SpecialistStatus.CONFIRMED) {
            throw new InvalidOperationException("Your account is not confirmed yet.");
        }

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.WAITING_FOR_SUGGESTIONS && order.getStatus() != OrderStatus.WAITING_FOR_SELECTION) {
            throw new InvalidOperationException("Order is not accepting suggestions at the moment.");
        }

        if (!specialist.getExpertIn().contains(order.getSubService())) {
            throw new InvalidOperationException("You are not an expert for this service type.");
        }
        if (dto.proposedPrice() == null || dto.proposedPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Proposed price must be positive.");
        } if (dto.proposedPrice().compareTo(order.getProposedPrice()) < 0) {
            throw new InvalidOperationException("Specialist's price cannot be less than the customer's proposed price.");
        }

        boolean alreadyHasSuggestion = order.getSuggestions().stream()
                .anyMatch(s -> s.getSpecialist().getId().equals(specialistId));
        if (alreadyHasSuggestion) {
            throw new DuplicateResourceException("You have already submitted a suggestion for this order.");
        }

        Suggestion suggestion = new Suggestion();
        suggestion.setSpecialist(specialist);
        suggestion.setOrder(order);
        suggestion.setProposedPrice(dto.proposedPrice());
        suggestion.setDurationInHours(dto.durationInHours());
        suggestion.setStartTime(dto.startTime());
        Suggestion savedSuggestion = suggestionRepository.save(suggestion);

        if (order.getStatus() == OrderStatus.WAITING_FOR_SUGGESTIONS) {
            order.setStatus(OrderStatus.WAITING_FOR_SELECTION);
            orderRepository.save(order);
        }

        return savedSuggestion;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerOrder> findAvailableOrders(Long specialistId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id: " + specialistId));
        if (specialist.getExpertIn().isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> subServiceIds = specialist.getExpertIn().stream()
                .map(SubService::getId)
                .collect(Collectors.toList());
        return orderRepository.findByStatusAndSubServiceIdIn(OrderStatus.WAITING_FOR_SUGGESTIONS, subServiceIds);
    }

    @Override
    public UserResponseDto updateProfile(Long specialistId, UserProfileUpdateDto dto) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id: " + specialistId));


        boolean hasActiveOrders = specialist.getOrders().stream()
                .anyMatch(order -> order.getStatus() != OrderStatus.DONE && order.getStatus() != OrderStatus.PAID);

        if (hasActiveOrders) {
            throw new InvalidOperationException("Cannot update profile with active orders. Please complete your current jobs first.");
        }

        specialist.setEmail(dto.email());
        specialist.setPassword(dto.password());
        specialist.setStatus(SpecialistStatus.AWAITING_CONFIRMATION);

        return UserMapper.toUserResponseDto(specialistRepository.save(specialist));
    }
}
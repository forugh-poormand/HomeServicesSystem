package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.dto.mapper.OrderMapper;
import ir.maktab127.homeservicessystem.dto.mapper.TransactionMapper;
import ir.maktab127.homeservicessystem.dto.mapper.UserMapper;
import ir.maktab127.homeservicessystem.entity.*;
import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import ir.maktab127.homeservicessystem.exceptions.*;
import ir.maktab127.homeservicessystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialistServiceImpl implements SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final CustomerOrderRepository orderRepository;
    private final SuggestionRepository suggestionRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public Specialist register(SpecialistRegistrationDto dto) {
        specialistRepository.findByEmail(dto.email()).ifPresent(s -> {
            throw new DuplicateResourceException("Email already exists: " + dto.email());
        });
        Specialist specialist = UserMapper.toSpecialist(dto);
        Wallet wallet = new Wallet();
        specialist.setWallet(wallet);

        if (dto.imagePath() != null && !dto.imagePath().isBlank()) {
            try {
                byte[] image = readImageFromPath(dto.imagePath());
                if (image.length > 300 * 1024) { // 300 KB
                    throw new ImageLengthOutOfBoundException("Image size cannot exceed 300 KB.");
                }
                specialist.setProfilePicture(image);
                specialist.setStatus(SpecialistStatus.AWAITING_CONFIRMATION);
            } catch (IOException e) {
                throw new InvalidOperationException("Could not process the profile picture.");
            }
        } else {
            specialist.setStatus(SpecialistStatus.NEW_AWAITING_PICTURE);
        }

        return specialistRepository.save(specialist);
    }

    private byte[] readImageFromPath(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("File not found at path: " + filePath);
        }
        return Files.readAllBytes(path);
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
        }
        if (dto.proposedPrice().compareTo(order.getProposedPrice()) < 0) {
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

        if (dto.email() != null && !dto.email().isBlank()) {
            specialistRepository.findByEmail(dto.email()).ifPresent(s -> {
                if (!s.getId().equals(specialistId)) {
                    throw new DuplicateResourceException("Email already exists: " + dto.email());
                }
            });
            specialist.setEmail(dto.email());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            specialist.setPassword(dto.password());
        }

        if (dto.imagePath() != null && !dto.imagePath().isBlank()) {
            try {
                byte[] pictureBytes = readImageFromPath(dto.imagePath());
                if (pictureBytes.length > 300 * 1024) { // 300 KB
                    throw new ImageLengthOutOfBoundException("Image size cannot exceed 300 KB.");
                }
                specialist.setProfilePicture(pictureBytes);

                if (specialist.getStatus() == SpecialistStatus.NEW_AWAITING_PICTURE) {
                    specialist.setStatus(SpecialistStatus.AWAITING_CONFIRMATION);
                }
            } catch (IOException e) {
                throw new InvalidOperationException("Could not process the profile picture.");
            }
        }

        Specialist savedSpecialist = specialistRepository.save(specialist);
        return UserMapper.toUserResponseDto(savedSpecialist);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrderHistory(Long specialistId) {
        specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found"));

        return suggestionRepository.findBySpecialistId(specialistId).stream()
                .map(Suggestion::getOrder)
                .distinct()
                .map(OrderMapper::toDto) // Convert to DTO
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ScoreDto getOrderScore(Long specialistId, Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getSelectedSpecialist() == null || !order.getSelectedSpecialist().getId().equals(specialistId)) {
            throw new InvalidOperationException("You do not have access to this order's score.");
        }

        Comment comment = order.getComment();
        if (comment == null) {
            throw new ResourceNotFoundException("No comment found for this order.");
        }

        return new ScoreDto(orderId, comment.getScore());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getWalletHistory(Long specialistId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id: " + specialistId));

        return transactionRepository.findByWalletIdOrderByTransactionDateDesc(specialist.getWallet().getId())
                .stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AverageScoreDto getAverageScore(Long specialistId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id: " + specialistId));

        return new AverageScoreDto(specialist.getAverageScore());
    }
}
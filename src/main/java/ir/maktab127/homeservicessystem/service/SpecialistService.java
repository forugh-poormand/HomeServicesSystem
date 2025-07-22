package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.entity.CustomerOrder;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.Suggestion;
import ir.maktab127.homeservicessystem.entity.Transaction;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface SpecialistService {
    Specialist register(SpecialistRegistrationDto dto);
    Suggestion submitSuggestion(Long specialistId, Long orderId, SuggestionRequestDto dto);
    List<CustomerOrder> findAvailableOrders(Long specialistId);
    UserResponseDto updateProfile(Long specialistId, UserProfileUpdateDto dto);
    List<OrderResponseDto> getOrderHistory(Long specialistId);
    ScoreDto getOrderScore(Long specialistId, Long orderId);
    List<TransactionDto> getWalletHistory(Long specialistId);
    AverageScoreDto getAverageScore(Long specialistId);

}
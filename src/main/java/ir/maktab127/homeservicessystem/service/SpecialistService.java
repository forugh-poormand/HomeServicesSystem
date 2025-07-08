package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.SpecialistRegistrationDto;
import ir.maktab127.homeservicessystem.dto.SuggestionRequestDto;
import ir.maktab127.homeservicessystem.dto.UserProfileUpdateDto;
import ir.maktab127.homeservicessystem.dto.UserResponseDto;
import ir.maktab127.homeservicessystem.entity.CustomerOrder;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.Suggestion;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface SpecialistService {
    Specialist register(SpecialistRegistrationDto dto, MultipartFile profilePicture);
    Suggestion submitSuggestion(Long specialistId, Long orderId, SuggestionRequestDto dto);
    List<CustomerOrder> findAvailableOrders(Long specialistId);
    UserResponseDto updateProfile(Long specialistId, UserProfileUpdateDto dto);
}
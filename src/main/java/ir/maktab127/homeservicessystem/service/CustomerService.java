package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.*;
import ir.maktab127.homeservicessystem.entity.*;
import java.util.List;

public interface CustomerService {
    Customer register(UserRegistrationDto dto);
    CustomerOrder placeOrder(Long customerId, OrderRequestDto dto);
    List<MainService> viewAllServices();
    List<SuggestionResponseDto> viewSuggestionsForOrder(Long customerId, Long orderId, String sortBy);
    OrderResponseDto selectSuggestion(Long customerId, Long orderId, Long suggestionId);
    OrderResponseDto startOrder(Long customerId, Long orderId);
    OrderResponseDto completeOrder(Long customerId, Long orderId);
    OrderResponseDto payForOrder(Long customerId, Long orderId);
    Comment leaveComment(Long customerId, Long orderId, CommentRequestDto dto);
    UserResponseDto updateProfile(Long customerId, UserProfileUpdateDto dto);
}
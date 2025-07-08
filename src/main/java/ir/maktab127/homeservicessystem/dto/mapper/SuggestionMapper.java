package ir.maktab127.homeservicessystem.dto.mapper;

import ir.maktab127.homeservicessystem.dto.SuggestionRequestDto;
import ir.maktab127.homeservicessystem.dto.SuggestionResponseDto;
import ir.maktab127.homeservicessystem.entity.Suggestion;

public class SuggestionMapper {
    public static Suggestion toEntity(SuggestionRequestDto dto) {
        Suggestion suggestion = new Suggestion();
        suggestion.setProposedPrice(dto.proposedPrice());
        suggestion.setDurationInHours(dto.durationInHours());
        suggestion.setStartTime(dto.startTime());
        return suggestion;
    }

    public static SuggestionResponseDto toDto(Suggestion suggestion) {
        return new SuggestionResponseDto(
                suggestion.getId(),
                suggestion.getSpecialist().getFirstName() + " " + suggestion.getSpecialist().getLastName(),
                suggestion.getProposedPrice(),
                suggestion.getDurationInHours(),
                suggestion.getStartTime()
        );
    }
}

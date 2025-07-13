package ir.maktab127.homeservicessystem.dto;

public record UserSearchCriteriaDto(
        String role,
        String firstName,
        String lastName,
        Long subServiceId,
        Double minScore,
        Double maxScore
) {}

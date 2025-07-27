package ir.maktab127.homeservicessystem.dto.mapper;

import ir.maktab127.homeservicessystem.dto.TransactionDto;
import ir.maktab127.homeservicessystem.entity.Transaction;

public class TransactionMapper {
    public static TransactionDto toDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDescription(),
                transaction.getTransactionDate()
        );
    }
}
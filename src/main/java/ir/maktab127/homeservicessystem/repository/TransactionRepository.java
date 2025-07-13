package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletIdOrderByTransactionDateDesc(Long walletId);
}
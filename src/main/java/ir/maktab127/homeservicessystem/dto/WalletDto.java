package ir.maktab127.homeservicessystem.dto;

import java.math.BigDecimal;

public record WalletDto(
        Long walletId,
        BigDecimal balance
) {
}

package org.example.validators;
import org.example.entity.BankAccount;
import org.example.exceptions.TransferException;

import java.math.BigDecimal;

public final class TransferValidator {

    public static void validateCheck(BankAccount source, BigDecimal amount) {
        validateAmountTransfer(source, amount);
        validateAmount(amount);
    }

    private static void validateAmountTransfer(BankAccount source, BigDecimal amount) {
        if (source.getBalance().compareTo(amount) <= 0) {
            throw new TransferException("Недостаточно средств для перевода");
        }
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("Сумма перевода меньше нуля");
        }
    }
}

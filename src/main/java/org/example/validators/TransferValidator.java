package org.example.validators;
import org.example.entity.BankAccount;
import org.example.exceptions.TransferException;

import java.math.BigDecimal;

public class TransferValidator {

    public TransferValidator() {}

    /**
     * Метод вызывает два других метода на проверку валидации данных
     * @param source счет с которого проводится транзакция
     * @param amount сумма денег в транзакции
     */
    public void checkTransfer(BankAccount source, BigDecimal amount) {
        checkBalanceCompareToAmount(source, amount);
        validateAmount(amount);
    }

    /**
     * Метод проверяет достаточная ли сумма для перевода
     * @param source счет с которого проводится транзакция
     * @param amount сумма денег в транзакции
     */
    public void checkBalanceCompareToAmount(BankAccount source, BigDecimal amount) {
        if (source.getBalance().compareTo(amount) <= 0) {
            throw new TransferException("Недостаточно средств для перевода");
        }
    }

    /**
     * Метод валидирует сумму перевода
     * @param amount сумма денег в транзакции
     */
    public void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("Сумма перевода меньше нуля");
        }
    }
}

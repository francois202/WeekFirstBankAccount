package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.enums.CategoryType;
import org.example.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class Transaction {
    /**
     * Идентификатор транзакции
     */
    private final String id;
    /**
     * Сумма денег в транзакции
     */
    private final BigDecimal amount;
    /**
     * Тип транзакции
     */
    private final TransactionType type;
    /**
     * Тип категории
     */
    private final CategoryType category;
    /**
     * Дата и время транзакции
     */
    private final LocalDateTime date;
    /**
     * Счет с которого проводится транзакция
     */
    private final BankAccount sourceAccount;
    /**
     * Счет на который делается транзакция
     */
    private final BankAccount targetAccount;

    /**
     * Конструктор класса Transaction
     * @param amount сумма денег в транзакции
     * @param type тип транзакции
     * @param sourceAccount счет с которого проводится транзакция
     * @param targetAccount счет на который делается транзакция
     */
    public Transaction(BigDecimal amount, TransactionType type, CategoryType category, BankAccount sourceAccount, BankAccount targetAccount) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = LocalDateTime.now();
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
    }

    /**
     * Переопределенный метод toString() для вывода информации о транзакции
     * @return информация о транзакции
     */
    @Override
    public String toString() {
        return String.format("%s || %s || %s || %s || %s || %s",
                id, amount, type, date,
                sourceAccount != null ? sourceAccount.getAccountNumber() : "null",
                targetAccount != null ? targetAccount.getAccountNumber() : "null");
    }
}

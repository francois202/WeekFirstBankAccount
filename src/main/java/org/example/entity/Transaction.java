package org.example.entity;

import org.example.enums.TranscationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    /**
     * Айди транзакции
     */
    private final String id;
    /**
     * Сумма денег в транзакции
     */
    private final BigDecimal amount;
    /**
     * Тип транзакции
     */
    private final TranscationType type;
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
     * @param id - айди транзакции
     * @param amount - сумма денег в транзакции
     * @param type - тип транзакции
     * @param sourceAccount - счет с которого проводится транзакция
     * @param targetAccount - счет на который делается транзакция
     */
    public Transaction(String id, BigDecimal amount, TranscationType type, BankAccount sourceAccount, BankAccount targetAccount) {
        this.id = id;
        this.amount = amount;
        this.type = type;
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
                id, amount, type, date, sourceAccount.getOwner(),
                targetAccount != null ? targetAccount.getOwner() : "");
    }
}

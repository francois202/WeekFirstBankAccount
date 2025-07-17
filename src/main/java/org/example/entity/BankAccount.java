package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.enums.TransactionType;
import org.example.validators.TransferValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class BankAccount {
    /**
     * Уникальный номер счета
     */
    private final String accountNumber;
    /**
     * Текущий баланс счета в валюте
     */
    private BigDecimal balance;
    /**
     * Владелец банковского счета
     */
    private final User owner;
    /**
     * История транзакций
     */
    private final List<Transaction> transactions;

    private final TransferValidator transferValidator = new TransferValidator();

    /**
     * Конструктор класса BankAccount
     * @param accountNumber номер счета
     * @param owner владелец счета
     */
    public BankAccount(String accountNumber, User owner) {
        this.accountNumber = accountNumber;
        this.balance = BigDecimal.ZERO;
        this.owner = owner;
        this.transactions = new ArrayList<>();
    }

    /**
     * Метод пополнения счета
     * @param amount кол-во денег для депозита
     */
    public void deposit(BigDecimal amount) {
        transferValidator.validateAmount(amount);
        Transaction transaction = new Transaction(amount, TransactionType.DEPOSIT, null,null, this);
        balance = balance.add(amount);
        transactions.add(transaction);
    }

    /**
     * Метод вывода денег со счета
     * @param amount кол-во денег для вывода
     */
    public void withdraw(BigDecimal amount) {
        transferValidator.checkTransfer(this, amount);
        Transaction transaction = new Transaction(amount, TransactionType.WITHDRAWAL, null, this, null);
        balance = balance.subtract(amount);
        transactions.add(transaction);
    }

    /**
     * Метод добавляет транзакцию в историю по счету
     * @param transaction транзакция
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Метод возвращает историю транзакицй по счету
     * @return копия истории транзакций
     */
    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }
}

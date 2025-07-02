package org.example.entity;

import org.example.enums.TranscationType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BankAccount {
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

    /**
     * Конструктор класса BankAccount
     * @param accountNumber - номер счета
     * @param owner - владелец счета
     */
    public BankAccount(String accountNumber, User owner) {
        this.balance = BigDecimal.ZERO;
        this.owner = owner;
        this.transactions = new ArrayList<>();
    }

    /**
     * Метод пополнения счета
     * @param amount - кол-во денег для депозита
     */
    public void deposit(BigDecimal amount) {
        Transaction transaction = new Transaction("id", amount, TranscationType.DEPOSIT,this, null);
        balance = balance.add(amount);
        transactions.add(transaction);
    }

    /**
     * Метод вывода денег со счета
     * @param amount - кол-во денег для вывода
     */
    public void withdraw(BigDecimal amount) {
        Transaction transaction = new Transaction("id", amount, TranscationType.WITHDRAWAL, this, null);
        balance = balance.subtract(amount);
        transactions.add(transaction);
    }

    /**
     * Метод возвращает баланс на счете пользователя
     * @return баланс на счете
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Метод добавляет транзакцию в историю по счету
     * @param transaction - транзакция
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

    /**
     * Метод возвращает владельца счета
     * @return владелец счета
     */
    public String getOwner() {
        return owner.getName();
    }
}

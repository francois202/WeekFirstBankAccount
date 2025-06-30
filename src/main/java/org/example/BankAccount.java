package org.example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BankAccount {
    private String accountNumber;
    private BigDecimal balance;
    private User owner;
    private List<Transaction> transactions;

    public BankAccount(String accountNumber, User owner) {
        this.accountNumber = accountNumber;
        this.balance = BigDecimal.valueOf(0);
        this.owner = owner;
        this.transactions = new ArrayList<>();
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public String getOwner() {
        return owner.getName();
    }
}

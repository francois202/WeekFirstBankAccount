package org.example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BankService {

    public void createAccount(User user, String accountNumber) {
        BankAccount bankAccount = new BankAccount(accountNumber, user);
        user.addAccount(bankAccount);
    }

    public void transfer(BankAccount source, BankAccount target, BigDecimal amount) {
        Transaction transaction = new Transaction("id", amount, "TRANSFER",
                LocalDateTime.now(), source, target);

        if (source.getBalance().compareTo(amount) > 0) {
            source.withdraw(amount);
            target.deposit(amount);

            source.addTransaction(transaction);
            target.addTransaction(transaction);
        } else {
            System.out.println("Недостаточно средств для перевода");
        }
    }

    public List<Transaction> getTransactionHistory(BankAccount account) {
        return account.getTransactions();
    }

    public BigDecimal getTotalBalance(User user) {
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (BankAccount account : user.getAccounts()) {
            totalBalance = totalBalance.add(account.getBalance());
        }

        return totalBalance;
    }

}

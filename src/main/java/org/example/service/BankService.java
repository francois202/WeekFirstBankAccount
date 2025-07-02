package org.example.service;

import org.example.entity.BankAccount;
import org.example.entity.Transaction;
import org.example.entity.User;
import org.example.enums.TranscationType;
import org.example.validators.TransferValidator;

import java.math.BigDecimal;
import java.util.List;

public class BankService {

    /**
     * Метод создает новый счет
     * @param user - имя пользователя
     * @param accountNumber - номер счета
     */
    public void createAccount(User user, String accountNumber) {
        BankAccount bankAccount = new BankAccount(accountNumber, user);
        user.addAccount(bankAccount);
    }

    /**
     * Метод создает новую транзакцию
     * @param source - счет источник
     * @param target - целевой счет, куда осуществляется перевод
     * @param amount - сумма денег в переводе
     */
    public void transfer(BankAccount source, BankAccount target, BigDecimal amount) {
        Transaction transaction = new Transaction("id", amount, TranscationType.TRANSFER, source, target);

        TransferValidator.validateCheck(source, amount);

        source.withdraw(amount);
        target.deposit(amount);

        source.addTransaction(transaction);
        target.addTransaction(transaction);
    }

    /**
     Метод возвращает историю транзакций для указанного счета.
     @param account счет пользователя
     @return история транзакций
     */
    public List<Transaction> getTransactionHistory(BankAccount account) {
        return account.getTransactions();
    }

    /**
     Метод возвращает общий баланс всех счетов пользователя.
     @param user экземпляр пользователя
     @return общий баланс
     */
    public BigDecimal getTotalBalance(User user) {
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (BankAccount account : user.getAccounts()) {
            totalBalance = totalBalance.add(account.getBalance());
        }

        return totalBalance;
    }

}

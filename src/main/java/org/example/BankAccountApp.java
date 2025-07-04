package org.example;

import org.example.entity.BankAccount;
import org.example.entity.User;
import org.example.service.BankService;

import java.math.BigDecimal;
import java.util.List;

public class BankAccountApp {
    public static void main(String[] args) {
        User user = new User("user1", "Джон");

        BankService bankService = new BankService();

        bankService.createAccount(user, "ACC123");
        bankService.createAccount(user, "ACC456");

        List<BankAccount> accounts = user.getAccounts();
        BankAccount acc1 = accounts.get(0);
        BankAccount acc2 = accounts.get(1);

        acc1.deposit(new BigDecimal("1000"));

        System.out.println("Баланс счета ACC123: " + acc1.getBalance());
        System.out.println("Баланс счета ACC456: " + acc2.getBalance());
        System.out.println();

        bankService.transfer(acc1, acc2, new BigDecimal("600"));
        bankService.transfer(acc1, acc2, new BigDecimal("200"));

        System.out.println("Баланс счета ACC123: " + acc1.getBalance());
        System.out.println("Баланс счета ACC456: " + acc2.getBalance());

        System.out.println();
        System.out.println("История транзакций для счета ACC123:");
        bankService.getTransactionHistory(acc1).forEach(System.out::println);

        System.out.println();
        System.out.println("Общий баланс для ACC123:");
        System.out.println(bankService.getTotalBalance(user));
    }
}

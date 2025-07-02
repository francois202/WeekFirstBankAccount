package org.example.entity;

import java.util.ArrayList;
import java.util.List;

public class User {
    /**
     * Имя пользователя
     */
    private final String name;
    /**
     * Список счетов, закрепленных за пользователем
     */
    private final List<BankAccount> accounts;

    /**
     * Конструктор класса User
     * @param id - айди пользователя
     * @param name - имя пользователя
     */
    public User(String id, String name) {
        this.name = name;
        this.accounts = new ArrayList<>();
    }

    /**
     * Метод добавляет новый счет для пользователя
     * @param account - экземляр счета
     */
    public void addAccount(BankAccount account) {
        accounts.add(account);
    }

    /**
     * Метод возвращает список счетов, закрепленных за пользователем
     * @return список счетов пользователя
     */
    public List<BankAccount> getAccounts() {
        return accounts;
    }

    /**
     * Метод возвращает имя пользователя
     * @return имя пользователя
     */
    public String getName() {
        return name;
    }
}

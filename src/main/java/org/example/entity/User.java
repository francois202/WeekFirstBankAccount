package org.example.entity;

import java.util.ArrayList;
import java.util.List;

public class User {
    /**
     * Идентификатор пользователя
     */
    private final String id;
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
     * @param id идентификатор пользователя
     * @param name имя пользователя
     */
    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.accounts = new ArrayList<>();
    }

    /**
     * Метод добавляет новый счет для пользователя
     * @param account экземляр счета
     */
    public void addAccount(BankAccount account) {
        accounts.add(account);
    }

    /**
     * Метод возвращает список счетов, закрепленных за пользователем
     * @return список счетов пользователя
     */
    public List<BankAccount> getAccounts() {
        return List.copyOf(accounts);
    }
}

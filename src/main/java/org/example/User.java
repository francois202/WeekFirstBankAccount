package org.example;

import java.util.List;

public class User {
    private String id;
    private String name;
    private List<BankAccount> accounts;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addAccount(BankAccount account) {
        accounts.add(account);
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }
}

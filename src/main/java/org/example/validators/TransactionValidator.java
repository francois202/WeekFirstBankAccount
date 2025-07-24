package org.example.validators;

import org.example.entity.BankAccount;
import org.example.entity.User;
import org.example.enums.CategoryType;

import java.util.Set;

public class TransactionValidator {

    /**
     * Метод проверяет существует ли передаваемая категория
     * @param category передаваемая категория
     */
    public static boolean isValidCategory(String category) {
        if (category == null || category.isEmpty()) {
            return false;
        }
        for (CategoryType categoryType : CategoryType.values()) {
            if (categoryType.name().equals(category)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Метод проверяет существуют ли передаваемые категории
     * @param categories множество передаваемых категорий
     */
    public static boolean isValidCategories(Set<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return false;
        }
        for (String category : categories) {
            if (isValidCategory(category)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Метод проверяет если пользователь равен null или у него нет аккаунтов
     * @param user пользователь
     */
    public static boolean hasUserActiveAccounts(User user) {
        if (user == null || user.getAccounts().isEmpty()) {
            return true;
        }

        for (BankAccount account : user.getAccounts()) {
            if (!account.getTransactions().isEmpty()) {
                return true;
            }
        }
        return false;
    }

}

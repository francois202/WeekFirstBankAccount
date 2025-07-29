package org.example.service;

import org.example.entity.Transaction;
import org.example.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.function.*;

import static org.example.validators.TransactionValidator.hasUserAccountsWithTransactions;

public class TransactionService {

    /**
     * Фильтрует транзакции пользователя по условию
     *
     * @param user пользователь
     * @param predicate интерфейс условие фильтрации
     * @return список транзакций, удовлетворяющих условию
     */
    public List<Transaction> filterTransactions(User user, Predicate<Transaction> predicate) {

        if (!hasUserAccountsWithTransactions(user)) {
            return Collections.emptyList();
        }

        return user.getAccounts().stream()
                .flatMap(bankAccount -> bankAccount.getTransactions().stream())
                .filter(predicate)
                .toList();
    }

    /**
     * Преобразует транзакции пользователя в строковое представление
     *
     * @param user пользователь
     * @param function интерфейс функция преобразования
     * @return список строковых представлений транзакций
     */
    public List<String> transformTransactions(User user, Function<Transaction, String> function) {

        if (!hasUserAccountsWithTransactions(user)) {
            return Collections.emptyList();
        }

        return user.getAccounts().stream()
                .flatMap(bankAccount -> bankAccount.getTransactions().stream())
                .map(function)
                .toList();
    }

    /**
     * Обрабатывает транзакции пользователя с использованием Consumer
     *
     * @param user пользователь
     * @param consumer интерфейс функция обработки
     */

    public void processTransactions(User user, Consumer<Transaction> consumer) {

        if (!hasUserAccountsWithTransactions(user)) {
            return;
        }

        user.getAccounts().stream()
                .flatMap(bankAccount -> bankAccount.getTransactions().stream())
                .forEach(consumer);
    }

    /**
     * Создаёт список транзакций с использованием Supplier
     *
     * @param supplier интерфейс поставщик
     * @return созданный список транзакций
     */
    public List<Transaction> createTransactionList(Supplier<List<Transaction>> supplier) {
        return supplier.get();
    }

    /**
     * Создаёт объединенный список транзакций с помощью BiFunction
     * @param list1 первый список транзакций
     * @param list2 второй список транзакций
     * @param merger интерфейс по объединению
     * @return объединенный список транзакций
     */
    public List<Transaction> mergeTransactionLists(List<Transaction> list1, List<Transaction> list2,
                                                   BiFunction<List<Transaction>, List<Transaction>, List<Transaction>> merger) {
        return merger.apply(list1, list2);
    }

}

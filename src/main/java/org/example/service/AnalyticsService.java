package org.example.service;

import org.example.entity.BankAccount;
import org.example.entity.Transaction;
import org.example.entity.User;
import org.example.enums.CategoryType;
import org.example.enums.TransactionType;
import org.example.validators.TransferValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.example.validators.TransactionValidator.*;

public class AnalyticsService {

    TransferValidator transferValidator = new TransferValidator();

    LocalDateTime dateTimeMonthAgo = LocalDateTime.now().minusMonths(1);
    /**
     * Метод создает новый счет
     *
     * @param user имя пользователя
     * @param accountNumber номер счета
     */
    public void createAccount(User user, String accountNumber) {
        BankAccount bankAccount = new BankAccount(accountNumber, user);
        user.addAccount(bankAccount);
    }

    /**
     * Метод создает новый платеж.
     *
     * @param source счет источник
     * @param category категория платежа
     * @param amount сумма денег при платеже
     */
    public void payment(BankAccount source, String category, BigDecimal amount) {
        transferValidator.checkTransfer(source, amount);

        source.withdraw(amount);

        Transaction transaction = new Transaction(amount, TransactionType.PAYMENT, CategoryType.valueOf(category), source, null);

        source.addTransaction(transaction);
    }

    /**
     * Метод возвращает сумму потраченных средств на указанную категорию за последний месяц.
     *
     * @param bankAccount счет в банке
     * @param category категория по платежу
     * @return сумма потраченных средств по категории за последний месяц
     */
    public BigDecimal getMonthlySpendingByCategory(BankAccount bankAccount, String category) {

        BigDecimal monthlySpending = BigDecimal.ZERO;

        if (!isValidCategory(category) || bankAccount == null) {
            return BigDecimal.ZERO;
        }

        for (Transaction transaction : bankAccount.getTransactions()) {
            if (TransactionType.PAYMENT.equals(transaction.getType()) && category.equals(transaction.getCategory().name())
                && transaction.getDate().isAfter(dateTimeMonthAgo)) {
                monthlySpending = monthlySpending.add(transaction.getAmount());
            }
        }

        return monthlySpending;
    }

    /**
     * Метод возвращает Map, где ключом является категория, а значением — сумма потраченных средств за последний месяц.
     *
     * @param user пользователь
     * @param categories категории
     * @return Map, где ключом является категория, а значением — сумма потраченных средств
     */
    public Map<String, BigDecimal> getMonthlySpendingByCategories(User user, Set<String> categories) {
        Map<String, BigDecimal> resultMap = new HashMap<>();

        if (user == null || !isValidCategories(categories)) {
            return resultMap;
        }

        for (BankAccount bankAccount : user.getAccounts()) {
            for (Transaction transaction : bankAccount.getTransactions()) {
                if (TransactionType.PAYMENT.equals(transaction.getType()) && transaction.getDate().isAfter(dateTimeMonthAgo)) {
                    resultMap.merge(String.valueOf(transaction.getCategory()), transaction.getAmount(), BigDecimal::add);
                }
            }
        }
        return resultMap;
    }

    /**
     * Компаратор для сортировки транзакций по сумме (от большей к меньшей)
     */
    public static class TransactionAmountComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction t1, Transaction t2) {
            return t2.getAmount().compareTo(t1.getAmount());
        }
    }

    /**
     * Метод возвращает LinkedHashMap, где ключом является категория, а значением — список транзакций,
     * отсортированных по сумме от наибольшей к наименьшей.
     *
     * @param user пользователь
     * @return LinkedHashMap, где ключом является категория, а значением — список транзакций
     */
    public LinkedHashMap<String, List<Transaction>> getTransactionHistorySortedByAmount(User user) {
        LinkedHashMap<String, List<Transaction>> resultMap = new LinkedHashMap<>();

        if (!hasUserAccountsWithTransactions(user)) {
            return resultMap;
        }

        List<Transaction> allPayments = new ArrayList<>();
        for (BankAccount bankAccount : user.getAccounts()) {
            for (Transaction transaction : bankAccount.getTransactions()) {
                if (TransactionType.PAYMENT.equals(transaction.getType())) {
                    allPayments.add(transaction);
                }
            }
        }

        for (Transaction transaction : allPayments) {
            if (!resultMap.containsKey(transaction.getCategory().name())) {
                resultMap.put(transaction.getCategory().name(), new ArrayList<>());
            }
            resultMap.get(transaction.getCategory().name()).add(transaction);
        }

        for (List<Transaction> transactions : resultMap.values()) {
            transactions.sort(new TransactionAmountComparator());
        }

        return resultMap;
    }

    /**
     * Метод возвращает последние N транзакций пользователя
     *
     * @param user пользователь
     * @param n N транзакций пользователя
     * @return список последних N транзакций пользователя
     */
    public List<Transaction> getLastNTransactions(User user, int n) {
        List<Transaction> listResult = new ArrayList<>();

        if (!hasUserAccountsWithTransactions(user)) {
            return listResult;
        }

        for (BankAccount account : user.getAccounts()) {
            listResult.addAll(account.getTransactions());
        }

        listResult.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        return listResult.subList(Math.max(0, listResult.size() - n), listResult.size());
    }

    /**
     * Метод возвращает PriorityQueue, содержащую топ-N самых больших транзакций.
     *
     * @param user пользователь
     * @param n кол-во транзакций
     * @return PriorityQueue, содержащую топ-N самых больших транзакций
     */
    public PriorityQueue<Transaction> getTopNLargestTransactions(User user, int n) {
        PriorityQueue<Transaction> queueAllPayments = new PriorityQueue<>(new TransactionAmountComparator());

        if (!hasUserAccountsWithTransactions(user)) {
            return queueAllPayments;
        }

        for (BankAccount bankAccount : user.getAccounts()) {
            for (Transaction transaction : bankAccount.getTransactions()) {
                if (TransactionType.PAYMENT.equals(transaction.getType())) {
                    queueAllPayments.add(transaction);
                }
            }
        }

        PriorityQueue<Transaction> queueResult = new PriorityQueue<>(new TransactionAmountComparator());
        int count = Math.min(n, queueAllPayments.size());
        for (int i = 0; i < count; i++) {
            queueResult.add(queueAllPayments.poll());
        }

        return queueResult;
    }
}

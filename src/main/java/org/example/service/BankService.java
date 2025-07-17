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

public class BankService {

    TransferValidator transferValidator = new TransferValidator();
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
     * Метод создает новую транзакцию
     *
     * @param source счет источник
     * @param target целевой счет, куда осуществляется перевод
     * @param amount сумма денег в переводе
     */
    public void transfer(BankAccount source, BankAccount target, BigDecimal amount) {
        transferValidator.checkTransfer(source, amount);

        source.withdraw(amount);
        target.deposit(amount);

        Transaction transaction = new Transaction(amount, TransactionType.TRANSFER, null, source, target);

        source.addTransaction(transaction);
        target.addTransaction(transaction);
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
     * Метод возвращает историю транзакций для указанного счета.
     *
     * @param account счет пользователя
     * @return история транзакций
     */
    public List<Transaction> getTransactionHistory(BankAccount account) {
        return account.getTransactions();
    }

    /**
     * Метод возвращает общий баланс всех счетов пользователя.
     *
     * @param user экземпляр пользователя
     * @return общий баланс
     */
    public BigDecimal getTotalBalance(User user) {
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (BankAccount account : user.getAccounts()) {
            totalBalance = totalBalance.add(account.getBalance());
        }

        return totalBalance;
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

        boolean flag = false;
        for (CategoryType categoryType : CategoryType.values()) {
            if (categoryType.name().equals(category)) {
                flag = true;
                break;
            }
        }
        if (!flag || bankAccount == null)
            return BigDecimal.ZERO;

        for (Transaction tr : bankAccount.getTransactions()) {
            if (tr.getType().equals(TransactionType.PAYMENT) && tr.getCategory().name().equals(category)
                && tr.getDate().isAfter(LocalDateTime.now().minusMonths(1))) {
                monthlySpending = monthlySpending.add(tr.getAmount());
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

        if (user == null || categories == null || categories.isEmpty()) {
            return resultMap;
        }

        for (String category : categories) {
            try {
                CategoryType.valueOf(category);
            } catch (IllegalArgumentException e) {
                return resultMap;
            }
        }

        for (BankAccount bankAccount : user.getAccounts()) {
            for (Transaction tr : bankAccount.getTransactions()) {
                if (tr.getType().equals(TransactionType.PAYMENT) && tr.getDate().isAfter(LocalDateTime.now().minusMonths(1))) {
                    if (resultMap.containsKey(tr.getCategory().name()))
                        resultMap.put(tr.getCategory().name(),
                                resultMap.get(tr.getCategory().name()).add(tr.getAmount()));
                    else
                        resultMap.put(tr.getCategory().name(), tr.getAmount());
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

        if (user == null)
            return resultMap;

        List<Transaction> allPayments = new ArrayList<>();
        for (BankAccount bankAccount : user.getAccounts()) {
            for (Transaction tr : bankAccount.getTransactions()) {
                if (tr.getType().equals(TransactionType.PAYMENT))
                    allPayments.add(tr);
            }
        }

        for (Transaction tr : allPayments) {
            if (!resultMap.containsKey(tr.getCategory().name()))
                resultMap.put(tr.getCategory().name(), new ArrayList<>());
            resultMap.get(tr.getCategory().name()).add(tr);
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

        if (user == null || user.getAccounts().isEmpty())
            return listResult;

        boolean flag = true;

        for (BankAccount account : user.getAccounts()) {
            if (!account.getTransactions().isEmpty()) {
                flag = false;
                break;
            }
        }
        if (flag) return listResult;

        for (BankAccount account : user.getAccounts()) {
            listResult.addAll(account.getTransactions());
        }
        if (listResult.size() > n)
            return listResult.subList(listResult.size() - n, listResult.size());
        else
            return listResult;
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

        if (user == null || user.getAccounts().isEmpty())
            return queueAllPayments;

        boolean flag = true;

        for (BankAccount account : user.getAccounts()) {
            if (!account.getTransactions().isEmpty()) {
                flag = false;
                break;
            }
        }
        if (flag) return queueAllPayments;

        for (BankAccount bankAccount : user.getAccounts()) {
            for (Transaction tr : bankAccount.getTransactions()) {
                if (tr.getType().equals(TransactionType.PAYMENT))
                    queueAllPayments.add(tr);
            }
        }

        PriorityQueue<Transaction> queueResult = new PriorityQueue<>(new TransactionAmountComparator());
        int count = Math.min(n, queueAllPayments.size());
        for (int i = 0; i < count; i++) {
            queueResult.add(queueAllPayments.poll());
        }

        return queueResult ;
    }
}

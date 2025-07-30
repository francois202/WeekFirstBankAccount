import org.example.entity.BankAccount;
import org.example.entity.Transaction;
import org.example.entity.User;
import org.example.enums.TransactionType;
import org.example.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnalyticsServiceTest {

    private User user;
    private AnalyticsService analyticsService;
    private BankAccount acc1;

    private static final String TAXI_CATEGORY = "TAXI";
    private static final String OTHER_CATEGORY = "OTHER";

    @BeforeEach
    public void setUpBeforeTest() {

        user = new User("user1", "Джон");
        analyticsService = new AnalyticsService();

        analyticsService.createAccount(user, "ACC123");
        List<BankAccount> accounts = user.getAccounts();
        acc1 = accounts.getFirst();
        acc1.deposit(new BigDecimal("10000"));

        analyticsService.payment(acc1, TAXI_CATEGORY, new BigDecimal("200"));
        analyticsService.payment(acc1, TAXI_CATEGORY, new BigDecimal("7000"));
        analyticsService.payment(acc1, OTHER_CATEGORY, new BigDecimal("100"));
        analyticsService.payment(acc1, OTHER_CATEGORY, new BigDecimal("800"));
    }

    @Test
    public void testGetMonthlySpendingByCategory() {

        assertEquals(BigDecimal.valueOf(7200), analyticsService.getMonthlySpendingByCategory(acc1, "TAXI"));
    }

    @Test
    public void testGetMonthlySpendingByCategoryInvalidCategory() {

        assertEquals(BigDecimal.valueOf(0), analyticsService.getMonthlySpendingByCategory(acc1, "TAXI11"));
    }

    @Test
    public void testGetMonthlySpendingByCategories() {

        Map<String, BigDecimal> expected = Map.of(
                "TAXI", new BigDecimal(7200),
                "OTHER", new BigDecimal(900)
        );

        Map<String, BigDecimal> actual = analyticsService.getMonthlySpendingByCategories(user, Set.of("TAXI", "OTHER"));
        assertEquals(expected, actual);

    }

    @Test
    public void testGetTransactionHistorySortedByAmount() {

        LinkedHashMap<String, List<Transaction>> resultMap = analyticsService.getTransactionHistorySortedByAmount(user);

        List<Transaction> transactionsTaxi = resultMap.get("TAXI");
        assertEquals(new BigDecimal("7000"), transactionsTaxi.getFirst().getAmount());

        List<Transaction> transactionsOther = resultMap.get("OTHER");
        assertEquals(new BigDecimal("800"), transactionsOther.getFirst().getAmount());
    }

    @Test
    public void testGetLastNTransactions() {
        List<Transaction> result = analyticsService.getLastNTransactions(user, 8);

        System.out.println(result);

        assertEquals(new BigDecimal("800"), result.getFirst().getAmount());
        assertEquals(TransactionType.WITHDRAWAL, result.getFirst().getType());

        assertEquals(new BigDecimal("800"), result.get(1).getAmount());
        assertEquals(TransactionType.PAYMENT, result.get(1).getType());
    }

    @Test
    public void testGetTopNLargestTransactions() {

        PriorityQueue<Transaction> result = analyticsService.getTopNLargestTransactions(user, 2);

        assertEquals(new BigDecimal("7000"), result.poll().getAmount());
        assertEquals(new BigDecimal("800"), result.poll().getAmount());
    }

    @Test
    public void testAnalyzePerformance() {

        List<Transaction> transactions = acc1.getTransactions();

        System.out.println("Количество транзакций: " + transactions.size() + "\n");

        long startTime1 = System.currentTimeMillis();

        transactions.stream()
                .filter(transaction -> transaction.getType().equals(TransactionType.PAYMENT))
                .map(Transaction::getAmount)
                .filter(amount -> amount.compareTo(new BigDecimal("200")) > 0)
                .sorted()
                .forEach(transaction -> System.out.print(transaction + " "));

        long endTime1 = System.currentTimeMillis();

        System.out.println("\nЗатраченное время для обычных стримов: " + (endTime1 - startTime1) + " мс \n\n");

        long startTime2 = System.currentTimeMillis();

        transactions.parallelStream()
                .filter(transaction -> transaction.getType().equals(TransactionType.PAYMENT))
                .map(Transaction::getAmount)
                .filter(amount -> amount.compareTo(new BigDecimal("200")) > 0)
                .sorted()
                .forEachOrdered(transaction -> System.out.print(transaction + " "));

        long endTime2 = System.currentTimeMillis();

        System.out.println("\nЗатраченное время для параллельных стримов: " + (endTime2 - startTime2) + " мс");
    }

}

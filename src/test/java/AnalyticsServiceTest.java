import org.example.entity.BankAccount;
import org.example.entity.Transaction;
import org.example.entity.User;
import org.example.enums.TransactionType;
import org.example.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnalyticsServiceTest {

    private static User user;
    private static AnalyticsService analyticsService;
    private static BankAccount acc1;

    @BeforeEach
    public void setUpBeforeTest() {

        user = new User("user1", "Джон");
        analyticsService = new AnalyticsService();

        analyticsService.createAccount(user, "ACC123");
        List<BankAccount> accounts = user.getAccounts();
        acc1 = accounts.getFirst();
        acc1.deposit(new BigDecimal("10000"));

        analyticsService.payment(acc1, "TAXI", new BigDecimal("200"));
        analyticsService.payment(acc1, "TAXI", new BigDecimal("7000"));
        analyticsService.payment(acc1, "OTHER", new BigDecimal("100"));
        analyticsService.payment(acc1, "OTHER", new BigDecimal("800"));
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
        List<Transaction> result = analyticsService.getLastNTransactions(user, 2);

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

}

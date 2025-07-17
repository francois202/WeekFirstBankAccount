import org.example.entity.BankAccount;
import org.example.entity.Transaction;
import org.example.entity.User;
import org.example.enums.TransactionType;
import org.example.service.BankService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

public class BankServiceTest {

    private static User user;
    private static BankService bankService;
    private static BankAccount acc1;

    @Before
    public void setUpBeforeTest() {

        user = new User("user1", "Джон");
        bankService = new BankService();

        bankService.createAccount(user, "ACC123");
        List<BankAccount> accounts = user.getAccounts();
        acc1 = accounts.getFirst();
        acc1.deposit(new BigDecimal("10000"));

        bankService.payment(acc1, "TAXI", new BigDecimal("200"));
        bankService.payment(acc1, "TAXI", new BigDecimal("7000"));
        bankService.payment(acc1, "OTHER", new BigDecimal("100"));
        bankService.payment(acc1, "OTHER", new BigDecimal("800"));
    }

    @Test
    public void testGetMonthlySpendingByCategory() {

        Assert.assertEquals(BigDecimal.valueOf(7200), bankService.getMonthlySpendingByCategory(acc1, "TAXI"));
    }
    
    @Test
    public void testGetMonthlySpendingByCategoryInvalidCategory() {

        Assert.assertEquals(BigDecimal.valueOf(0), bankService.getMonthlySpendingByCategory(acc1, "TAXI11"));
    }

    @Test
    public void testGetMonthlySpendingByCategories() {

        Map<String, BigDecimal> expected = Map.of(
                "TAXI", new BigDecimal(7200),
                "OTHER", new BigDecimal(900)
        );

        Map<String, BigDecimal> actual = bankService.getMonthlySpendingByCategories(user, Set.of("TAXI", "OTHER"));
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void testGetTransactionHistorySortedByAmount() {

        LinkedHashMap<String, List<Transaction>> resultMap = bankService.getTransactionHistorySortedByAmount(user);

        List<Transaction> transactionsTaxi = resultMap.get("TAXI");
        Assert.assertEquals(new BigDecimal("7000"), transactionsTaxi.getFirst().getAmount());

        List<Transaction> transactionsOther = resultMap.get("OTHER");
        Assert.assertEquals(new BigDecimal("800"), transactionsOther.getFirst().getAmount());
    }

    @Test
    public void testGetLastNTransactions() {
        List<Transaction> result = bankService.getLastNTransactions(user, 2);

        Assert.assertEquals(new BigDecimal("800"), result.getFirst().getAmount());
        Assert.assertEquals(TransactionType.WITHDRAWAL, result.getFirst().getType());

        Assert.assertEquals(new BigDecimal("800"), result.get(1).getAmount());
        Assert.assertEquals(TransactionType.PAYMENT, result.get(1).getType());
    }

    @Test
    public void testGetTopNLargestTransactions() {

        PriorityQueue<Transaction> result = bankService.getTopNLargestTransactions(user, 2);

        Assert.assertEquals(new BigDecimal("7000"), result.poll().getAmount());
        Assert.assertEquals(new BigDecimal("800"), result.poll().getAmount());
    }

}

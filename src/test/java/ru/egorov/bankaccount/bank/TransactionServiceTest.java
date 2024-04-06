package ru.egorov.bankaccount.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.egorov.bankaccount.BankAccountApplication;
import ru.egorov.bankaccount.bank.dto.in.NewTransactionDTO;
import ru.egorov.bankaccount.bank.dto.outDto.TransactionWithLimitDto;
import ru.egorov.bankaccount.bank.entity.Transaction;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;
import ru.egorov.bankaccount.bank.service.ClientService;
import ru.egorov.bankaccount.bank.service.LimitService;
import ru.egorov.bankaccount.bank.service.TransactionService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(classes = BankAccountApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class TransactionServiceTest {
    @Value("${application.db.currency}")
    private String currencyDb;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private LimitService limitService;

    @Test
    @DisplayName("Проверка корректного создания транзакции")
    public void createClientTransactionTest() {
        LocalDateTime expectedDate = LocalDateTime.now().plusDays(1);
        transactionService.createTransaction(new NewTransactionDTO(
                "1",
                "2",
                currencyDb,
                123.45,
                ExpenseCategory.SERVICE,
                expectedDate
        ));

        Transaction transaction1 = transactionService.findTransactionsByAccountNumber("1").get(0);
        Transaction transaction2 = transactionService.findTransactionsByAccountNumber("2").get(0);

        Assertions.assertEquals("1", transaction1.getClient().getAccountNumber());
        Assertions.assertEquals("2", transaction1.getClientTo().getAccountNumber());
        Assertions.assertEquals(expectedDate.toLocalDate(), transaction1.getDate().toLocalDate());
        Assertions.assertEquals(ExpenseCategory.SERVICE, transaction1.getExpenseCategory());
        Assertions.assertEquals(-123.45, transaction1.getSum().doubleValue());

        Assertions.assertEquals("2", transaction2.getClient().getAccountNumber());
        Assertions.assertEquals("1", transaction2.getClientTo().getAccountNumber());
        Assertions.assertEquals(expectedDate.toLocalDate(), transaction2.getDate().toLocalDate());
        Assertions.assertEquals(ExpenseCategory.SERVICE, transaction2.getExpenseCategory());
        Assertions.assertEquals(123.45, transaction2.getSum().doubleValue());
    }

    @Test
    @DisplayName("Проверка выставления флага limit_exceeded")
    public void checkLimitExceeded() {
        NewTransactionDTO transactionDTO = new NewTransactionDTO(
                "1",
                "2",
                currencyDb,
                600,
                ExpenseCategory.SERVICE,
                LocalDateTime.now()
        );
        transactionService.createTransaction(transactionDTO); // 600 - false
        transactionDTO.setDate(LocalDateTime.now());
        transactionService.createTransaction(transactionDTO); // 1200 - true
        limitService.saveNewLimit(
                "1",
                2000,
                currencyDb,
                ExpenseCategory.SERVICE
        );
        transactionDTO.setDate(LocalDateTime.now());
        transactionService.createTransaction(transactionDTO); // 1800 - false
        transactionDTO.setDate(LocalDateTime.now());
        transactionService.createTransaction(transactionDTO); // 2400 - true

        List<Transaction> transactions = transactionService.findTransactionsByAccountNumber("1");
        Assertions.assertEquals(4, transactions.size());
        Assertions.assertTrue(transactions.get(0).isLimitExceeded());
        Assertions.assertFalse(transactions.get(1).isLimitExceeded());
        Assertions.assertTrue(transactions.get(2).isLimitExceeded());
        Assertions.assertFalse(transactions.get(3).isLimitExceeded());
    }

    @Test
    @DisplayName("Проверка выставления флага limit_exceeded при наступлении нового месяца")
    public void checkLimitExceededNewNextMonth() {
        clientService.saveClientIfDoesNotExist("1");
        NewTransactionDTO transactionDTO = new NewTransactionDTO(
                "1",
                "2",
                currencyDb,
                1500,
                ExpenseCategory.SERVICE,
                LocalDateTime.now()
        );
        limitService.saveNewLimit(
                "1",
                2000,
                currencyDb,
                ExpenseCategory.SERVICE
        );
        transactionService.createTransaction(transactionDTO); // 1500 - false
        transactionDTO.setDate(LocalDateTime.now().plusMonths(1));
        transactionService.createTransaction(transactionDTO); // true

        List<Transaction> transactions = transactionService.findTransactionsByAccountNumber("1");
        Assertions.assertEquals(2, transactions.size());
        Assertions.assertTrue(transactions.get(0).isLimitExceeded());
        Assertions.assertFalse(transactions.get(1).isLimitExceeded());
    }

    @Test
    @DisplayName("Проверка выставления флага limit_exceeded для разных типов расходов")
    public void checkLimitExceededDifferentExpenseCategory() {
        transactionService.createTransaction(new NewTransactionDTO(
                "1",
                "2",
                currencyDb,
                600,
                ExpenseCategory.SERVICE,
                LocalDateTime.now()
        )); // 600 - false
        transactionService.createTransaction(new NewTransactionDTO(
                "1",
                "2",
                currencyDb,
                600,
                ExpenseCategory.PRODUCT,
                LocalDateTime.now()
        )); // 600 - false

        List<Transaction> transactions = transactionService.findTransactionsByAccountNumber("1");
        Assertions.assertEquals(2, transactions.size());
        Assertions.assertFalse(transactions.get(0).isLimitExceeded());
        Assertions.assertFalse(transactions.get(1).isLimitExceeded());
    }

    @Test
    @DisplayName("Проверка поиска транзакций, превысивших лимит, и их сопоставление с лимитом, которые они превысили")
    public void findExceededLimitTransactionsTest() {
        clientService.saveClientIfDoesNotExist("1");

        NewTransactionDTO transactionDTO = new NewTransactionDTO(
                "1",
                "2",
                currencyDb,
                1500,
                ExpenseCategory.SERVICE,
                LocalDateTime.now()
        );
        transactionService.createTransaction(transactionDTO);

        limitService.saveNewLimit(
                "1",
                2000,
                currencyDb,
                ExpenseCategory.SERVICE
        );

        transactionDTO.setDate(LocalDateTime.now());
        transactionService.createTransaction(transactionDTO);

        transactionDTO.setDate(LocalDateTime.now());
        transactionDTO.setSum(5000);
        transactionDTO.setExpenseCategory(ExpenseCategory.PRODUCT);
        transactionService.createTransaction(transactionDTO);


        List<TransactionWithLimitDto> exceededLimitTransactions = transactionService.findExceededLimitTransactions("1");
        Assertions.assertEquals(3, exceededLimitTransactions.size());

        Assertions.assertTrue(exceededLimitTransactions.get(0).isLimitExceeded());
        Assertions.assertEquals(1000, exceededLimitTransactions.get(0).getLimit().getSum());
        Assertions.assertEquals(ExpenseCategory.PRODUCT, exceededLimitTransactions.get(0).getLimit().getExpenseCategory());

        Assertions.assertTrue(exceededLimitTransactions.get(1).isLimitExceeded());
        Assertions.assertEquals(2000, exceededLimitTransactions.get(1).getLimit().getSum());
        Assertions.assertEquals(ExpenseCategory.SERVICE, exceededLimitTransactions.get(1).getLimit().getExpenseCategory());

        Assertions.assertTrue(exceededLimitTransactions.get(2).isLimitExceeded());
        Assertions.assertEquals(1000, exceededLimitTransactions.get(2).getLimit().getSum());
        Assertions.assertEquals(ExpenseCategory.SERVICE, exceededLimitTransactions.get(2).getLimit().getExpenseCategory());
    }

}

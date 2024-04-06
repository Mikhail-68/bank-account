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
import ru.egorov.bankaccount.bank.entity.Client;
import ru.egorov.bankaccount.bank.entity.Limit;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;
import ru.egorov.bankaccount.bank.repository.ClientRepository;
import ru.egorov.bankaccount.bank.repository.LimitRepository;
import ru.egorov.bankaccount.bank.service.LimitService;
import ru.egorov.bankaccount.datacurrencypairs.repository.CurrencyCacheRepository;

import java.util.List;

@SpringBootTest(classes = BankAccountApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class LimitServiceTest {
    @Value("${application.db.currency}")
    private String currencyDb;

    @Autowired
    private LimitService limitService;
    @Autowired
    private LimitRepository limitRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Test
    @DisplayName("Проверка корректности создания нового лимита")
    public void addNewTransactionTest() {
        String expectedAccountNumber = "123456789";
        double sum = 123.4567;
        ExpenseCategory expectedExpenseCategory = ExpenseCategory.SERVICE;
        clientRepository.save(new Client(expectedAccountNumber));
        limitService.saveNewLimit(expectedAccountNumber, sum, currencyDb, expectedExpenseCategory);

        List<Limit> actualLimitList = limitRepository.findByAccountNumberClient(expectedAccountNumber);
        Limit actualLimit = actualLimitList.get(0);

        Assertions.assertEquals(1, actualLimitList.size());
        Assertions.assertEquals(expectedAccountNumber, actualLimit.getClient().getAccountNumber());
        Assertions.assertEquals(123.46, actualLimit.getSum().doubleValue());
        Assertions.assertEquals(expectedExpenseCategory, actualLimit.getExpenseCategory());
    }
}

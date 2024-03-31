package ru.egorov.bankaccount.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.egorov.bankaccount.bank.entity.Client;
import ru.egorov.bankaccount.bank.entity.Limit;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;
import ru.egorov.bankaccount.bank.repository.ClientRepository;
import ru.egorov.bankaccount.bank.repository.LimitRepository;
import ru.egorov.bankaccount.bank.service.ClientService;

import java.util.List;

//@SpringBootTest(
//        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
//        classes = WavefrontProperties.Application.class
//)
@SpringBootTest
@ActiveProfiles("test")
public class ClientServiceTest {
    @Autowired
    private ClientService clientService;
    @Autowired
    private LimitRepository limitRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Test
    @DisplayName("Проверка корректности создания нового лимита")
    public void addNewTransactionTest() {
        String expectedAccountNumber = "123456789";
        double sum = 123.456;
        ExpenseCategory expectedExpenseCategory = ExpenseCategory.SERVICE;
        clientRepository.save(new Client(expectedAccountNumber));
        clientService.saveNewLimit(expectedAccountNumber, sum, expectedExpenseCategory);

        List<Limit> actualLimitList = limitRepository.findByAccountNumberClient(expectedAccountNumber);
        Limit actualLimit = actualLimitList.get(0);

        Assertions.assertEquals(1, actualLimitList.size());
        Assertions.assertEquals(expectedAccountNumber, actualLimit.getClient().getAccountNumber());
        Assertions.assertEquals(123.46, actualLimit.getSum().doubleValue());
        Assertions.assertEquals(expectedExpenseCategory, actualLimit.getExpenseCategory());
    }
}

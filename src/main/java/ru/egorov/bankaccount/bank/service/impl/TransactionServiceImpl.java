package ru.egorov.bankaccount.bank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.egorov.bankaccount.bank.dto.in.NewTransactionDTO;
import ru.egorov.bankaccount.bank.dto.outDto.TransactionListDto;
import ru.egorov.bankaccount.bank.entity.Transaction;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;
import ru.egorov.bankaccount.bank.mapper.TransactionMapper;
import ru.egorov.bankaccount.bank.repository.TransactionRepository;
import ru.egorov.bankaccount.bank.service.ClientService;
import ru.egorov.bankaccount.bank.service.LimitService;
import ru.egorov.bankaccount.bank.service.TransactionService;
import ru.egorov.bankaccount.bank.utils.RoundingBigDecimal;
import ru.egorov.bankaccount.datacurrencypairs.service.DataCurrencyService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ClientService clientService;
    private final LimitService limitService;
    private final DataCurrencyService currencyService;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, ClientService clientService, LimitService limitService, DataCurrencyService currencyService, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.clientService = clientService;
        this.limitService = limitService;
        this.currencyService = currencyService;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public List<Transaction> findTransactionsByAccountNumber(String accountNumber) {
        return transactionRepository.findByClient(accountNumber);
    }

    @Override
    public TransactionListDto findTransactionsByAccountNumberConvertCurrency(String accountNumber, String currency) {
        List<Transaction> listTransactions = transactionRepository.findByClient(accountNumber);
        BigDecimal exchangeRate = currencyService.getCurrentValuePairsFromDefault(currency);
        listTransactions.forEach(transaction -> transaction.setSum(
                RoundingBigDecimal.roundBigDecimal(transaction.getSum().multiply(exchangeRate))
        ));
        return new TransactionListDto(currency,
                transactionMapper.toTransactionDtoList(listTransactions));
    }

    @Override
    @Transactional
    public void createTransaction(NewTransactionDTO transactionDTO) {
        log.info("Создание новой транзакции");
        BigDecimal sum = RoundingBigDecimal.roundBigDecimal(transactionDTO.getSum());
        clientService.saveClientIfDoesNotExist(transactionDTO.getAccountFrom());
        clientService.saveClientIfDoesNotExist(transactionDTO.getAccountTo());

        log.debug("Конвертируем валюту");
        BigDecimal sumUsd = currencyService.getCurrentValuePairsToDefault(transactionDTO.getCurrency())
                .multiply(BigDecimal.valueOf(transactionDTO.getSum()));
        sumUsd = RoundingBigDecimal.roundBigDecimal(sumUsd);

        saveTransaction(transactionDTO, transactionDTO.getAccountTo(), transactionDTO.getAccountFrom(), sumUsd);
        saveTransaction(transactionDTO, transactionDTO.getAccountFrom(), transactionDTO.getAccountTo(), sumUsd.negate());
        log.info("Новая транзакция создана");
    }

    private void saveTransaction(NewTransactionDTO transactionDTO, String accountNumber, String accountNumberTo, BigDecimal sumUsd) {
        log.debug("Создаем транзакцию для номера счета: " + accountNumber);

        BigDecimal limit = limitService.getLastLimitThisMonth(accountNumber, transactionDTO.getExpenseCategory()).getSum();
        BigDecimal currentSum = calculateSumTransactionsThisMonth(accountNumber, transactionDTO.getExpenseCategory())
                .add(sumUsd);
        boolean limitExceeded = currentSum.doubleValue() > limit.doubleValue();

        transactionRepository.save(new Transaction(
                clientService.findClientByAccountNumber(accountNumber).get(),
                clientService.findClientByAccountNumber(accountNumberTo).get(),
                transactionDTO.getDate(),
                sumUsd,
                limitExceeded,
                transactionDTO.getExpenseCategory()
        ));
        log.debug("Создана транзакция для счета: " + accountNumber);
    }

    @Override
    public BigDecimal calculateSumTransactionsThisMonth(String accountNumber, ExpenseCategory expenseCategory) {
        return transactionRepository.calculateSumTransactionsThisMonth(accountNumber, expenseCategory.name())
                .orElse(BigDecimal.ZERO);
    }
}

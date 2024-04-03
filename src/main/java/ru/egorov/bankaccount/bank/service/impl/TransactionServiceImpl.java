package ru.egorov.bankaccount.bank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.egorov.bankaccount.bank.dto.in.NewTransactionDTO;
import ru.egorov.bankaccount.bank.dto.outDto.LimitDTO;
import ru.egorov.bankaccount.bank.dto.outDto.TransactionListDto;
import ru.egorov.bankaccount.bank.dto.outDto.TransactionWithLimitDto;
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
import java.util.ArrayList;
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
        log.debug("Конвертация валюты транзакции");
        listTransactions.forEach(transaction -> transaction.setSum(
                RoundingBigDecimal.roundBigDecimal(transaction.getSum().multiply(exchangeRate))
        ));
        return new TransactionListDto(currency,
                transactionMapper.toTransactionDto(listTransactions));
    }

    public List<TransactionWithLimitDto> findExceededLimitTransactions(String accountNumber) {
        List<Transaction> transactions = transactionRepository.findWhoExceededLimit(accountNumber);
        List<LimitDTO> limit = limitService.getLimitByClient(accountNumber);

        List<TransactionWithLimitDto> transactionWithLimit = new ArrayList<>();

        int i = 0;
        for (Transaction transaction : transactions) {
            while (i < limit.size()) {
                if (transaction.getDate().isAfter(limit.get(i).getDate())) {
                    transactionWithLimit.add(
                            transactionMapper.toTransactionWithLimitDto(transaction, limit.get(i))
                    );
                    break;
                } else {
                    i++;
                }
            }
        }
        return transactionWithLimit;
    }

    public List<TransactionWithLimitDto> findExceededLimitTransactionsConvertCurrency(String accountNumber, String currency) {
        List<TransactionWithLimitDto> transactions = findExceededLimitTransactions(accountNumber);

        BigDecimal exchangeRate = currencyService.getCurrentValuePairsFromDefault(currency);
        log.debug("Конвертация валюты транзакций и лимитов");
        transactions.forEach(transaction -> {
                    transaction.setSum(
                            RoundingBigDecimal.roundBigDecimal(BigDecimal.valueOf(transaction.getSum()).multiply(exchangeRate)).doubleValue()
                    );
                    LimitDTO limitDTO = transaction.getLimit();
                    transaction.setLimit(new LimitDTO(
                            limitDTO.getId(),
                            limitDTO.getDate(),
                            RoundingBigDecimal.roundBigDecimal(
                                    BigDecimal.valueOf(limitDTO.getSum()).multiply(exchangeRate)
                            ).doubleValue(),
                            limitDTO.getExpenseCategory()
                    ));
                }
        );
        return transactions;
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
        boolean limitExceeded;
        if (sumUsd.doubleValue() < 0) {
            BigDecimal currentSum = calculateSumTransactionsThisMonth(accountNumber, transactionDTO.getExpenseCategory())
                    .add(sumUsd.abs());
            limitExceeded = currentSum.doubleValue() > limit.doubleValue();
        } else {
            limitExceeded = false;
        }

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

package ru.egorov.bankaccount.bank.service;

import ru.egorov.bankaccount.bank.dto.in.NewTransactionDTO;
import ru.egorov.bankaccount.bank.dto.outDto.TransactionListDto;
import ru.egorov.bankaccount.bank.entity.Transaction;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    /**
     * Возвращает список транзакций клиента а валюте БД
     *
     * @param accountNumber номер счета клиента
     * @return список транзакций
     */
    List<Transaction> findTransactionsByAccountNumber(String accountNumber);

    /**
     * Возвращает список транзакций клиента в переданной валюте
     *
     * @param accountNumber номер счета клиента
     * @param currency      валюта отображения транзакций
     * @return список транзакций в выбранной валюте
     */
    TransactionListDto findTransactionsByAccountNumberConvertCurrency(String accountNumber, String currency);

    void createTransaction(NewTransactionDTO transactionDTO);

    /**
     * Вычисляет сумму транзакция клиента за текущий месяц определенной категории расхода
     *
     * @param accountNumber   номер счета клиента
     * @param expenseCategory категория расходов
     * @return сумма транзакций клиента за текущий месяц
     */
    BigDecimal calculateSumTransactionsThisMonth(String accountNumber, ExpenseCategory expenseCategory);

}

package ru.egorov.bankaccount.bank.service;

import ru.egorov.bankaccount.bank.dto.in.NewTransactionDTO;
import ru.egorov.bankaccount.bank.entity.Transaction;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    List<Transaction> findTransactionsByAccountNumber(String accountNumber);

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

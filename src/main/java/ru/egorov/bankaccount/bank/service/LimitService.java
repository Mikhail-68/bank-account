package ru.egorov.bankaccount.bank.service;

import ru.egorov.bankaccount.bank.dto.outDto.LimitDTO;
import ru.egorov.bankaccount.bank.entity.Limit;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;

import java.util.List;
import java.util.Optional;

public interface LimitService {
    /**
     * Возвращает лимит по ее id
     *
     * @param id номер лимита
     * @return лимит
     */
    Optional<Limit> getLimitById(int id);

    /**
     * Возвращает список лимитов клиента
     *
     * @param accountNumber номер банковского счета клиента
     * @return список выставленных лимитов у клиента
     */
    List<LimitDTO> getLimitByClient(String accountNumber);

    /**
     * Получить последний лимит клиента в текущем месяце
     *
     * @param accountNumber   номер счета клиента
     * @param expenseCategory категория расхода
     * @return установленный последний лимит в текущем месяце
     */
    Limit getLastLimitThisMonth(String accountNumber, ExpenseCategory expenseCategory);

    /**
     * Устанавливает новый месячный лимит
     *
     * @param accountNumber   номер счета клиента
     * @param sum             лимит
     * @param currency        валюта лимита
     * @param expenseCategory категория расходов
     */
    void saveNewLimit(String accountNumber, double sum, String currency, ExpenseCategory expenseCategory);

    /**
     * Устанавливает лимиты по-умолчанию, если не установлен ни один лимит в текущем месяце
     *
     * @param accountNumber номер счета клиента
     */
    void setDefaultLimitsIfNotExistLimitsThisMonth(String accountNumber);
}

package ru.egorov.bankaccount.bank.service;

import ru.egorov.bankaccount.bank.dto.LimitDTO;
import ru.egorov.bankaccount.bank.entity.Limit;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    /**
     * Возвращает лимит по ее id
     * @param id номер лимита
     * @return лимит
     */
    Optional<Limit> getLimitById(int id);

    /**
     * Возвращает список лимитов клиента
     * @param accountNumber номер банковского счета клиента
     * @return список выставленных лимитов у клиента
     */
    List<LimitDTO> getLimitByClient(String accountNumber);

    void saveNewLimit(String accountNumber, double sum, ExpenseCategory expenseCategory);
}

package ru.egorov.bankaccount.bank.service;

import ru.egorov.bankaccount.bank.entity.Client;

import java.util.Optional;

public interface ClientService {
    /**
     * Добавить клиента в БД если он отсутствует
     * @param accountNumber номер счета клиента
     */
    void saveClientIfDoesNotExist(String accountNumber);

    /**
     * Поиск клиента по его номеру счета
     * @param accountNumber номер счета клиента
     * @return Клиент
     */
    Optional<Client> findClientByAccountNumber(String accountNumber);
}

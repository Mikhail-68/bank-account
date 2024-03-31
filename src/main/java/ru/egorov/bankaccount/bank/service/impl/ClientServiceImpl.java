package ru.egorov.bankaccount.bank.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.egorov.bankaccount.bank.entity.Client;
import ru.egorov.bankaccount.bank.repository.ClientRepository;
import ru.egorov.bankaccount.bank.service.ClientService;
import ru.egorov.bankaccount.bank.service.LimitService;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final LimitService limitService;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, LimitService limitService) {
        this.clientRepository = clientRepository;
        this.limitService = limitService;
    }

    @Override
    @Transactional
    public void saveClientIfDoesNotExist(String accountNumber) {
        if(clientRepository.findByAccountNumber(accountNumber).isPresent()) {
            return;
        }
        clientRepository.save(new Client(accountNumber));
        limitService.setDefaultLimitsIfNotExistLimitsThisMonth(accountNumber);
    }

    @Override
    public Optional<Client> findClientByAccountNumber(String accountNumber) {
        return clientRepository.findByAccountNumber(accountNumber);
    }
}

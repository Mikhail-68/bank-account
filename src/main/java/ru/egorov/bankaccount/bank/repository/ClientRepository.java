package ru.egorov.bankaccount.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.egorov.bankaccount.bank.entity.Client;

import java.util.Optional;

@Repository
@Transactional
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByAccountNumber(String accountNumber);
}

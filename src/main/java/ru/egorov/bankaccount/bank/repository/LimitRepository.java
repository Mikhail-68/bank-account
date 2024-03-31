package ru.egorov.bankaccount.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.egorov.bankaccount.bank.entity.Client;
import ru.egorov.bankaccount.bank.entity.Limit;

import java.util.List;

@Repository
@Transactional
public interface LimitRepository extends JpaRepository<Limit, Integer> {
    @Query("SELECT l FROM Limit l WHERE l.client.accountNumber=:accountNumber")
    List<Limit> findByAccountNumberClient(@Param("accountNumber") String accountNumber);
    List<Limit> findByClient(Client client);
    @Modifying
    @Query(value = "INSERT INTO \"limit\" (client_id, sum, expense_category) VALUES " +
            "((SELECT id FROM client WHERE account_number=:accountNumber), :sum, :expenseCategory)", nativeQuery = true)
    void save(String accountNumber, double sum, String expenseCategory);
}

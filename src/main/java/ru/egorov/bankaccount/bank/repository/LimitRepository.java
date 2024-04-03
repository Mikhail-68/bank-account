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
import java.util.Optional;

@Repository
@Transactional
public interface LimitRepository extends JpaRepository<Limit, Integer> {
    @Modifying
    @Query(value = "INSERT INTO \"limit\" (client_id, sum, expense_category) VALUES " +
            "((SELECT id FROM client WHERE account_number=:accountNumber), :sum, :expenseCategory)",
            nativeQuery = true)
    void save(String accountNumber, double sum, String expenseCategory);

    @Query("SELECT l FROM Limit l WHERE l.client.accountNumber=:accountNumber ORDER BY l.date DESC")
    List<Limit> findByAccountNumberClient(@Param("accountNumber") String accountNumber);

    List<Limit> findByClientOrderByDateDesc(Client client);

    @Query(value = "SELECT l.* FROM \"limit\" l " +
            "JOIN client c ON c.id = l.client_id " +
            "WHERE c.account_number = :accountNumber " +
            "AND l.expense_category = :expenseCategory " +
            "AND EXTRACT(YEAR FROM CURRENT_DATE) = EXTRACT(YEAR FROM l.date) " +
            "AND EXTRACT(MONTH FROM CURRENT_DATE) = EXTRACT(MONTH FROM l.date) " +
            "ORDER BY l.date DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Limit> getLastLimitThisMonth(String accountNumber, String expenseCategory);
}

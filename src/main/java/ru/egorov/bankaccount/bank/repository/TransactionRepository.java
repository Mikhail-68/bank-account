package ru.egorov.bankaccount.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.egorov.bankaccount.bank.entity.Client;
import ru.egorov.bankaccount.bank.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByClient(Client client);

    @Query("SELECT t FROM Transaction t WHERE t.client.accountNumber = :accountNumber ORDER BY t.date DESC")
    List<Transaction> findByClient(String accountNumber);

    @Query(value = "SELECT ABS(SUM(t.sum)) FROM \"transaction\" t " +
            "JOIN client c ON c.id = t.client_id " +
            "WHERE c.account_number = :accountNumber " +
            "AND t.sum < 0 " +
            "AND t.expense_category = :expenseCategory " +
            "AND EXTRACT(YEAR FROM CURRENT_DATE) = EXTRACT(YEAR FROM t.date) " +
            "AND EXTRACT(MONTH FROM CURRENT_DATE) = EXTRACT(MONTH FROM t.date)",
            nativeQuery = true)
    Optional<BigDecimal> calculateSumTransactionsThisMonth(String accountNumber,
                                                           String expenseCategory);

    @Query("SELECT t FROM Transaction t WHERE t.limitExceeded = true ORDER BY t.date DESC")
    List<Transaction> findWhoExceededLimit(String accountNumber);
}

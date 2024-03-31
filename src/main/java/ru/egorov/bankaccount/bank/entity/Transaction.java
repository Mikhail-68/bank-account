package ru.egorov.bankaccount.bank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "\"transaction\"")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;
    private LocalDateTime date;
    private BigDecimal sum;
    private boolean limitExceeded;
    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;

    public Transaction(Client client, LocalDateTime date, BigDecimal sum,
                       boolean limitExceeded, ExpenseCategory expenseCategory) {
        this.client = client;
        this.date = date;
        this.sum = sum;
        this.limitExceeded = limitExceeded;
        this.expenseCategory = expenseCategory;
    }
}

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
@Table(name = "\"limit\"")
public class Limit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;
    private LocalDateTime date;
    private BigDecimal sum;
    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;

    public Limit(Client client, LocalDateTime date, BigDecimal sum, ExpenseCategory expenseCategory) {
        this.client = client;
        this.date = date;
        this.sum = sum;
        this.expenseCategory = expenseCategory;
    }
}

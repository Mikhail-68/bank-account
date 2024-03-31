package ru.egorov.bankaccount.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LimitDTO {
    private int id;
    private LocalDateTime date;
    private double sum;
    private ExpenseCategory expenseCategory;
}

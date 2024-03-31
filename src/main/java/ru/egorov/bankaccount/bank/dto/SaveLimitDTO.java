package ru.egorov.bankaccount.bank.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveLimitDTO {
    private String clientAccountNumber;
    @Min(value = 0, message = "Сумма лимита должна быть больше 0")
    private double sum;
    private ExpenseCategory expenseCategory;
}


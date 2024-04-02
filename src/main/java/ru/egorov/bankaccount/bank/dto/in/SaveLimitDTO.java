package ru.egorov.bankaccount.bank.dto.in;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "[\\d]{1,10}", message = "Номер аккаунта должен состоять из цифр и его длина должна быть от 1 до 10 символов")
    private String clientAccountNumber;
    @Min(value = 0, message = "Сумма лимита должна быть больше 0")
    private double sum;
    private String currency;
    private ExpenseCategory expenseCategory;
}


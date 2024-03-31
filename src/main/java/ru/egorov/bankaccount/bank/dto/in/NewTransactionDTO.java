package ru.egorov.bankaccount.bank.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewTransactionDTO {
    @Pattern(regexp = "[\\d]{1,10}", message = "Номер аккаунта должен состоять из цифр и его длина должна быть от 1 до 10 символов")
    private String accountFrom;
    @Pattern(regexp = "[\\d]{1,10}", message = "Номер аккаунта должен состоять из цифр и его длина должна быть от 1 до 10 символов")
    private String accountTo;
    private String currency;
    @Min(value = 0, message = "Сумма должна быть больше нуля")
    private double sum;
    private ExpenseCategory expenseCategory;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
}

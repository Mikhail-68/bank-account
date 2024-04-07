package ru.egorov.bankaccount.bank.dto.outDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionListDto {
    private String currency;
    private List<TransactionDto> transactions;
}

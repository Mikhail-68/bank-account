package ru.egorov.bankaccount.bank.mapper;

import org.mapstruct.Mapper;
import ru.egorov.bankaccount.bank.dto.outDto.TransactionDto;
import ru.egorov.bankaccount.bank.entity.Transaction;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {
    public TransactionDto toTransactionDtoList(Transaction transaction) {
        return new TransactionDto(
                transaction.getClientTo().getAccountNumber(),
                transaction.getSum().doubleValue(),
                transaction.getExpenseCategory(),
                transaction.getDate(),
                transaction.isLimitExceeded()
        );
    }

    public abstract List<TransactionDto> toTransactionDtoList(List<Transaction> transaction);
}

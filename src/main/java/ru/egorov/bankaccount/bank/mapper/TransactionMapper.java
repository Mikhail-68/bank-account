package ru.egorov.bankaccount.bank.mapper;

import org.mapstruct.Mapper;
import ru.egorov.bankaccount.bank.dto.outDto.LimitDTO;
import ru.egorov.bankaccount.bank.dto.outDto.TransactionDto;
import ru.egorov.bankaccount.bank.dto.outDto.TransactionWithLimitDto;
import ru.egorov.bankaccount.bank.entity.Transaction;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

    public TransactionDto toTransactionDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getClientTo().getAccountNumber(),
                transaction.getSum().doubleValue(),
                transaction.getExpenseCategory(),
                transaction.getDate(),
                transaction.isLimitExceeded()
        );
    }

    public abstract List<TransactionDto> toTransactionDto(List<Transaction> transaction);

    public TransactionWithLimitDto toTransactionWithLimitDto(Transaction transaction, LimitDTO limitDTO) {
        return new TransactionWithLimitDto(
                transaction.getClientTo().getAccountNumber(),
                transaction.getSum().doubleValue(),
                transaction.getExpenseCategory(),
                transaction.getDate(),
                transaction.isLimitExceeded(),
                limitDTO
        );
    }
}

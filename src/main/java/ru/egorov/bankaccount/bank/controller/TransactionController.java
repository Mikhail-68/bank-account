package ru.egorov.bankaccount.bank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.egorov.bankaccount.bank.dto.in.NewTransactionDTO;
import ru.egorov.bankaccount.bank.dto.outDto.MessageDTO;
import ru.egorov.bankaccount.bank.dto.outDto.TransactionListDto;
import ru.egorov.bankaccount.bank.dto.outDto.TransactionWithLimitDto;
import ru.egorov.bankaccount.bank.entity.Transaction;
import ru.egorov.bankaccount.bank.service.TransactionService;

import java.util.List;

@Tag(name = "Контроллер управления транзакциями", description = "Управляет транзакциями клиентов")
@RestController
@RequestMapping("/api/v1")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Получение всех транзакций клиента",
            description = "Позволяет получить список транзакций клиента по его номеру счета")
    @GetMapping("/transaction/all")
    public ResponseEntity<List<Transaction>> getTransactions(@RequestParam String accountNumber) {
        return ResponseEntity.ok(transactionService.findTransactionsByAccountNumber(accountNumber));
    }

    @Operation(summary = "Получение транзакций клиента, конвертированные в валюту",
            description = "Позволяет получить транзакции клиента по его номеру счета, конвертированные в определенную валюту")
    @GetMapping("/transaction/convert/all")
    public ResponseEntity<TransactionListDto> getTransactions(@RequestParam String accountNumber,
                                                              @RequestParam String currency) {
        return ResponseEntity.ok(transactionService.findTransactionsByAccountNumberConvertCurrency(accountNumber, currency));
    }

    @Operation(summary = "Получение лимитов клиента, превысивших месячный лимит",
            description = "Позволяет получить список транзакций клиента по его номеру счета, которые превысили месячный лимит")
    @GetMapping("/transaction/exceeded")
    public ResponseEntity<List<TransactionWithLimitDto>> getExceededLimitTransactions(@RequestParam String accountNumber) {
        return ResponseEntity.ok(transactionService.findExceededLimitTransactions(accountNumber));
    }

    @Operation(summary = "Получение транзакций клиента, превысивших месячный лимит, конвертированные в валюту",
            description = "Позволяет получить транзакции клиента, превысившие лимит, по его номеру счета и при этом конвертированные в валюту")
    @GetMapping("/transaction/convert/exceeded")
    public ResponseEntity<List<TransactionWithLimitDto>> getExceededLimitTransactionsConvertCurrency(
            @RequestParam String accountNumber,
            @RequestParam String currency) {
        return ResponseEntity.ok(transactionService.findExceededLimitTransactionsConvertCurrency(accountNumber, currency));
    }

    @Operation(summary = "Создание новой транзакции", description = "Позволяет создавать транзакции между различными счетами")
    @PostMapping("/transaction")
    public ResponseEntity<Object> createTransaction(@RequestBody @Valid NewTransactionDTO transactionDTO,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            transactionService.createTransaction(transactionDTO);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MessageDTO(ex.getMessage()));
        }

        return ResponseEntity.ok().build();
    }

}

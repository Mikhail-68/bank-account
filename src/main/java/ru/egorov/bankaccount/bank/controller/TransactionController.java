package ru.egorov.bankaccount.bank.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.egorov.bankaccount.bank.dto.out.MessageDTO;
import ru.egorov.bankaccount.bank.dto.in.NewTransactionDTO;
import ru.egorov.bankaccount.bank.entity.Transaction;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;
import ru.egorov.bankaccount.bank.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("transaction/all")
    public ResponseEntity<List<Transaction>> getTransactions(@RequestParam String accountNumber) {
        return ResponseEntity.ok(transactionService.findTransactionsByAccountNumber(accountNumber));
    }

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

    @GetMapping("/transaction/getSumTransactionsThisMonth/{id}")
    public double getSumTransactionsAfterLastLimit(
            @PathVariable("id") String accountNumber,
            @RequestParam ExpenseCategory category) {
        return transactionService.calculateSumTransactionsThisMonth(accountNumber, category)
                .doubleValue();
    }
}

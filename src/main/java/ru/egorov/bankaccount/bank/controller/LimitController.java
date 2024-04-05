package ru.egorov.bankaccount.bank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.egorov.bankaccount.bank.dto.outDto.LimitDTO;
import ru.egorov.bankaccount.bank.dto.in.SaveLimitDTO;
import ru.egorov.bankaccount.bank.entity.Limit;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;
import ru.egorov.bankaccount.bank.service.LimitService;

import java.util.List;
import java.util.Optional;


@Tag(name = "Контроллер управления лимитами", description = "Управляет лимитами клиентов")
@RestController
@RequestMapping("api/v1")
public class LimitController {
    private final LimitService limitService;

    @Autowired
    public LimitController(LimitService limitService) {
        this.limitService = limitService;
    }

    @Operation(summary = "Получение лимита по его id", description = "Позволяет получить лимит по его id")
    @GetMapping("/limit/{id}")
    public ResponseEntity<Limit> getLimitById(@PathVariable int id) {
        Optional<Limit> limit = limitService.getLimitById(id);
        if (limit.isEmpty())
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).build();
        return ResponseEntity.ok(limit.get());
    }

    @Operation(summary = "Получение списка лимитов пользователя по его номеру счета",
            description = "Позволяет получить список лимитов пользователя по его номеру счета")
    @GetMapping("user/limit/{id}")
    public ResponseEntity<List<LimitDTO>> getLimitByClient(@PathVariable("id") String accountNumber) {
        return ResponseEntity.ok(limitService.getLimitByClient(accountNumber));
    }

    @Operation(summary = "Сохранение нового лимита", description = "Позволяет сохранить новый месячный лимит пользователя")
    @PostMapping("user/limit")
    public ResponseEntity<Object> saveNewLimit(@RequestBody @Valid SaveLimitDTO newLimit, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        limitService.saveNewLimit(newLimit.getClientAccountNumber(),
                newLimit.getSum(),
                newLimit.getCurrency(),
                newLimit.getExpenseCategory());
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Получение последнего лимита пользователя в текущем месяце",
            description = "Позволяет получить последний в этом месяце лимит пользователя по его номеру счета и категории расходов")
    @GetMapping("user/lastLimit")
    public ResponseEntity<Limit> getLastLimitThisMonth(
            @RequestParam String accountNumber,
            @RequestParam ExpenseCategory expenseCategory) {
        return ResponseEntity.ok(limitService.getLastLimitThisMonth(accountNumber, expenseCategory));
    }
}

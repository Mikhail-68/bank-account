package ru.egorov.bankaccount.bank.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.egorov.bankaccount.bank.dto.LimitDTO;
import ru.egorov.bankaccount.bank.dto.SaveLimitDTO;
import ru.egorov.bankaccount.bank.entity.Limit;
import ru.egorov.bankaccount.bank.service.ClientService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/limit/{id}")
    public ResponseEntity<Limit> getLimitById(@PathVariable int id) {
        Optional<Limit> limit = clientService.getLimitById(id);
        if(limit.isEmpty())
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).build();
        return ResponseEntity.ok(limit.get());
    }

    @GetMapping("user/limit/{id}")
    public ResponseEntity<List<LimitDTO>> getLimitByClient(@PathVariable("id") String accountNumber) {
        return ResponseEntity.ok(clientService.getLimitByClient(accountNumber));
    }

    @PostMapping("user/limit")
    public ResponseEntity<Object> saveNewLimit(@RequestBody @Valid SaveLimitDTO newLimit, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        clientService.saveNewLimit(newLimit.getClientAccountNumber(), newLimit.getSum(), newLimit.getExpenseCategory());
        return ResponseEntity.accepted().build();
    }
}

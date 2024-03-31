package ru.egorov.bankaccount.bank.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.egorov.bankaccount.bank.dto.LimitDTO;
import ru.egorov.bankaccount.bank.entity.Limit;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;
import ru.egorov.bankaccount.bank.mapper.LimitMapper;
import ru.egorov.bankaccount.bank.repository.LimitRepository;
import ru.egorov.bankaccount.bank.service.ClientService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final LimitRepository limitRepository;
    private final LimitMapper limitMapper;

    @Autowired
    public ClientServiceImpl(LimitRepository limitRepository, LimitMapper limitMapper) {
        this.limitRepository = limitRepository;
        this.limitMapper = limitMapper;
    }

    @Override
    public Optional<Limit> getLimitById(int id) {
        return limitRepository.findById(id);
    }

    @Override
    public List<LimitDTO> getLimitByClient(String accountNumber) {
        return limitMapper.toLimitDTOList(limitRepository.findByAccountNumberClient(accountNumber));
    }

    @Override
    @Transactional
    public void saveNewLimit(String accountNumber, double sum, ExpenseCategory expenseCategory) {
        BigDecimal bigDecimal = BigDecimal.valueOf(sum).setScale(2, RoundingMode.HALF_UP);
        limitRepository.save(accountNumber, bigDecimal.doubleValue(), expenseCategory.name());
    }
}

package ru.egorov.bankaccount.bank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.egorov.bankaccount.bank.dto.out.LimitDTO;
import ru.egorov.bankaccount.bank.entity.Limit;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;
import ru.egorov.bankaccount.bank.mapper.LimitMapper;
import ru.egorov.bankaccount.bank.repository.LimitRepository;
import ru.egorov.bankaccount.bank.service.LimitService;
import ru.egorov.bankaccount.bank.utils.RoundingBigDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class LimitServiceImpl implements LimitService {

    @Value("${application.limit.startValue}")
    private double startLimitValue;

    private final LimitRepository limitRepository;
    private final LimitMapper limitMapper;

    @Autowired
    public LimitServiceImpl(LimitRepository limitRepository, LimitMapper limitMapper) {
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
    public Limit getLastLimitThisMonth(String accountNumber, ExpenseCategory expenseCategory) {
        setDefaultLimitsIfNotExistLimitsThisMonth(accountNumber);
        log.debug("Получение последнего лимита в текущем месяце для счета: " + accountNumber);
        Optional<Limit> limit = limitRepository.getLastLimitThisMonth(accountNumber, expenseCategory.name());
        return limit.get();
    }

    @Override
    public void saveNewLimit(String accountNumber, double sum, ExpenseCategory expenseCategory) {
        log.debug("Сохранение нового лимита для счета: " + accountNumber);
        BigDecimal bigDecimal = RoundingBigDecimal.roundBigDecimal(sum);
        limitRepository.save(accountNumber, bigDecimal.doubleValue(), expenseCategory.name());
    }

    @Override
    public void setDefaultLimitsIfNotExistLimitsThisMonth(String accountNumber) {
        for(var category : ExpenseCategory.values()) {
            if(limitRepository.getLastLimitThisMonth(accountNumber, category.name()).isEmpty()) {
                log.debug("Установка дефолтного лимита для счета: " + accountNumber + " и типа расхода: " + category.name());
                limitRepository.save(accountNumber, startLimitValue, category.name());
            }
        }
    }
}
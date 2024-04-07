package ru.egorov.bankaccount.bank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.egorov.bankaccount.bank.dto.outDto.LimitDTO;
import ru.egorov.bankaccount.bank.entity.Limit;
import ru.egorov.bankaccount.bank.enums.ExpenseCategory;
import ru.egorov.bankaccount.bank.mapper.LimitMapper;
import ru.egorov.bankaccount.bank.repository.LimitRepository;
import ru.egorov.bankaccount.bank.service.LimitService;
import ru.egorov.bankaccount.bank.utils.RoundingBigDecimal;
import ru.egorov.bankaccount.datacurrencypairs.service.DataCurrencyService;

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
    private final DataCurrencyService currencyService;

    @Autowired
    public LimitServiceImpl(LimitRepository limitRepository, LimitMapper limitMapper, DataCurrencyService currencyService) {
        this.limitRepository = limitRepository;
        this.limitMapper = limitMapper;
        this.currencyService = currencyService;
    }

    @Override
    public Optional<Limit> getLimitById(int id) {
        log.debug("Получение лимита по его id");
        return limitRepository.findById(id);
    }

    @Override
    public List<LimitDTO> getLimitByClient(String accountNumber) {
        log.debug("Получение лимита по номеру счета клиента");
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
    public void saveNewLimit(String accountNumber, double sum, String currency, ExpenseCategory expenseCategory) {
        BigDecimal currencyConvert = currencyService.getCurrentValuePairsToDefault(currency)
                .multiply(BigDecimal.valueOf(sum));
        currencyConvert = RoundingBigDecimal.roundBigDecimal(currencyConvert);
        log.debug("Сохранение нового лимита для счета: " + accountNumber);
        limitRepository.save(accountNumber, currencyConvert.doubleValue(), expenseCategory.name());
    }

    @Override
    public void setDefaultLimitsIfNotExistLimitsThisMonth(String accountNumber) {
        for (var category : ExpenseCategory.values()) {
            if (limitRepository.getLastLimitThisMonth(accountNumber, category.name()).isEmpty()) {
                log.debug("Установка дефолтного лимита для счета: " + accountNumber + " и типа расхода: " + category.name());
                limitRepository.save(accountNumber, startLimitValue, category.name());
            }
        }
    }
}

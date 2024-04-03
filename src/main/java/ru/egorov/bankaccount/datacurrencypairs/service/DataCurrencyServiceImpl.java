package ru.egorov.bankaccount.datacurrencypairs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.egorov.bankaccount.datacurrencypairs.dto.CurrencyDto;
import ru.egorov.bankaccount.datacurrencypairs.entity.CurrencyCache;
import ru.egorov.bankaccount.datacurrencypairs.repository.CurrencyCacheRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DataCurrencyServiceImpl implements DataCurrencyService {

    @Value("${application.twelvedata.apikey}")
    private String apiKey;
    @Value("${application.db.currency}")
    private String currencyDb;
    private final RestTemplate restTemplate = new RestTemplate();

    private final CurrencyCacheRepository cacheRepository;

    @Autowired
    public DataCurrencyServiceImpl(CurrencyCacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    @Override
    public BigDecimal getCurrentValue(String value) {
        log.debug("Получение валюты: " + value);

        if(value.contains("/") && value.indexOf("/") == value.lastIndexOf("/")) {
            String[] str = value.split("/");
            if(str[0].equals(str[1])) {
                log.debug("Возврат валюты: 1. Так как валюты одинаковы (пример: USD/USD)");
                return BigDecimal.ONE;
            }
        }

        Optional<CurrencyCache> currencyCache = cacheRepository.findById(value);
        if (currencyCache.isPresent() && currencyCache.get().getDate().equals(LocalDate.now()) && currencyCache.get().getValue() != null) {
            log.debug("Получение валюты из кэша");
            log.debug("Курс валюты: " + currencyCache.get().getValue());
            return currencyCache.get().getValue();
        }

        log.debug("Получение курса валюты из API");
        CurrencyDto currencyDto = restTemplate.getForObject(String.format(
                "https://api.twelvedata.com/eod?symbol=%s&apikey=%s",
                value, apiKey
        ), CurrencyDto.class);



        if(currencyDto == null || currencyDto.getValue() == null) {
            throw new IllegalArgumentException("Неправильный формат обозначения ценной бумаги");
        }
        log.debug("Курс валюты: " + currencyDto.getValue());
        log.debug("Кэшируем полученные данные в БД");
        cacheRepository.save(new CurrencyCache(value, LocalDate.now(), currencyDto.getValue()));
        return currencyDto.getValue();
    }

    @Override
    public BigDecimal getCurrentValuePairs(String value1, String value2) {
        return getCurrentValue(value1 + "/" + value2);
    }

    @Override
    public BigDecimal getCurrentValuePairsToDefault(String value) {
        return getCurrentValuePairs(value, currencyDb);
    }

    @Override
    public BigDecimal getCurrentValuePairsFromDefault(String value) {
        return getCurrentValuePairs(currencyDb, value);
    }
}

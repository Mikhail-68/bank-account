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
import java.util.Optional;

@Slf4j
@Service
public class DataCurrencyServiceImpl implements DataCurrencyService {

    @Value("${application.twelvedata.apikey}")
    private String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    private final CurrencyCacheRepository cacheRepository;

    @Autowired
    public DataCurrencyServiceImpl(CurrencyCacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    public BigDecimal getCurrentValue(String value) {
        Optional<CurrencyCache> currencyCache = cacheRepository.findById(value);
        if (currencyCache.isPresent() && currencyCache.get().getDate().equals(LocalDate.now())) {
            log.debug("Получение валюты из кэша");
            return currencyCache.get().getValue();
        }

        log.debug("Получение валюты из API");
        CurrencyDto currencyDto = restTemplate.getForObject(String.format(
                "https://api.twelvedata.com/eod?symbol=%s&apikey=%s",
                value, apiKey
        ), CurrencyDto.class);
        log.debug("Кэшируем полученные данные в БД");
        cacheRepository.save(new CurrencyCache(value, LocalDate.now(), currencyDto.getValue()));
        return currencyDto.getValue();
    }

    public BigDecimal getCurrentValuePairs(String value1, String value2) {
        return getCurrentValue(value1 + "/" + value2);
    }
}

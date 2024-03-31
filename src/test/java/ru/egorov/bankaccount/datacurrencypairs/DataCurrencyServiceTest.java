package ru.egorov.bankaccount.datacurrencypairs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.egorov.bankaccount.datacurrencypairs.entity.CurrencyCache;
import ru.egorov.bankaccount.datacurrencypairs.repository.CurrencyCacheRepository;
import ru.egorov.bankaccount.datacurrencypairs.service.DataCurrencyService;
import ru.egorov.bankaccount.datacurrencypairs.service.DataCurrencyServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = WavefrontProperties.Application.class
)
@ActiveProfiles("test")
public class DataCurrencyServiceTest {

    @Mock
    private CurrencyCacheRepository cacheRepository;
    private DataCurrencyService currencyService;

    @BeforeEach
    public void init() {
        currencyService = new DataCurrencyServiceImpl(cacheRepository);
    }

    @Test
    @DisplayName("Проверка получения курса, когда курс не кэширован")
    public void getCurrencyWhenValueNotCache() {
        String currency = "USD/RUB";
        Mockito.when(cacheRepository.findById(currency)).thenReturn(Optional.empty());

        currencyService.getCurrentValue(currency);

        Mockito.verify(cacheRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("Проверка получения курса, когда курс кэширован")
    public void getCurrencyWhenValueCache() {
        String currency = "USD/RUB";
        BigDecimal expectedValue = BigDecimal.valueOf(12.34);
        Mockito.when(cacheRepository.findById(currency)).thenReturn(
                Optional.of(new CurrencyCache(currency, LocalDate.now(), expectedValue)));

        BigDecimal actualValue = currencyService.getCurrentValue(currency);

        Mockito.verify(cacheRepository, Mockito.times(0)).save(Mockito.any());
        Assertions.assertEquals(expectedValue.doubleValue(), actualValue.doubleValue());
    }

    @Test
    @DisplayName("Проверка правильного сопопоставления валют")
    public void currrencyValuePairsTest() {
        String currency1 = "USD";
        String currency2 = "RUB";
        String expectedCurrency = "USD/RUB";
        BigDecimal expectedValue = BigDecimal.valueOf(12.34);
        Mockito.when(cacheRepository.findById(expectedCurrency)).thenReturn(
                Optional.of(new CurrencyCache(expectedCurrency, LocalDate.now(), expectedValue)));

        BigDecimal actualValue = currencyService.getCurrentValuePairs(currency1, currency2);

        Assertions.assertEquals(expectedValue.doubleValue(), actualValue.doubleValue());
    }
}

package ru.egorov.bankaccount.datacurrencypairs.service;

import java.math.BigDecimal;

public interface DataCurrencyService {
    /**
     * Получение стоимости ценной бумаги
     *
     * @param value обозначение ценной бумаги
     * @return стоимость ценной бумаги
     */
    BigDecimal getCurrentValue(String value);

    /**
     * Получение стоимости валютной пары
     *
     * @param value1 первая валюта
     * @param value2 вторая валюта
     * @return стоимость валютной пары
     */
    BigDecimal getCurrentValuePairs(String value1, String value2);
}

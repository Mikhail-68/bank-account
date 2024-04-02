package ru.egorov.bankaccount.datacurrencypairs.service;

import java.math.BigDecimal;

public interface DataCurrencyService {
    /**
     * Получение стоимости ценной бумаги
     *
     * @param value обозначение ценной бумаги
     * @return стоимость ценной бумаги
     * @throws IllegalArgumentException при неправильном формате ценных бумаг
     */
    BigDecimal getCurrentValue(String value);

    /**
     * Получение стоимости валютной пары
     *
     * @param value1 первая валюта
     * @param value2 вторая валюта
     * @return стоимость валютной пары
     * @throws IllegalArgumentException при неправильном формате ценных бумаг
     */
    BigDecimal getCurrentValuePairs(String value1, String value2);

    /**
     * Возвращает курс валюты, относительно заданной валюты базы данных
     * @param value валюта
     * @return значение стоимости валюты
     */
    BigDecimal getCurrentValuePairsDefault(String value);
}

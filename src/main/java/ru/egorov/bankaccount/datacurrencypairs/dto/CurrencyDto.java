package ru.egorov.bankaccount.datacurrencypairs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CurrencyDto {
    @JsonProperty("close")
    private BigDecimal value;
}

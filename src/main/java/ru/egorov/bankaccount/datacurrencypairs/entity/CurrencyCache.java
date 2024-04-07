package ru.egorov.bankaccount.datacurrencypairs.entity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode

@Table
public class CurrencyCache {
    @PrimaryKey
    private String currency;
    private LocalDate date;
    private BigDecimal value;
}

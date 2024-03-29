package ru.egorov.bankaccount.datacurrencypairs.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import ru.egorov.bankaccount.datacurrencypairs.entity.CurrencyCache;

@Repository
public interface CurrencyCacheRepository extends CassandraRepository<CurrencyCache, String> {
}

package ru.egorov.bankaccount.bank.mapper;

import org.mapstruct.Mapper;
import ru.egorov.bankaccount.bank.dto.out.LimitDTO;
import ru.egorov.bankaccount.bank.entity.Limit;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class LimitMapper {

    public LimitDTO toLimitDTO(Limit limit) {
        return new LimitDTO(
                limit.getId(),
                limit.getDate(),
                limit.getSum().doubleValue(),
                limit.getExpenseCategory()
        );
    }

    public abstract List<LimitDTO> toLimitDTOList(List<Limit> limit);
}

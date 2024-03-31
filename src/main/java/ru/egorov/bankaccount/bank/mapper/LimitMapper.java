package ru.egorov.bankaccount.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.egorov.bankaccount.bank.dto.LimitDTO;
import ru.egorov.bankaccount.bank.entity.Limit;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LimitMapper {
    Limit toLimit(LimitDTO limitDTO);
    LimitDTO toLimitDTO(Limit limit);

    List<Limit> toLimitList(List<LimitDTO> limitDTO);
    List<LimitDTO> toLimitDTOList(List<Limit> limit);
}

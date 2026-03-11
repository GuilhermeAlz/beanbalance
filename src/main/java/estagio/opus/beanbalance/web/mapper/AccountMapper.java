package estagio.opus.beanbalance.web.mapper;

import estagio.opus.beanbalance.domain.entity.Account;
import estagio.opus.beanbalance.web.dto.account.AccountRequest;
import estagio.opus.beanbalance.web.dto.account.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponse toResponse(Account account);

    Account toEntity(AccountRequest request);

    void updateEntity(AccountRequest request, @MappingTarget Account account);
}

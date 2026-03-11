package estagio.opus.beanbalance.domain.repository;

import estagio.opus.beanbalance.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findAllByUserId(UUID userId);

    Optional<Account> findByIdAndUserId(UUID id, UUID userId);
}

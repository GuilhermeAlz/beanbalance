package estagio.opus.beanbalance.domain.repository;

import estagio.opus.beanbalance.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByExternalAuthId(String externalAuthId);

    boolean existsByEmail(String email);
}

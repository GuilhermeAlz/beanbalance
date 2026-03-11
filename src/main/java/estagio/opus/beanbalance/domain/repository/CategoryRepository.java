package estagio.opus.beanbalance.domain.repository;

import estagio.opus.beanbalance.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM Category c WHERE c.user.id = :userId OR c.custom = false")
    List<Category> findAllAccessibleByUser(UUID userId);

    Optional<Category> findByIdAndUserId(UUID id, UUID userId);
}

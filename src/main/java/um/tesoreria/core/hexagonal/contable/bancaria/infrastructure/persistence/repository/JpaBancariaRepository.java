package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.entity.BancariaEntity;

import java.util.Optional;

@Repository
public interface JpaBancariaRepository extends JpaRepository<BancariaEntity, Long> {

    // específico de MySQL: revisar al migrar a Postgres. @Lock(PESSIMISTIC_WRITE) es JPA estándar, pero depende del
    // dialecto MySQL57Dialect (Hibernate 7 genera FOR UPDATE OF, que MySQL 5.7 rechaza).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BancariaEntity> findWithLockByBancariaId(Long bancariaId);
}

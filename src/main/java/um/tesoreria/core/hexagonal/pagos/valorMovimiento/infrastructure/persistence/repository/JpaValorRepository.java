package um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.entity.ValorEntity;

@Repository
public interface JpaValorRepository extends JpaRepository<ValorEntity, Integer> {
}

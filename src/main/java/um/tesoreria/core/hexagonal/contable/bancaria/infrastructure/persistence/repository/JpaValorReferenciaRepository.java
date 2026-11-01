package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.repository;

import org.springframework.data.repository.Repository;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.entity.ValorReferenciaEntity;

/** Solo lectura: Repository (y no JpaRepository) no expone escrituras sobre valores. */
public interface JpaValorReferenciaRepository extends Repository<ValorReferenciaEntity, Long> {

    boolean existsByBancariaIdOrigen(Long bancariaId);

    long countByBancariaIdOrigen(Long bancariaId);
}

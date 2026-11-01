package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.repository;

import org.springframework.data.repository.Repository;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.entity.LibroBancoReferenciaEntity;

/** Solo lectura: Repository (y no JpaRepository) no expone escrituras sobre libro_banco. */
public interface JpaLibroBancoReferenciaRepository extends Repository<LibroBancoReferenciaEntity, Long> {

    boolean existsByBancariaId(Long bancariaId);

    long countByBancariaId(Long bancariaId);
}

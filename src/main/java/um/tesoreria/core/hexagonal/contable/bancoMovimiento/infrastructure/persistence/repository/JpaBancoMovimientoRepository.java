package um.tesoreria.core.hexagonal.contable.bancoMovimiento.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.infrastructure.persistence.entity.BancoMovimientoEntity;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface JpaBancoMovimientoRepository extends JpaRepository<BancoMovimientoEntity, Long> {

    Optional<BancoMovimientoEntity> findTopByBancariaIdAndFechaOrderByOrdenDesc(Long bancariaId,
                                                                               OffsetDateTime fecha);
}

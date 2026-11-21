package um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.entity.ValorMovimientoEntity;

import java.util.List;

@Repository
public interface JpaValorMovimientoRepository extends JpaRepository<ValorMovimientoEntity, Long> {

    List<ValorMovimientoEntity> findAllByValorMovimientoIdInOrderByValorMovimientoIdAsc(
            List<Long> valorMovimientoIds);
}

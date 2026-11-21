package um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.entity.EntregaEntity;

import java.util.List;

@Repository
public interface JpaEntregaRepository extends JpaRepository<EntregaEntity, Long> {

    List<EntregaEntity> findAllByEntregaIdIn(List<Long> entregaIds);
}

package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.entity.ReemplazoTotalEntity;

import java.util.Optional;

@Repository
public interface JpaReemplazoTotalRepository extends JpaRepository<ReemplazoTotalEntity, Long> {

    Optional<ReemplazoTotalEntity> findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoId(
            Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId);

}

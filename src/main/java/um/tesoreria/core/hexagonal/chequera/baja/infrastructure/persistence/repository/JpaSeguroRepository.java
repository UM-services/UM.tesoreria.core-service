package um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.entity.SeguroEntity;
import um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.entity.SeguroId;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface JpaSeguroRepository extends JpaRepository<SeguroEntity, SeguroId> {

    Optional<SeguroEntity> findByDocumentoIdAndPersonaIdAndLectivoId(Integer documentoId, BigDecimal personaId,
                                                                     Integer lectivoId);

}

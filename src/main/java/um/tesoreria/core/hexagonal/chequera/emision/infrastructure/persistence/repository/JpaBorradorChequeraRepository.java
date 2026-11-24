package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.BorradorChequeraEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaBorradorChequeraRepository extends JpaRepository<BorradorChequeraEntity, Long> {

    Optional<BorradorChequeraEntity> findByClaveIdempotencia(String claveIdempotencia);

    List<BorradorChequeraEntity> findAllByUsuarioIdAndEstadoOrderByBorradorIdDesc(String usuarioId, EstadoBorrador estado);

    List<BorradorChequeraEntity> findAllByLectivoIdAndFacultadIdAndEstadoOrderByBorradorIdAsc(Integer lectivoId,
                                                                                              Integer facultadId,
                                                                                              EstadoBorrador estado);

    List<BorradorChequeraEntity> findAllByLectivoIdAndEstadoOrderByBorradorIdAsc(Integer lectivoId,
                                                                                 EstadoBorrador estado);

    List<BorradorChequeraEntity> findAllByEstadoAndUpdatedBefore(EstadoBorrador estado, LocalDateTime limite);

    Optional<BorradorChequeraEntity> findFirstByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndEstadoOrderByBorradorIdDesc(
            Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, EstadoBorrador estado);

    Optional<BorradorChequeraEntity>
    findFirstByPersonaIdAndDocumentoIdAndLectivoIdAndFacultadIdAndTipoChequeraIdAndChequeraSerieIdIsNullAndEstadoOrderByBorradorIdDesc(
            BigDecimal personaId, Integer documentoId, Integer lectivoId, Integer facultadId,
            Integer tipoChequeraId, EstadoBorrador estado);
}

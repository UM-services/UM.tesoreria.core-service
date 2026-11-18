package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface BorradorChequeraRepository {

    BorradorChequera save(BorradorChequera borrador);

    Optional<BorradorChequera> findById(Long borradorId);

    Optional<BorradorChequera> findByClaveIdempotencia(String claveIdempotencia);

    List<BorradorChequera> findAllByUsuarioIdAndEstado(String usuarioId, EstadoBorrador estado);

    /**
     * Borrador abierto más reciente de una serie concreta. Permite reparar una chequera sin
     * duplicar borradores si la generación se dispara dos veces.
     */
    Optional<BorradorChequera> findAbiertoBySerie(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

    /**
     * Borrador abierto de una chequera que todavía no existe (sin número), para la misma persona,
     * lectivo, facultad y tipo. Evita duplicados al iniciar dos veces la emisión.
     */
    Optional<BorradorChequera> findAbiertoByNueva(java.math.BigDecimal personaId, Integer documentoId,
                                                  Integer lectivoId, Integer facultadId, Integer tipoChequeraId);

    void deleteById(Long borradorId);

    int descartarAnterioresA(OffsetDateTime limite);
}

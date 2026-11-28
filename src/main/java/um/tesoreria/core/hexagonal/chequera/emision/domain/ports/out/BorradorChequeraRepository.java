package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorSede;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.PaginaBorradores;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface BorradorChequeraRepository {

    BorradorChequera save(BorradorChequera borrador);

    Optional<BorradorChequera> findById(Long borradorId);

    Optional<BorradorChequera> findByClaveIdempotencia(String claveIdempotencia);

    List<BorradorChequera> findAllByUsuarioIdAndEstado(String usuarioId, EstadoBorrador estado);

    /**
     * Página de borradores de un lectivo/facultad limitada a los tipos de chequera de una sede.
     */
    PaginaBorradores findAllByLectivoIdAndFacultadIdAndTipoChequeraIdInAndEstado(Integer lectivoId,
                                                                                Integer facultadId,
                                                                                List<Integer> tipoChequeraIds,
                                                                                EstadoBorrador estado,
                                                                                int page,
                                                                                int size);

    /**
     * Cantidad de borradores de un lectivo/facultad limitada a los tipos de chequera de una sede.
     */
    long countByLectivoIdAndFacultadIdAndTipoChequeraIdInAndEstado(Integer lectivoId,
                                                                   Integer facultadId,
                                                                   List<Integer> tipoChequeraIds,
                                                                   EstadoBorrador estado);

    /**
     * Borradores abiertos de una facultad para un conjunto de series (vista previa de generación).
     */
    List<BorradorChequera> findAllByFacultadIdAndChequeraSerieIdInAndEstado(Integer facultadId,
                                                                           List<Long> chequeraSerieIds,
                                                                           EstadoBorrador estado);

    /**
     * Borradores de una facultad y lectivo en un estado (consulta de exportación por sede).
     */
    List<BorradorChequera> findAllByLectivoIdAndFacultadIdAndEstado(Integer lectivoId, Integer facultadId,
                                                                    EstadoBorrador estado);

    /**
     * Referencias mínimas (facultad y tipo de chequera) de los borradores de un lectivo, para
     * armar el resumen por facultad/sede sin cargar cuotas.
     */
    List<BorradorSede> findAllSedesByLectivoIdAndEstado(Integer lectivoId, EstadoBorrador estado);

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

package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out;

import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.DebitoCobranza;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a los débitos que responden los archivos de Débito Directo y VISA.
 *
 * <p>La importación sólo necesita localizar el débito, linkearlo a la fila del archivo y marcar
 * los rechazos. Las actualizaciones se hacen sobre la entidad existente, preservando el resto de
 * sus campos.</p>
 */
public interface DebitoCobranzaRepository {

    Optional<DebitoCobranza> findById(Long debitoId);

    Optional<DebitoCobranza> findByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                         Integer productoId, Integer alternativaId, Integer cuotaId,
                                         Integer debitoTipoId);

    void marcarArchivoBanco(Long debitoId, Long archivoBancoId);

    void marcarRechazado(Long debitoId, String motivoRechazo, Long archivoBancoId);

    List<DebitoCobranza> findAllByArchivoBancoIds(Collection<Long> archivoBancoIds);

}

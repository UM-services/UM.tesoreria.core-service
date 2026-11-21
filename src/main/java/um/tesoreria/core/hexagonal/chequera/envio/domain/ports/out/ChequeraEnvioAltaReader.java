package um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;

import java.util.List;
import java.util.Optional;

/**
 * Lectura del padrón. Una sola consulta: vista de altas + persona + domicilio + tipo de chequera
 * + tipo de arancel + {@code chequera_serie.retenida}.
 */
public interface ChequeraEnvioAltaReader {

    List<ChequeraEnvioAlta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId,
                                                                          Integer facultadId,
                                                                          Integer geograficaId);

    /** Relectura de una fila al ejecutar la tarea (refleja correos capturados y retención). */
    Optional<ChequeraEnvioAlta> findByChequeraId(Long chequeraId);
}

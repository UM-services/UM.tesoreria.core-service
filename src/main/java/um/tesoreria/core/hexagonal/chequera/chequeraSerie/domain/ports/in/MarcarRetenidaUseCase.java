package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

/**
 * Marca (o desmarca) la retención de una serie. Es una escritura puntual de {@code retenida}:
 * el envío masivo la usa para reflejar la notificación de deuda en lugar de la chequera.
 */
public interface MarcarRetenidaUseCase {

    ChequeraSerie marcar(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, boolean retenida);
}

package um.tesoreria.core.hexagonal.chequera.envio.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitud;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudClaim;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudCreacion;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudEstado;

import java.util.UUID;

/**
 * Ciclo de vida de una solicitud de envío.
 *
 * <p>La creaci\u00f3n es idempotente por chequera; el reclamo es at\u00f3mico; el cierre registra el
 * resultado real y actualiza la serie, el message check y la tarea del job en una sola
 * transacci\u00f3n.</p>
 */
public interface EnvioSolicitudUseCase {

    EnvioSolicitudCreacion crear(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                 Long jobId, String origen);

    EnvioSolicitudClaim reclamar(UUID solicitudId);

    EnvioSolicitud marcarEnviado(UUID solicitudId);

    EnvioSolicitud marcarFallido(UUID solicitudId, String error);

    EnvioSolicitud marcarOmitida(UUID solicitudId, EnvioSolicitudEstado estado, String mensaje);

    EnvioSolicitud consultar(UUID solicitudId);

}

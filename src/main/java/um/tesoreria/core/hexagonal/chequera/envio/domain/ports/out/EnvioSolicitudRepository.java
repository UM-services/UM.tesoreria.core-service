package um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitud;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistencia de las solicitudes de envío.
 *
 * <p>La lectura con lock pesimista es la que serializa el reclamo entre instancias del sender.</p>
 */
public interface EnvioSolicitudRepository {

    EnvioSolicitud save(EnvioSolicitud solicitud);

    Optional<EnvioSolicitud> findBySolicitudId(UUID solicitudId);

    Optional<EnvioSolicitud> findBySolicitudIdForUpdate(UUID solicitudId);

    Optional<EnvioSolicitud> findByChequeraId(Long chequeraId);

    Optional<EnvioSolicitud> findByDedupeKey(String dedupeKey);

}

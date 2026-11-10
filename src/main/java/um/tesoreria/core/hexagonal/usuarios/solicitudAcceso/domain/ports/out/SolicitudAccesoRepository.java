package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;

import java.util.List;
import java.util.Optional;

public interface SolicitudAccesoRepository {

    SolicitudAcceso save(SolicitudAcceso solicitud);

    Optional<SolicitudAcceso> findById(Long solicitudAccesoId);

    List<SolicitudAcceso> findPendientes();

    List<SolicitudAcceso> findByUserId(Long userId);

    boolean existsPendienteByUserIdAndClave(Long userId, String clave);
}

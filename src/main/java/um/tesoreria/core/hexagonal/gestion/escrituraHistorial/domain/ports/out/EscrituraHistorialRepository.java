package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out;

import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraHistorial;

import java.util.List;

/**
 * Puerto de salida de persistencia. Sin exposición HTTP.
 */
public interface EscrituraHistorialRepository {

    EscrituraHistorial save(EscrituraHistorial historial);

    List<EscrituraHistorial> findAllByEntidadAndEntidadClaveOrderByFechaAscEscrituraHistorialIdAsc(
            String entidad, String entidadClave);
}

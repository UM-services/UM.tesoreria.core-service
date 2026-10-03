package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out;

import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraHistorial;

/**
 * Puerto de salida de persistencia. Solo alta de eventos; sin exposición HTTP.
 */
public interface EscrituraHistorialRepository {

    EscrituraHistorial save(EscrituraHistorial historial);
}

package um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model;

/**
 * Resultado de guardar una asignación: {@code anterior} es el vínculo como estaba (leído con bloqueo) o nulo si se
 * insertó uno nuevo.
 */
public record AsignacionGuardada(UbicacionArticulo anterior, UbicacionArticulo guardada) {
}

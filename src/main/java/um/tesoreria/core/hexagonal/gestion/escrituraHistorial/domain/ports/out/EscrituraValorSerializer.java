package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out;

/**
 * Serializa el estado de una entidad a texto compacto para el historial.
 * Un valor nulo o ausente se representa como cadena vacía (caso baja / alta).
 */
public interface EscrituraValorSerializer {

    String serialize(Object valor);
}

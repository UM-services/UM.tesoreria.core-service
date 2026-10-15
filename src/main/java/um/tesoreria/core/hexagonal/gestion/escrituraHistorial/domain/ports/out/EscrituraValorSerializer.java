package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out;

/**
 * Serializa una copia del estado a JSON compacto para el historial.
 * {@code null} se persiste como NULL (anterior del alta, nuevo de la baja).
 */
public interface EscrituraValorSerializer {

    String serialize(Object valor);
}

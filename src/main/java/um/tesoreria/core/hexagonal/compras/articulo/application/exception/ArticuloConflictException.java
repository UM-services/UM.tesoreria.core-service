package um.tesoreria.core.hexagonal.compras.articulo.application.exception;

import lombok.Getter;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ReferenciaArticulo;

import java.util.List;

/**
 * La escritura choca con el estado de la base (409).
 */
@Getter
public class ArticuloConflictException extends RuntimeException {

    public enum Motivo { ID_DUPLICADO, REFERENCIADO, CONFLICTO }

    private final Motivo motivo;
    private final Long articuloId;
    private final List<ReferenciaArticulo> referencias;

    private ArticuloConflictException(Motivo motivo, Long articuloId, List<ReferenciaArticulo> referencias, String mensaje) {
        super(mensaje);
        this.motivo = motivo;
        this.articuloId = articuloId;
        this.referencias = List.copyOf(referencias);
    }

    public static ArticuloConflictException idDuplicado(Long articuloId) {
        return new ArticuloConflictException(Motivo.ID_DUPLICADO, articuloId, List.of(),
                "El artículo " + articuloId + " ya existe");
    }

    /** {@code referencias} vacía: la base rechazó el borrado sin decir qué tabla lo referencia. */
    public static ArticuloConflictException referenciado(Long articuloId, List<ReferenciaArticulo> referencias) {
        return new ArticuloConflictException(Motivo.REFERENCIADO, articuloId, referencias,
                "El artículo " + articuloId + " está referenciado");
    }

    public static ArticuloConflictException conflicto(Long articuloId) {
        return new ArticuloConflictException(Motivo.CONFLICTO, articuloId, List.of(),
                "La escritura del artículo " + articuloId + " choca con otro dato");
    }

}

package um.tesoreria.core.hexagonal.compras.articulo.application.exception;

import lombok.Getter;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ReferenciaArticulo;

import java.util.List;

/**
 * La escritura choca con el estado de la base (409).
 */
@Getter
public class ArticuloConflictException extends RuntimeException {

    public enum Motivo { ID_DUPLICADO, REFERENCIADO, CONFLICTO, BLOQUEADO }

    private final Motivo motivo;
    private final Long articuloId;
    private final List<ReferenciaArticulo> referencias;
    /** Repetir la operación en una transacción nueva puede resolverlo (interbloqueo). */
    private final boolean reintentable;

    private ArticuloConflictException(Motivo motivo, Long articuloId, List<ReferenciaArticulo> referencias, boolean reintentable, String mensaje) {
        super(mensaje);
        this.motivo = motivo;
        this.articuloId = articuloId;
        this.referencias = List.copyOf(referencias);
        this.reintentable = reintentable;
    }

    public static ArticuloConflictException idDuplicado(Long articuloId) {
        return new ArticuloConflictException(Motivo.ID_DUPLICADO, articuloId, List.of(), false,
                "El artículo " + articuloId + " ya existe");
    }

    /** {@code referencias} vacía: la base rechazó el borrado sin decir qué tabla lo referencia. */
    public static ArticuloConflictException referenciado(Long articuloId, List<ReferenciaArticulo> referencias) {
        return new ArticuloConflictException(Motivo.REFERENCIADO, articuloId, referencias, false,
                "El artículo " + articuloId + " está referenciado");
    }

    public static ArticuloConflictException conflicto(Long articuloId) {
        return new ArticuloConflictException(Motivo.CONFLICTO, articuloId, List.of(), false,
                "La escritura del artículo " + articuloId + " choca con otro dato");
    }

    /** Interbloqueo (reintentable) o espera de bloqueo vencida (no: otro tiene la fila tomada hace rato). */
    public static ArticuloConflictException bloqueado(Long articuloId, boolean reintentable) {
        return new ArticuloConflictException(Motivo.BLOQUEADO, articuloId, List.of(), reintentable,
                (reintentable ? "Interbloqueo" : "Espera de bloqueo vencida") + " sobre el artículo " + articuloId);
    }

}

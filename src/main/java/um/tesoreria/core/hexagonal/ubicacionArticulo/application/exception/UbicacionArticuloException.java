package um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception;

import java.text.MessageFormat;

/**
 * No existe el vínculo ubicación-artículo (404).
 */
public class UbicacionArticuloException extends RuntimeException {

    public UbicacionArticuloException(Integer ubicacionId, Long articuloId) {
        super(MessageFormat.format("Cannot find UbicacionArticulo {0}:{1}", ubicacionId, articuloId));
    }

}

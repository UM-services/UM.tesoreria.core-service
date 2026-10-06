package um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception;

import lombok.Getter;

/**
 * La escritura del vínculo chocó con otra (409). {@code reintentable}: otra transacción insertó el mismo par
 * o hubo un interbloqueo; repetir la operación en una transacción nueva la resuelve.
 */
@Getter
public class UbicacionArticuloConflictException extends RuntimeException {

    private final boolean reintentable;

    public UbicacionArticuloConflictException(boolean reintentable, String mensaje) {
        super(mensaje);
        this.reintentable = reintentable;
    }

}

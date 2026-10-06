package um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception;

import lombok.Getter;

/**
 * La escritura del vínculo chocó con otra (409). {@code reintentable}: otra transacción insertó el mismo par
 * o hubo un interbloqueo; repetir la operación en una transacción nueva la resuelve.
 */
@Getter
public class UbicacionArticuloConflictException extends RuntimeException {

    private final boolean reintentable;
    /** Otra transacción tuvo la fila tomada más que la espera de bloqueo de MySQL: no se reintenta. */
    private final boolean bloqueado;

    public UbicacionArticuloConflictException(boolean reintentable, String mensaje) {
        this(reintentable, false, mensaje);
    }

    private UbicacionArticuloConflictException(boolean reintentable, boolean bloqueado, String mensaje) {
        super(mensaje);
        this.reintentable = reintentable;
        this.bloqueado = bloqueado;
    }

    public static UbicacionArticuloConflictException bloqueado(String mensaje) {
        return new UbicacionArticuloConflictException(false, true, mensaje);
    }

}

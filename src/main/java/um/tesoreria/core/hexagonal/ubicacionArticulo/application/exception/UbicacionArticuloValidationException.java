package um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception;

import lombok.Getter;

/**
 * Dato de entrada inválido (400). {@code campo} es el nombre JSON del DTO; nulo si no se puede atribuir a uno.
 */
@Getter
public class UbicacionArticuloValidationException extends RuntimeException {

    private final String campo;

    public UbicacionArticuloValidationException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

}

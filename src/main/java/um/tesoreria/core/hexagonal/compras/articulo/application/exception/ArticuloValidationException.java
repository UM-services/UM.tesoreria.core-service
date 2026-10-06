package um.tesoreria.core.hexagonal.compras.articulo.application.exception;

import lombok.Getter;

/**
 * Dato de entrada inválido (400). {@code campo} es el nombre JSON del DTO; nulo si no se puede atribuir a uno.
 */
@Getter
public class ArticuloValidationException extends RuntimeException {

    private final String campo;

    public ArticuloValidationException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

}

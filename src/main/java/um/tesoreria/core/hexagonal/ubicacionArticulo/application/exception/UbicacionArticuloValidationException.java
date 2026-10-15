package um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception;

import lombok.Getter;

/**
 * Dato de entrada inválido (400). {@code campo} es el nombre JSON del DTO; nulo si no se puede atribuir a uno.
 */
@Getter
public class UbicacionArticuloValidationException extends RuntimeException {

    // Los usan la validación previa y la traducción de las FK, que la respaldan si otro borra el dato entre medio
    public static final String UBICACION_INEXISTENTE = "La ubicación indicada no existe.";
    public static final String ARTICULO_INEXISTENTE = "El artículo indicado no existe.";
    public static final String CUENTA_INEXISTENTE = "La cuenta indicada no existe en el plan de cuentas.";

    private final String campo;

    public UbicacionArticuloValidationException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

}

package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

/**
 * Estado de una línea durante la previsualización. No se persiste: el "log" de lo que quedó
 * imputado sigue siendo {@code archivo_banco.ArB_Pasado}, igual que en el sistema anterior.
 */
public enum EstadoLinea {

    OK,
    NO_ENCONTRADA,
    SIN_ALTERNATIVA,
    DUPLICADA,
    RECHAZADA

}

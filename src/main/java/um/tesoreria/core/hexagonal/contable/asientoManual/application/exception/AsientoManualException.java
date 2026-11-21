package um.tesoreria.core.hexagonal.contable.asientoManual.application.exception;

import org.springframework.http.HttpStatus;

public class AsientoManualException extends RuntimeException {

    public enum Codigo {
        SIN_LINEAS(HttpStatus.BAD_REQUEST),
        LINEA_INVALIDA(HttpStatus.BAD_REQUEST),
        ORDEN_OCUPADO(HttpStatus.CONFLICT),
        ASIENTO_INEXISTENTE(HttpStatus.NOT_FOUND),
        EJERCICIO_INEXISTENTE(HttpStatus.CONFLICT),
        EJERCICIO_BLOQUEADO(HttpStatus.CONFLICT),
        IMPORTACION_INVALIDA(HttpStatus.BAD_REQUEST);

        private final HttpStatus status;

        Codigo(HttpStatus status) {
            this.status = status;
        }

        public HttpStatus status() {
            return status;
        }
    }

    private final Codigo codigo;

    public AsientoManualException(Codigo codigo, String message) {
        super(message);
        this.codigo = codigo;
    }

    public AsientoManualException(Codigo codigo, String message, Throwable cause) {
        super(message, cause);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }

}

package um.tesoreria.core.hexagonal.contable.asientoCierre.application.exception;

import org.springframework.http.HttpStatus;

public class AsientoCierreException extends RuntimeException {

    public enum Codigo {
        EJERCICIO_INEXISTENTE(HttpStatus.NOT_FOUND),
        EJERCICIO_BLOQUEADO(HttpStatus.CONFLICT),
        SIN_LINEAS(HttpStatus.BAD_REQUEST),
        LINEA_INVALIDA(HttpStatus.BAD_REQUEST);

        private final HttpStatus status;

        Codigo(HttpStatus status) {
            this.status = status;
        }

        public HttpStatus status() {
            return status;
        }
    }

    private final Codigo codigo;

    public AsientoCierreException(Codigo codigo, String message) {
        super(message);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }

}

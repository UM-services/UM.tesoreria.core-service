package um.tesoreria.core.hexagonal.contable.asientoApertura.application.exception;

import org.springframework.http.HttpStatus;

public class AsientoAperturaException extends RuntimeException {

    public enum Codigo {
        EJERCICIO_INEXISTENTE(HttpStatus.NOT_FOUND),
        EJERCICIO_ANTERIOR_INEXISTENTE(HttpStatus.CONFLICT),
        REFERENCIA_INVALIDA(HttpStatus.BAD_REQUEST),
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

    public AsientoAperturaException(Codigo codigo, String message) {
        super(message);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }

}

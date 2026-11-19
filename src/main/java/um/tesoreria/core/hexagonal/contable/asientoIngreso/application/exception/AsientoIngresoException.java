package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception;

import org.springframework.http.HttpStatus;

/** Rechazo de negocio del slice {@code asientoIngreso}. */
public class AsientoIngresoException extends RuntimeException {

    public enum Codigo {
        EJERCICIO_INEXISTENTE(HttpStatus.CONFLICT),
        EJERCICIO_BLOQUEADO(HttpStatus.CONFLICT),
        CUENTA_TIPO_PAGO_FALTANTE(HttpStatus.CONFLICT),
        RANGO_INVALIDO(HttpStatus.BAD_REQUEST),
        RANGO_EXCEDIDO(HttpStatus.BAD_REQUEST),
        JOB_NO_ENCONTRADO(HttpStatus.NOT_FOUND),
        JOB_YA_FINALIZADO(HttpStatus.CONFLICT),
        REGENERACION_FALLIDA(HttpStatus.CONFLICT);

        private final HttpStatus status;

        Codigo(HttpStatus status) {
            this.status = status;
        }

        public HttpStatus status() {
            return status;
        }
    }

    private final Codigo codigo;

    public AsientoIngresoException(Codigo codigo, String message) {
        super(message);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }

}

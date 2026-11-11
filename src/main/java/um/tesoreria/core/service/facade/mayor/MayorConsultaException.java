package um.tesoreria.core.service.facade.mayor;

import org.springframework.http.HttpStatus;

/** Rechazo de negocio de la consulta del mayor (se responde con el estado de {@link Codigo#status()}). */
public class MayorConsultaException extends RuntimeException {

    public enum Codigo {
        FECHAS_REQUERIDAS(HttpStatus.BAD_REQUEST),
        SELECCION_INVALIDA(HttpStatus.CONFLICT),
        DESDE_POSTERIOR(HttpStatus.CONFLICT),
        FUERA_DE_EJERCICIO(HttpStatus.CONFLICT),
        EJERCICIOS_NO_CONSECUTIVOS(HttpStatus.CONFLICT),
        RANGO_SIN_CUENTAS(HttpStatus.CONFLICT),
        CUENTA_INEXISTENTE(HttpStatus.NOT_FOUND);

        private final HttpStatus status;

        Codigo(HttpStatus status) {
            this.status = status;
        }

        public HttpStatus status() {
            return status;
        }
    }

    private final Codigo codigo;

    public MayorConsultaException(Codigo codigo, String message) {
        super(message);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }
}
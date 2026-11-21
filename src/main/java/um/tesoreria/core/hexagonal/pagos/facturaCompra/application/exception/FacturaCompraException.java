package um.tesoreria.core.hexagonal.pagos.facturaCompra.application.exception;

import org.springframework.http.HttpStatus;

public class FacturaCompraException extends RuntimeException {

    public enum Codigo {
        FECHA_INVALIDA(HttpStatus.BAD_REQUEST),
        TOTAL_INVALIDO(HttpStatus.BAD_REQUEST),
        ARTICULOS_INVALIDOS(HttpStatus.BAD_REQUEST),
        SUMA_ARTICULOS_INVALIDA(HttpStatus.BAD_REQUEST),
        PROVEEDOR_INEXISTENTE(HttpStatus.CONFLICT),
        COMPROBANTE_INEXISTENTE(HttpStatus.CONFLICT),
        FACTURA_DUPLICADA(HttpStatus.CONFLICT),
        ARTICULO_INEXISTENTE(HttpStatus.CONFLICT),
        CUENTA_FALTANTE(HttpStatus.CONFLICT),
        FACTURA_INEXISTENTE(HttpStatus.NOT_FOUND);

        private final HttpStatus status;

        Codigo(HttpStatus status) {
            this.status = status;
        }

        public HttpStatus status() {
            return status;
        }
    }

    private final Codigo codigo;

    public FacturaCompraException(Codigo codigo, String message) {
        super(message);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }

}

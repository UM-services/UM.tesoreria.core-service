package um.tesoreria.core.hexagonal.chequera.producto.application.exception;

import java.text.MessageFormat;

public class ProductoException extends RuntimeException {

    private static final long serialVersionUID = -2093160998381916119L;

    public ProductoException(Integer productoId) {
        super(MessageFormat.format("Cannot find Producto {0}", productoId));
    }

    public ProductoException() {
        super("Cannot find Producto");
    }
}

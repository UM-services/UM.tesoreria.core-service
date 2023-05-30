package um.tesoreria.rest.exception;

import java.text.MessageFormat;

public class ProveedorArticuloException extends RuntimeException {
	
    private static final long serialVersionUID = 4196299057083931074L;

	public ProveedorArticuloException(Long proveedorArticuloId) {
        super(MessageFormat.format("Cannot find ProveedorArticulo {0}", proveedorArticuloId));
    }
	
}

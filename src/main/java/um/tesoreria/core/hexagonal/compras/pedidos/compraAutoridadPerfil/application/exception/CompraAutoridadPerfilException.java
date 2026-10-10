package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.exception;

public class CompraAutoridadPerfilException extends RuntimeException {

    public CompraAutoridadPerfilException(String message) {
        super(message);
    }

    public CompraAutoridadPerfilException(Long autoridadPerfilId) {
        super("No existe el perfil de autoridad con id: " + autoridadPerfilId);
    }

}

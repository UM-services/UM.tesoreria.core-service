package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in;

public interface AsignarAutoridadUsuarioUseCase {

    void asignar(Integer usuarioId, Long autoridadPerfilId);

}

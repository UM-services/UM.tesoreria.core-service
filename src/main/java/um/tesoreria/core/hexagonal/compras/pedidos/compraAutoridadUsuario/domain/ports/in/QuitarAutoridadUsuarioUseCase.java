package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in;

public interface QuitarAutoridadUsuarioUseCase {

    void quitar(Integer usuarioId, Long autoridadPerfilId);

}

package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in;

import java.util.List;

public interface GetPerfilIdsByUsuarioUseCase {

    List<Long> getPerfilIds(Integer usuarioId);

}

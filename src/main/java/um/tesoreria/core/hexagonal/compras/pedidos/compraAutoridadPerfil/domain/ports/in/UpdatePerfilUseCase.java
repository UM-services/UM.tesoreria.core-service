package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;

public interface UpdatePerfilUseCase {

    CompraAutoridadPerfil update(CompraAutoridadPerfil perfil, Long autoridadPerfilId);

}

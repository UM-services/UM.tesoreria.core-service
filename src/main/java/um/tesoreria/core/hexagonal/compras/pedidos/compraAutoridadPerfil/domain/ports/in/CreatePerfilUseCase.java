package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;

public interface CreatePerfilUseCase {

    CompraAutoridadPerfil create(CompraAutoridadPerfil perfil);

}

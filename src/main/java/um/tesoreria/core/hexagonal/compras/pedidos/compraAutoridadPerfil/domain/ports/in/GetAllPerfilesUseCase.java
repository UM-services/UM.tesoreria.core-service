package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;

import java.util.List;

public interface GetAllPerfilesUseCase {

    List<CompraAutoridadPerfil> getAll();

}

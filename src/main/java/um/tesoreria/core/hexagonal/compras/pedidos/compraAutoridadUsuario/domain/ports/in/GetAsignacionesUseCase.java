package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.model.CompraAutoridadUsuario;

import java.util.List;

public interface GetAsignacionesUseCase {

    List<CompraAutoridadUsuario> getAsignaciones();

}

package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;

import java.util.Optional;

public interface GetReferenciaByEjercicioUseCase {

    Optional<CompraReferencia> getByEjercicioId(Integer ejercicioId);

}

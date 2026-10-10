package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;

import java.util.Optional;

public interface CompraReferenciaRepository {

    Optional<CompraReferencia> findByEjercicioId(Integer ejercicioId);

    CompraReferencia save(CompraReferencia referencia);

}

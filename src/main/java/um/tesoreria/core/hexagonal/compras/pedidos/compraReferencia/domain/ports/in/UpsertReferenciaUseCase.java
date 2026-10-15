package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;

public interface UpsertReferenciaUseCase {

    CompraReferencia upsert(CompraReferencia referencia);

}

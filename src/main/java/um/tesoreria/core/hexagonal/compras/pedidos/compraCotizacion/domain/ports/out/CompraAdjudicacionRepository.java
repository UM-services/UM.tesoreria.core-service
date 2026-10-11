package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;

import java.util.Optional;

/** Persistencia del acto de adjudicación (etapa 7). */
public interface CompraAdjudicacionRepository {

    CompraAdjudicacion save(CompraAdjudicacion adjudicacion);

    /** Última adjudicación registrada para un proceso, si existe. */
    Optional<CompraAdjudicacion> findByCompraCotizacionId(Long compraCotizacionId);
}

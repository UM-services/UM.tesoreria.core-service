package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionCriteria;

import java.util.List;
import java.util.Optional;

public interface CompraFormalizacionRepository {

    CompraFormalizacion save(CompraFormalizacion formalizacion);

    Optional<CompraFormalizacion> findById(Long compraFormalizacionId);

    /** La OC emitida para una adjudicación, si existe (una sola por adjudicación). */
    Optional<CompraFormalizacion> findByCompraAdjudicacionId(Long compraAdjudicacionId);

    boolean existsByCompraAdjudicacionId(Long compraAdjudicacionId);

    List<CompraFormalizacion> findByCriteria(CompraFormalizacionCriteria criteria);

}

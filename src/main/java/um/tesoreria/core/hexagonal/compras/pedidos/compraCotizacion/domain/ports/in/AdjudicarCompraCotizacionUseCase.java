package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacionDecision;

import java.math.BigDecimal;

/**
 * Registra la adjudicación de un proceso recomendado (etapa 7): persiste el acto y aplica el
 * estado resultante al proceso. La validación del límite por monto vive en la fachada (fail-closed).
 */
public interface AdjudicarCompraCotizacionUseCase {

    CompraAdjudicacion adjudicar(Long compraCotizacionId, CompraAdjudicacionDecision decision,
                                 Long compraCotizacionProveedorId, Integer proveedorAdjudicadoId, BigDecimal monto,
                                 String fundamento, Long autoridadUsuarioId, BigDecimal limiteAplicado);
}

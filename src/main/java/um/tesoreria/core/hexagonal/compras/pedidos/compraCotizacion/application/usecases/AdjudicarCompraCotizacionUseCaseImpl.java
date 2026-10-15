package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacionDecision;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.AdjudicarCompraCotizacionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraAdjudicacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdjudicarCompraCotizacionUseCaseImpl implements AdjudicarCompraCotizacionUseCase {

    private final CompraCotizacionRepository compraCotizacionRepository;
    private final CompraAdjudicacionRepository compraAdjudicacionRepository;

    @Override
    @Transactional
    public CompraAdjudicacion adjudicar(Long compraCotizacionId, CompraAdjudicacionDecision decision,
                                        Long compraCotizacionProveedorId, Integer proveedorAdjudicadoId,
                                        BigDecimal monto, String fundamento, Long autoridadUsuarioId,
                                        BigDecimal limiteAplicado) {
        CompraCotizacion proceso = compraCotizacionRepository.findById(compraCotizacionId)
                .orElseThrow(() -> new CompraCotizacionException(compraCotizacionId));
        proceso.resolverAdjudicacion(decision.estadoProceso());
        compraCotizacionRepository.updateHeader(proceso);
        CompraAdjudicacion adjudicacion = CompraAdjudicacion.builder()
                .compraCotizacionId(compraCotizacionId)
                .ejercicioId(proceso.getEjercicioId())
                .compraCotizacionProveedorId(compraCotizacionProveedorId)
                .proveedorAdjudicadoId(proveedorAdjudicadoId)
                .monto(monto)
                .decision(decision)
                .fundamento(fundamento)
                .autoridadUsuarioId(autoridadUsuarioId)
                .limiteAplicado(limiteAplicado)
                .fecha(LocalDateTime.now())
                .build();
        return compraAdjudicacionRepository.save(adjudicacion);
    }
}

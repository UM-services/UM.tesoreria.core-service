package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.ArmadoAsientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.LecturaRango;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LineaMovimientoContable;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.MovimientosContables;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in.ConsultarMovimientosUseCase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Movimientos Contables de {@code frmMovimientosContables} ({@code rptmovcon}): las mismas líneas que el Libro
 * Diario, sin agrupar, con el CUIT del proveedor. Mismos topes y errores que el Libro Diario.
 */
@Component
@RequiredArgsConstructor
public class ConsultarMovimientosUseCaseImpl implements ConsultarMovimientosUseCase {

    private final LecturaRango lecturaRango;
    private final ArmadoAsientos armadoAsientos;

    @Override
    @Transactional(readOnly = true)
    public MovimientosContables consultarMovimientos(OffsetDateTime desde, OffsetDateTime hasta) {
        List<LineaMovimientoContable> lineas = armadoAsientos.movimientos(lecturaRango.leer(desde, hasta));
        BigDecimal totalDebe = lineas.stream().map(LineaMovimientoContable::debe).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaber = lineas.stream().map(LineaMovimientoContable::haber).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new MovimientosContables(desde, hasta, lineas, totalDebe, totalHaber);
    }
}

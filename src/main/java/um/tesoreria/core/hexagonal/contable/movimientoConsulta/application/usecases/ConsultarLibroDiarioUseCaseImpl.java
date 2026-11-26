package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.ArmadoAsientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.LecturaRango;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LibroDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in.ConsultarLibroDiarioUseCase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Libro Diario: todas las líneas de {@code movcon} de un rango de días, agrupadas por asiento (fecha y orden).
 * <pre>
 * leer el rango con sus topes (LecturaRango, conteo y lectura en la misma transacción de solo lectura)
 *   -> nombres: cuentas, comprobantes y proveedores (unas pocas sentencias por pedido)
 *   -> agrupar por asiento y sumar
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class ConsultarLibroDiarioUseCaseImpl implements ConsultarLibroDiarioUseCase {

    private final LecturaRango lecturaRango;
    private final ArmadoAsientos armadoAsientos;

    @Override
    @Transactional(readOnly = true)
    public LibroDiario consultarLibroDiario(OffsetDateTime desde, OffsetDateTime hasta) {
        List<AsientoDiario> asientos = armadoAsientos.asientos(lecturaRango.leer(desde, hasta));
        BigDecimal totalDebe = asientos.stream().map(AsientoDiario::totalDebe).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaber = asientos.stream().map(AsientoDiario::totalHaber).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new LibroDiario(desde, hasta, asientos, totalDebe, totalHaber);
    }
}

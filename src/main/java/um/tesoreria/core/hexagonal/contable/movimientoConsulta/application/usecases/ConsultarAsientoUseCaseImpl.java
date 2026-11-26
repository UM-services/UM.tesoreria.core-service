package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException.Codigo;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.ArmadoAsientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.RangoContable;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in.ConsultarAsientoUseCase;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Un asiento (fecha y orden) leído de {@code movcon}, no de la tabla {@code asiento}: hay asientos con líneas y sin
 * fila allí. Toma el día de calendario de {@code fecha}, con cualquier hora y offset, igual que el Libro Diario.
 * <pre>
 * validar orden y fecha -> líneas del asiento (proyección, una consulta) -> sin líneas: 404
 *                       -> nombres (unas pocas sentencias, no una por línea) -> sumar
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class ConsultarAsientoUseCaseImpl implements ConsultarAsientoUseCase {

    private final CuentaMovimientoService cuentaMovimientoService;
    private final ArmadoAsientos armadoAsientos;

    @Override
    @Transactional(readOnly = true)
    public AsientoDiario consultarAsiento(OffsetDateTime fecha, Integer orden) {
        if (fecha == null) {
            throw new MovimientoConsultaException(Codigo.FECHAS_REQUERIDAS, "La fecha es obligatoria");
        }
        if (orden == null || orden <= 0) {
            throw new MovimientoConsultaException(Codigo.ORDEN_INVALIDO, "El orden debe ser mayor que 0: " + orden);
        }
        RangoContable dia = RangoContable.deFechas(fecha, fecha);
        if (dia.fueraDeRango()) {
            throw new MovimientoConsultaException(Codigo.FECHA_FUERA_DE_RANGO,
                    "La fecha debe estar entre " + RangoContable.MINIMO + " y " + RangoContable.MAXIMO);
        }
        List<LineaMovimiento> lineas = cuentaMovimientoService.findLineasByAsiento(dia.inicio(), dia.fin(), orden);
        if (lineas.isEmpty()) {
            throw new MovimientoConsultaException(Codigo.ASIENTO_INEXISTENTE,
                    "No hay líneas para el asiento " + dia.desde() + " / " + orden);
        }
        return armadoAsientos.asiento(lineas);
    }
}

package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException.Codigo;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.ArmadoAsientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.TopeMovimientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LibroDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.RangoContable;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in.ConsultarLibroDiarioUseCase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Libro Diario: todas las líneas de {@code movcon} de un rango de días, agrupadas por asiento (fecha y orden).
 * <pre>
 * validar fechas -> más días que el tope: 409 -> contar líneas -> más que el tope: 409
 *                -> leer como mucho tope + 1 líneas (proyección, una consulta) -> más que el tope: 409
 *                   (conteo y lectura en la misma transacción de solo lectura)
 *                -> nombres: cuentas, comprobantes y proveedores (una consulta por fuente, más las relaciones
 *                   de carga inmediata de cuenta y comprobante: unas pocas sentencias por pedido)
 *                -> agrupar por asiento y sumar
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class ConsultarLibroDiarioUseCaseImpl implements ConsultarLibroDiarioUseCase {

    private final CuentaMovimientoService cuentaMovimientoService;
    private final ArmadoAsientos armadoAsientos;
    private final TopeMovimientos topeMovimientos;

    @Override
    @Transactional(readOnly = true)
    public LibroDiario consultarLibroDiario(OffsetDateTime desde, OffsetDateTime hasta) {
        if (desde == null || hasta == null) {
            throw new MovimientoConsultaException(Codigo.FECHAS_REQUERIDAS, "Desde y hasta son obligatorios");
        }
        RangoContable rango = RangoContable.deFechas(desde, hasta);
        if (rango.fueraDeRango()) {
            throw new MovimientoConsultaException(Codigo.FECHA_FUERA_DE_RANGO,
                    "Las fechas deben estar entre " + RangoContable.MINIMO + " y " + RangoContable.MAXIMO);
        }
        if (rango.invertido()) {
            throw new MovimientoConsultaException(Codigo.DESDE_POSTERIOR,
                    "La fecha desde no puede ser posterior a hasta");
        }
        if (rango.dias() > topeMovimientos.maximoDias()) {
            throw new MovimientoConsultaException(Codigo.RANGO_DEMASIADO_LARGO,
                    "El rango tiene " + rango.dias() + " días y el máximo es " + topeMovimientos.maximoDias()
                            + ": acorte el rango");
        }
        int maximo = topeMovimientos.maximo();
        long cantidad = cuentaMovimientoService.countByFechaContable(rango.inicio(), rango.fin());
        if (cantidad > maximo) {
            throw demasiados(String.valueOf(cantidad), maximo);
        }

        // Otra transacción pudo agregar líneas después del conteo: se lee una más que el tope para detectarlo.
        List<LineaMovimiento> lineas = cuentaMovimientoService.findLineasByFechaContable(rango.inicio(), rango.fin(),
                maximo + 1);
        if (lineas.size() > maximo) {
            throw demasiados("más de " + maximo, maximo);
        }
        List<AsientoDiario> asientos = armadoAsientos.asientos(lineas);
        BigDecimal totalDebe = asientos.stream().map(AsientoDiario::totalDebe).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaber = asientos.stream().map(AsientoDiario::totalHaber).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new LibroDiario(desde, hasta, asientos, totalDebe, totalHaber);
    }

    private static MovimientoConsultaException demasiados(String cantidad, int maximo) {
        return new MovimientoConsultaException(Codigo.DEMASIADOS_MOVIMIENTOS,
                "El rango tiene " + cantidad + " movimientos y el máximo es " + maximo + ": acorte el rango");
    }
}

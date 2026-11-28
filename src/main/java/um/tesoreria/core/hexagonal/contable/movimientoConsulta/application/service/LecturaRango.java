package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException.Codigo;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.RangoContable;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Lectura de todas las líneas de {@code movcon} de un rango de días, con las validaciones y topes comunes a las
 * consultas por rango.
 * <pre>
 * validar fechas -> más días que el tope: 409 -> contar líneas -> más que el tope: 409
 *                -> leer como mucho tope + 1 líneas (proyección, una consulta) -> más que el tope: 409
 * </pre>
 * Quien la usa abre la transacción de solo lectura, así conteo y lectura ven lo mismo.
 */
@Component
@RequiredArgsConstructor
public class LecturaRango {

    private final CuentaMovimientoService cuentaMovimientoService;
    private final TopeMovimientos topeMovimientos;

    /** Líneas ordenadas por fecha, orden, ítem e id. */
    public List<LineaMovimiento> leer(OffsetDateTime desde, OffsetDateTime hasta) {
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
        return lineas;
    }

    private static MovimientoConsultaException demasiados(String cantidad, int maximo) {
        return new MovimientoConsultaException(Codigo.DEMASIADOS_MOVIMIENTOS,
                "El rango tiene " + cantidad + " movimientos y el máximo es " + maximo + ": acorte el rango");
    }
}

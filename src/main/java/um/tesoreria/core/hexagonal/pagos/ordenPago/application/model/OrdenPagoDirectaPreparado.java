package um.tesoreria.core.hexagonal.pagos.ordenPago.application.model;

import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaContexto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Orden de pago directa resuelta contra la base: comprobante, ejercicio, contexto del motor
 * de asientos y errores de validación detectados al armarla.
 */
public record OrdenPagoDirectaPreparado(Comprobante comprobante, Ejercicio ejercicio,
                                        OrdenPagoDirectaContexto contexto, BigDecimal totalGastos,
                                        BigDecimal totalValores, Integer prefijo, Long numeroComprobante,
                                        List<String> errores) {
}

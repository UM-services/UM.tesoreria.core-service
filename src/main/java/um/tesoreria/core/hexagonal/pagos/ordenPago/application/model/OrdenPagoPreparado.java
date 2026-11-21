package um.tesoreria.core.hexagonal.pagos.ordenPago.application.model;

import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoContexto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoFactura;

import java.math.BigDecimal;
import java.util.List;

/**
 * Orden de pago resuelta contra la base: datos del comprobante, facturas a cancelar,
 * contexto del motor de asientos y errores de validación detectados al armarlo.
 */
public record OrdenPagoPreparado(Proveedor proveedor, Comprobante comprobante, Ejercicio ejercicio,
                                 List<ProveedorMovimiento> facturas, List<OrdenPagoFactura> facturasSolicitud,
                                 OrdenPagoContexto contexto, BigDecimal totalFacturas, BigDecimal totalValores,
                                 Integer prefijo, Long numeroComprobante, List<String> errores) {
}

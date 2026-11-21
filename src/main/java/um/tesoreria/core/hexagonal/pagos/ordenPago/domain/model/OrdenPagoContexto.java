package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Todo lo que necesita el motor de asientos para una orden de pago a proveedor.
 */
public record OrdenPagoContexto(Integer proveedorId, Integer comprobanteId, Byte debita,
                                BigDecimal numeroCuentaProveedor, Integer prefijo, Long numeroComprobante,
                                OffsetDateTime fechaComprobante, Long proveedorMovimientoId,
                                List<ContextoFactura> facturas, List<ContextoEntrega> entregas,
                                List<ContextoValor> valores) {

    public String conceptoPago() {
        long numero = numeroComprobante == null ? 0L : numeroComprobante;
        int numeroPrefijo = prefijo == null ? 0 : prefijo;
        return String.format("Pago %04d-%08d", numeroPrefijo, numero);
    }
}

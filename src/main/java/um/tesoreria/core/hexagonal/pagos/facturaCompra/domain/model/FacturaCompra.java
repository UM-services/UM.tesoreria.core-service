package um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Alta de una factura de compra (legacy {@code frmProvCompras} / {@code clsCtlCompras.saveCompra}). */
public record FacturaCompra(
        Integer proveedorId,
        Integer comprobanteId,
        Integer prefijo,
        Long numeroComprobante,
        OffsetDateTime fechaComprobante,
        OffsetDateTime fechaVencimiento,
        BigDecimal neto,
        BigDecimal total,
        String concepto,
        Integer geograficaId,
        List<FacturaCompraArticulo> articulos) {
}

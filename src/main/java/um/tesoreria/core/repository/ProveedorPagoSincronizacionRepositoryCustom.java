package um.tesoreria.core.repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Consulta de comprobantes de pago cuya {@code fecha_pago} no coincide con la fecha de emisión
 * del valor que los canceló.
 *
 * <p>Reemplaza el {@code UPDATE ... JOIN} nativo que corría en cada generación del reporte de
 * facturas pendientes: ahora el cambio pasa por JPA (entidad {@code ProveedorPago}) y por eso
 * dispara {@code PostUpdate} y queda auditado. Se implementa con Criteria API (sin
 * {@code @Query}).</p>
 */
public interface ProveedorPagoSincronizacionRepositoryCustom {

    /**
     * @return una fila por comprobante desincronizado, con la fecha de emisión del primer valor
     *         de su orden de pago (menor {@code opv_orden})
     */
    List<ProveedorPagoDesincronizado> findDesincronizados();

    record ProveedorPagoDesincronizado(Long proveedorPagoId, OffsetDateTime fechaEmision) {
    }
}

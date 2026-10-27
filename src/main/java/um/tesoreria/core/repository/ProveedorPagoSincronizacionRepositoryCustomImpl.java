package um.tesoreria.core.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.kotlin.model.ProveedorPago;
import um.tesoreria.core.kotlin.model.ProveedorValor;
import um.tesoreria.core.kotlin.model.ValorMovimiento;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Criteria de la sincronización de {@code fecha_pago}.
 *
 * <p>Reproduce el join del UPDATE nativo ({@code detallecomprobantes → detallevalores →
 * valores}) sin escribir: devuelve sólo los comprobantes cuya fecha difiere. Si una orden de
 * pago tiene varios valores, gana el de menor {@code opv_orden} (determinista; el UPDATE
 * anterior elegía uno arbitrario).</p>
 */
@Repository
public class ProveedorPagoSincronizacionRepositoryCustomImpl
        implements ProveedorPagoSincronizacionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorPagoDesincronizado> findDesincronizados() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();

        Root<ProveedorPago> pago = query.from(ProveedorPago.class);
        Root<ProveedorValor> detalleValor = query.from(ProveedorValor.class);
        Root<ValorMovimiento> valor = query.from(ValorMovimiento.class);

        query.multiselect(pago.get("proveedorPagoId"), pago.get("fechaPago"), valor.get("fechaEmision"))
                .where(
                        builder.isNotNull(pago.get("proveedorMovimientoIdPago")),
                        builder.equal(detalleValor.get("proveedorMovimientoId"),
                                pago.get("proveedorMovimientoIdPago")),
                        builder.equal(valor.get("valorMovimientoId"),
                                detalleValor.get("valorMovimientoId")),
                        builder.isNotNull(valor.get("fechaEmision")),
                        builder.or(
                                builder.isNull(pago.get("fechaPago")),
                                builder.notEqual(pago.get("fechaPago"), valor.get("fechaEmision"))))
                .orderBy(builder.asc(pago.get("proveedorPagoId")),
                        builder.asc(detalleValor.get("orden")));

        List<Tuple> rows = entityManager.createQuery(query).getResultList();

        Map<Long, ProveedorPagoDesincronizado> porComprobante = new LinkedHashMap<>();
        for (Tuple row : rows) {
            Long proveedorPagoId = row.get(0, Long.class);
            OffsetDateTime fechaPago = row.get(1, OffsetDateTime.class);
            OffsetDateTime fechaEmision = row.get(2, OffsetDateTime.class);

            if (proveedorPagoId == null || fechaEmision == null || Objects.equals(fechaPago, fechaEmision)) {
                continue;
            }
            porComprobante.putIfAbsent(proveedorPagoId,
                    new ProveedorPagoDesincronizado(proveedorPagoId, fechaEmision));
        }
        return new ArrayList<>(porComprobante.values());
    }
}

package um.tesoreria.core.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.ProveedorPago;
import um.tesoreria.core.repository.ProveedorPagoRepository;
import um.tesoreria.core.repository.ProveedorPagoSincronizacionRepositoryCustom;
import um.tesoreria.core.repository.ProveedorPagoSincronizacionRepositoryCustom.ProveedorPagoDesincronizado;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Normaliza {@code movprov_detallecomprobantes.fecha_pago} a partir de la fecha de emisión del
 * valor que canceló el comprobante.
 *
 * <p>Antes era un {@code UPDATE ... JOIN} nativo dentro de la consulta del reporte de facturas
 * pendientes: no disparaba los listeners de Hibernate y quedaba fuera de la auditoría. Ahora se
 * resuelven los comprobantes desincronizados con Criteria y se guardan como entidades, así cada
 * fila modificada emite su evento {@code CHANGE} con {@code beforeData}/{@code afterData}.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProveedorPagoSincronizacionService {

    private final ProveedorPagoSincronizacionRepositoryCustom sincronizacionRepository;
    private final ProveedorPagoRepository proveedorPagoRepository;

    /**
     * @return cantidad de comprobantes cuyo {@code fecha_pago} se corrigió
     */
    @Transactional
    public int sincronizarFechaPago() {
        List<ProveedorPagoDesincronizado> desincronizados = sincronizacionRepository.findDesincronizados();
        if (desincronizados.isEmpty()) {
            return 0;
        }

        Map<Long, OffsetDateTime> fechas = new LinkedHashMap<>();
        desincronizados.forEach(item -> fechas.putIfAbsent(item.proveedorPagoId(), item.fechaEmision()));

        List<ProveedorPago> pagos = proveedorPagoRepository.findAllById(fechas.keySet());
        int actualizados = 0;
        for (ProveedorPago pago : pagos) {
            OffsetDateTime fechaEmision = fechas.get(pago.getProveedorPagoId());
            if (fechaEmision == null || fechaEmision.equals(pago.getFechaPago())) {
                continue;
            }
            pago.setFechaPago(fechaEmision);
            actualizados++;
        }

        if (actualizados > 0) {
            proveedorPagoRepository.saveAll(pagos);
            log.info("Sincronización de fecha_pago: {} comprobante(s) actualizados", actualizados);
        }
        return actualizados;
    }
}

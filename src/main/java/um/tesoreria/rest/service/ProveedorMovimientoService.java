/**
 *
 */
package um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import um.tesoreria.rest.exception.ProveedorMovimientoException;
import um.tesoreria.rest.kotlin.model.Comprobante;
import um.tesoreria.rest.repository.IProveedorMovimientoRepository;
import um.tesoreria.rest.kotlin.model.ProveedorArticulo;
import um.tesoreria.rest.kotlin.model.ProveedorMovimiento;

/**
 * @author daniel
 *
 */
@Service
public class ProveedorMovimientoService {

    private final Integer TT_PAGOS = 4;

    @Autowired
    private IProveedorMovimientoRepository repository;

    @Autowired
    private ComprobanteService comprobanteService;

    @Autowired
    private ProveedorArticuloService proveedorArticuloService;

    public List<ProveedorMovimiento> findAllByComprobanteIdAndFechaComprobanteBetween(Integer comprobanteId,
                                                                                      OffsetDateTime fechaInicio, OffsetDateTime fechaFinal) {
        return repository.findAllByComprobanteIdAndFechaComprobanteBetween(comprobanteId, fechaInicio, fechaFinal);
    }

    public List<ProveedorMovimiento> findAllByProveedorMovimientoIdIn(List<Long> proveedorMovimientoIds) {
        return repository.findAllByProveedorMovimientoIdIn(proveedorMovimientoIds, null);
    }

    public List<ProveedorMovimiento> findAllEliminables(Integer ejercicioId) {
        List<ProveedorMovimiento> anuladas = repository.findAllByComprobanteIdAndPrefijoAndFechaAnulacionNotNull(6,
                ejercicioId);
        List<ProveedorMovimiento> sueldosPendientes = repository
                .findAllByComprobanteIdAndPrefijoAndNetoSinDescuentoAndNombreBeneficiarioStartingWithAndConceptoStartingWith(
                        6, ejercicioId, BigDecimal.ZERO, "Personal", "Sueldos");
        List<ProveedorMovimiento> proveedorMovimientos = Stream.concat(anuladas.stream(), sueldosPendientes.stream())
                .collect(Collectors.toList());
        return proveedorMovimientos;
    }

    public List<ProveedorMovimiento> findAllByProveedorId(Integer proveedorId, Integer geograficaId) {
        if (geograficaId == 0) {
            return repository.findAllByProveedorId(proveedorId);
        }
        return repository.findAllByProveedorIdAndGeograficaId(proveedorId, geograficaId);
    }

    public List<ProveedorMovimiento> findAllAsignables(Integer proveedorId, OffsetDateTime desde,
                                                       OffsetDateTime hasta, Integer geograficaId, Boolean todos) throws JsonProcessingException {
        List<Comprobante> comprobantes = comprobanteService.findAllByTipoTransaccionId(3);
        List<Integer> comprobanteIds = comprobantes.stream().map(c -> c.getComprobanteId())
                .collect(Collectors.toList());
        List<Long> proveedorMovimientoIds = null;
        if (geograficaId == 0) {
            List<ProveedorMovimiento> proveedorMovimientos = repository
                    .findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndFechaAnulacionIsNull(
                            proveedorId, desde, hasta, comprobanteIds);
            proveedorMovimientoIds = proveedorMovimientos.stream().map(m -> m.getProveedorMovimientoId())
                    .collect(Collectors.toList());
        } else {
            List<ProveedorMovimiento> proveedorMovimientos = repository
                    .findAllByProveedorIdAndFechaComprobanteBetweenAndComprobanteIdInAndGeograficaIdAndFechaAnulacionIsNull(
                            proveedorId, desde, hasta, comprobanteIds, geograficaId);
            proveedorMovimientoIds = proveedorMovimientos.stream().map(m -> m.getProveedorMovimientoId())
                    .collect(Collectors.toList());
        }
        List<ProveedorArticulo> proveedorArticulos = proveedorArticuloService
                .findAllByProveedorMovimientoIds(proveedorMovimientoIds, true);
        if (todos) {
            proveedorArticulos = proveedorArticulos.stream()
                    .filter(p -> p.getAsignado().compareTo(p.getPrecioFinal()) != 0).collect(Collectors.toList());
        }
        proveedorMovimientoIds = proveedorArticulos.stream().map(p -> p.getProveedorMovimientoId())
                .collect(Collectors.toList());
        List<ProveedorMovimiento> proveedorMovimientos = repository
                .findAllByProveedorMovimientoIdIn(proveedorMovimientoIds, Sort.by("fechaComprobante").descending()
                        .and(Sort.by("prefijo").ascending()).and(Sort.by("numeroComprobante").ascending()));
        return proveedorMovimientos;
    }

    public ProveedorMovimiento findByProveedorMovimientoId(Long proveedorMovimientoId) {
        ProveedorMovimiento proveedorMovimiento = repository.findByProveedorMovimientoId(proveedorMovimientoId)
                .orElseThrow(() -> new ProveedorMovimientoException(proveedorMovimientoId));
        return proveedorMovimiento;
    }

    public ProveedorMovimiento findByOrdenPago(Integer prefijo, Long numeroComprobante) {
        List<Integer> comprobanteIds = comprobanteService.findAllByOrdenPago().stream().map(comprobante -> comprobante.getComprobanteId()).collect(Collectors.toList());
        return repository.findByPrefijoAndNumeroComprobanteAndComprobanteIdIn(prefijo, numeroComprobante, comprobanteIds).orElseThrow(() -> new ProveedorMovimientoException(prefijo, numeroComprobante));
    }

    public ProveedorMovimiento findLastOrdenPago(Integer prefijo) {
        List<Integer> comprobanteIds = comprobanteService.findAllByOrdenPagoAndTipoTransaccionId(TT_PAGOS).stream().map(comprobante -> comprobante.getComprobanteId()).collect(Collectors.toList());
        return repository.findTopByPrefijoAndComprobanteIdInOrderByNumeroComprobanteDesc(prefijo, comprobanteIds).orElseThrow(() -> new ProveedorMovimientoException(prefijo));
    }

    public ProveedorMovimiento update(ProveedorMovimiento newProveedorMovimiento, Long proveedorMovimientoId) {
        return repository.findByProveedorMovimientoId(proveedorMovimientoId).map(proveedorMovimiento -> {
            proveedorMovimiento = new ProveedorMovimiento(proveedorMovimientoId, newProveedorMovimiento.getProveedorId(), newProveedorMovimiento.getNombreBeneficiario(), newProveedorMovimiento.getComprobanteId(), newProveedorMovimiento.getFechaComprobante(), newProveedorMovimiento.getFechaVencimiento(), newProveedorMovimiento.getPrefijo(), newProveedorMovimiento.getNumeroComprobante(), newProveedorMovimiento.getNetoSinDescuento(), newProveedorMovimiento.getDescuento(), newProveedorMovimiento.getNeto(), newProveedorMovimiento.getImporte(), newProveedorMovimiento.getCancelado(), newProveedorMovimiento.getFechaContable(), newProveedorMovimiento.getOrdenContable(), newProveedorMovimiento.getConcepto(), newProveedorMovimiento.getFechaAnulacion(), newProveedorMovimiento.getConCargo(), newProveedorMovimiento.getSolicitaFactura(), newProveedorMovimiento.getGeograficaId(), null, null, null, null);
            proveedorMovimiento = repository.save(proveedorMovimiento);
            return proveedorMovimiento;
        }).orElseThrow(() -> new ProveedorMovimientoException(proveedorMovimientoId));
    }

    @Transactional
    public void deleteByProveedorMovimientoId(Long proveedorMovimientoId) {
        repository.deleteByProveedorMovimientoId(proveedorMovimientoId);
    }

    public ProveedorMovimiento add(ProveedorMovimiento proveedorMovimiento) {
        return repository.save(proveedorMovimiento);
    }
}

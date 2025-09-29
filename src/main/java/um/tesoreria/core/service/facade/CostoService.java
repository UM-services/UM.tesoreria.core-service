/**
 *
 */
package um.tesoreria.core.service.facade;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.*;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.kotlin.model.internal.AsientoInternal;
import um.tesoreria.core.model.dto.AsignacionCostoDto;
import um.tesoreria.core.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;

@Service
@Slf4j
@RequiredArgsConstructor
public class CostoService {

    private final EntityManager entityManager;
    private final TrackService trackService;

    private final AsientoService asientoService;
    private final ContabilidadService contabilidadService;
    private final EntregaService entregaService;
    private final EntregaDetalleService entregaDetalleService;
    private final CuentaMovimientoService cuentaMovimientoService;
    private final ProveedorArticuloService proveedorArticuloService;
    private final ProveedorMovimientoService proveedorMovimientoService;
    private final ProveedorArticuloTrackService proveedorArticuloTrackService;

    @Transactional
    public Boolean addAsignacion(AsignacionCostoDto asignacionCostoDto) {
        log.debug("Processing CostoService.addAsignacion");
        log.debug("AsignacionCostoDto -> {}", asignacionCostoDto.jsonify());
        try {
            Track track = trackService.add(new Track.Builder()
                    .descripcion("Asignación Costo")
                    .build());
            log.debug("Track -> {}", track.jsonify());

            int item = 0;
            AsientoInternal asientoInternal = contabilidadService.nextAsiento(asignacionCostoDto.getProveedorMovimiento().getFechaComprobante(), null);
            log.debug("AsientoInternal -> {}", asientoInternal.jsonify());
            Entrega entrega = new Entrega.Builder()
                    .fecha(asignacionCostoDto.getProveedorMovimiento().getFechaComprobante())
                    .ubicacionId(asignacionCostoDto.getUbicacionArticulo().getUbicacionId())
                    .fechaContable(asientoInternal.getFechaContable())
                    .ordenContable(asientoInternal.getOrdenContable())
                    .tipo("asignacion")
                    .trackId(track.getTrackId())
                    .build();
            log.debug("Entrega -> {}", entrega.jsonify());

            // Asiento
            log.debug("Eliminando asiento previo si existiera -> {}-{}", asientoInternal.getFechaContable(), asientoInternal.getOrdenContable());
            try {
                contabilidadService.deleteAsiento(asientoInternal.getFechaContable(), asientoInternal.getOrdenContable());
                entityManager.flush();
            } catch (EjercicioBloqueadoException e) {
                log.debug("Error Ejercicio : {}", e.getMessage());
                return false;
            }
            var asiento = new Asiento.Builder()
                    .fecha(asientoInternal.getFechaContable())
                    .orden(asientoInternal.getOrdenContable())
                    .vinculo("Asignación Costos")
                    .trackId(track.getTrackId())
                    .build();
            log.debug("Agregando asiento");
            asiento = asientoService.add(asiento);
            log.debug("Asiento -> {}", asiento.jsonify());

            String concepto = MessageFormat.format("{0} - {1}-{2} - Asignación de Costos", asignacionCostoDto.getComprobante().getDescripcion(), String.format("%04d", asignacionCostoDto.getProveedorMovimiento().getPrefijo()), String.format("%08d", asignacionCostoDto.getProveedorMovimiento().getNumeroComprobante()));
            item += 1;
            CuentaMovimiento cuentaMovimiento = new CuentaMovimiento.Builder()
                    .fechaContable(entrega.getFechaContable())
                    .ordenContable(entrega.getOrdenContable())
                    .item(item)
                    .numeroCuenta(asignacionCostoDto.getArticulo().getNumeroCuenta())
                    .debita(asignacionCostoDto.getComprobante().getDebita())
                    .comprobanteId(asignacionCostoDto.getComprobante().getComprobanteId())
                    .concepto(concepto)
                    .importe(BigDecimal.valueOf(Math.abs(asignacionCostoDto.getImporte().doubleValue())))
                    .proveedorId(asignacionCostoDto.getProveedorMovimiento().getProveedorId())
                    .proveedorMovimientoId(asignacionCostoDto.getProveedorMovimiento().getProveedorMovimientoId())
                    .trackId(track.getTrackId())
                    .build();
            cuentaMovimiento = cuentaMovimientoService.add(cuentaMovimiento);
            log.debug("CuentaMovimiento -> {}", cuentaMovimiento.jsonify());
            item += 1;
            cuentaMovimiento = new CuentaMovimiento.Builder()
                    .fechaContable(entrega.getFechaContable())
                    .ordenContable(entrega.getOrdenContable())
                    .item(item)
                    .numeroCuenta(asignacionCostoDto.getUbicacionArticulo().getNumeroCuenta())
                    .debita((byte) (1 - asignacionCostoDto.getComprobante().getDebita()))
                    .comprobanteId(asignacionCostoDto.getComprobante().getComprobanteId())
                    .concepto(concepto)
                    .importe(BigDecimal.valueOf(Math.abs(asignacionCostoDto.getImporte().doubleValue())))
                    .proveedorId(asignacionCostoDto.getProveedorMovimiento().getProveedorId())
                    .proveedorMovimientoId(asignacionCostoDto.getProveedorMovimiento().getProveedorMovimientoId())
                    .trackId(track.getTrackId())
                    .build();
            cuentaMovimiento = cuentaMovimientoService.add(cuentaMovimiento);
            log.debug("CuentaMovimiento -> {}", cuentaMovimiento.jsonify());

            entrega = entregaService.add(entrega);
            log.debug("Entrega -> {}", entrega.jsonify());
            EntregaDetalle entregaDetalle = new EntregaDetalle.Builder()
                    .entregaId(entrega.getEntregaId())
                    .articuloId(asignacionCostoDto.getProveedorArticulo().getArticuloId())
                    .proveedorMovimientoId(asignacionCostoDto.getProveedorArticulo().getProveedorMovimientoId())
                    .orden(asignacionCostoDto.getProveedorArticulo().getOrden())
                    .concepto(asignacionCostoDto.getProveedorArticulo().getConcepto())
                    .cantidad(asignacionCostoDto.getImporte())
                    .trackId(track.getTrackId())
                    .build();
            entregaDetalle = entregaDetalleService.add(entregaDetalle);
            log.debug("EntregaDetalle -> {}", entregaDetalle.jsonify());

            ProveedorArticulo proveedorArticulo = proveedorArticuloService.findByProveedorArticuloId(asignacionCostoDto.getProveedorArticulo().getProveedorArticuloId());
            proveedorArticulo.setAsignado(proveedorArticulo.getAsignado().add(asignacionCostoDto.getImporte()).setScale(2, RoundingMode.HALF_UP));
            proveedorArticulo = proveedorArticuloService.update(proveedorArticulo, proveedorArticulo.getProveedorArticuloId());
            log.debug("ProveedorArticulo -> {}", proveedorArticulo.jsonify());

            ProveedorArticuloTrack proveedorArticuloTrack = new ProveedorArticuloTrack.Builder()
                    .proveedorMovimientoId(proveedorArticulo.getProveedorMovimientoId())
                    .proveedorArticuloId(proveedorArticulo.getProveedorArticuloId())
                    .trackId(track.getTrackId())
                    .importe(asignacionCostoDto.getImporte())
                    .build();
            proveedorArticuloTrack = proveedorArticuloTrackService.add(proveedorArticuloTrack);
            log.debug("ProveedorArticuloTrack -> {}", proveedorArticuloTrack.jsonify());
        } catch (CuentaMovimientoException e) {
            log.debug("Error CuentaMovimiento - {}", e.getMessage());
            return false;
        } catch (EntregaException e) {
            log.debug("Error Entrega - {}", e.getMessage());
            return false;
        } catch (EntregaDetalleException e) {
            log.debug("Error EntregaDetalle - {}", e.getMessage());
            return false;
        } catch (ProveedorArticuloException e) {
            log.debug("Error ProveedorArticulo - {}", e.getMessage());
            return false;
        }
        return true;
    }

    @Transactional
    public Boolean deleteDesignacion(Long entregaId) {

        Long trackId = null;
        try {
            Entrega entrega = new Entrega();
            for (EntregaDetalle entregaDetalle : entregaDetalleService.findAllByEntregaId(entregaId)) {
                log.debug("EntregaDetalle in deleteDesignacion -> {}", entregaDetalle.jsonify());
                entrega = entregaDetalle.getEntrega();
                trackId = entrega.getTrackId();
                log.debug("Entrega in deleteDesignacion -> {}", entrega.jsonify());
                if (entregaDetalle.getProveedorArticulo() != null) {
                    var proveedorArticulo = entregaDetalle.getProveedorArticulo();
                    log.debug("ProveedorArticulo before in deleteDesignacion -> {}", proveedorArticulo.jsonify());
                    proveedorArticulo.setAsignado(proveedorArticulo.getAsignado().subtract(entregaDetalle.getCantidad()).setScale(2, RoundingMode.HALF_UP));
                    if (proveedorArticulo.getAsignado().compareTo(BigDecimal.ZERO) < 0) {
                        proveedorArticulo.setAsignado(BigDecimal.ZERO);
                    }
                    proveedorArticulo = proveedorArticuloService.update(proveedorArticulo, proveedorArticulo.getProveedorArticuloId());
                    log.debug("ProveedorArticulo after in deleteDesignacion -> {}", proveedorArticulo.jsonify());
                    proveedorArticuloTrackService.deleteAllByProveedorArticuloIdAndTrackId(proveedorArticulo.getProveedorArticuloId(), trackId);
                    log.debug("ProveedorArticuloTrack eliminado");
                }
                entregaDetalleService.deleteByEntregaDetalleId(entregaDetalle.getEntregaDetalleId());
                log.debug("EntregaDetalle in deleteDesignacion deleted");
            }
            entregaService.deleteByEntregaId(entregaId);
            log.debug("Entrega in deleteDesignacion deleted");
            contabilidadService.deleteAsiento(entrega.getFechaContable(), entrega.getOrdenContable());
            log.debug("Asiento in deleteDesignacion deleted");
            trackService.deleteByTrackId(trackId);
        } catch (EjercicioBloqueadoException e) {
            log.debug("Error Ejercicio : {}", e.getMessage());
            return false;
        } catch (EntregaDetalleException e) {
            log.debug("Error EntregaDetalle : {}", e.getMessage());
            return false;
        } catch (ProveedorArticuloException e) {
            log.debug("Error ProveedorArticulo : {}", e.getMessage());
            return false;
        } catch (EntregaException e) {
            log.debug("Error Entrega : {}", e.getMessage());
            return false;
        }
        return true;
    }

    @Transactional
    public Boolean depurarGastosProveedor(Integer proveedorId, Integer geograficaId) {

        try {
            for (ProveedorMovimiento proveedorMovimiento : proveedorMovimientoService.findAllByProveedorId(proveedorId, geograficaId)) {
                for (ProveedorArticulo proveedorArticulo : proveedorArticuloService.findAllByProveedorMovimientoId(proveedorMovimiento.getProveedorMovimientoId(), false)) {
                    if (proveedorArticulo.getArticulo().getDirecto() == 0 && proveedorArticulo.getEntregado().compareTo(BigDecimal.ZERO) > 0) {
                        proveedorArticulo.setEntregado(BigDecimal.ZERO);
                        proveedorArticuloService.update(proveedorArticulo, proveedorArticulo.getProveedorArticuloId());
                    }
                }
            }
        } catch (ProveedorMovimientoException e) {
            log.debug("Error ProveedorMovimiento - {}", e.getMessage());
            return false;
        } catch (ProveedorArticuloException e) {
            log.debug("Error ProveedorArtiulo - {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.debug("Error Exception - {}", e.getMessage());
            return false;
        }
        return true;
    }

    @Transactional
    public void recalcularAsignado(Long proveedorArticuloId) {
        ProveedorArticulo proveedorArticulo = proveedorArticuloService.findByProveedorArticuloId(proveedorArticuloId);
        BigDecimal asignado = BigDecimal.ZERO;
        for (EntregaDetalle entregaDetalle : entregaDetalleService.findAllByProveedorMovimientoIdAndOrden(proveedorArticulo.getProveedorMovimientoId(), proveedorArticulo.getOrden())) {
            asignado = asignado.add(entregaDetalle.getCantidad()).setScale(2, RoundingMode.HALF_UP);
        }
        proveedorArticulo.setAsignado(asignado);
        proveedorArticulo = proveedorArticuloService.update(proveedorArticulo, proveedorArticuloId);
    }

}

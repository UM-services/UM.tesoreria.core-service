/**
 *
 */
package um.tesoreria.core.service.facade;

import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.kotlin.model.internal.AsientoInternal;
import um.tesoreria.core.model.dto.AsignacionCostoDto;
import um.tesoreria.core.exception.EntregaDetalleException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.*;
import um.tesoreria.core.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;

/**
 * @author daniel
 */
@Service
@Slf4j
public class CostoService {

    private final TrackService trackService;

    private final AsientoService asientoService;

    private final ContabilidadService contabilidadService;

    private final EntregaService entregaService;

    private final EntregaDetalleService entregaDetalleService;

    private final CuentaMovimientoService cuentaMovimientoService;

    private final ProveedorArticuloService proveedorArticuloService;

    private final ProveedorMovimientoService proveedorMovimientoService;

    private final ProveedorArticuloTrackService proveedorArticuloTrackService;

    public CostoService(TrackService trackService, AsientoService asientoService, ContabilidadService contabilidadService, EntregaService entregaService, EntregaDetalleService entregaDetalleService, CuentaMovimientoService cuentaMovimientoService, ProveedorArticuloService proveedorArticuloService, ProveedorMovimientoService proveedorMovimientoService, ProveedorArticuloTrackService proveedorArticuloTrackService) {
        this.trackService = trackService;
        this.asientoService = asientoService;
        this.contabilidadService = contabilidadService;
        this.entregaService = entregaService;
        this.entregaDetalleService = entregaDetalleService;
        this.cuentaMovimientoService = cuentaMovimientoService;
        this.proveedorArticuloService = proveedorArticuloService;
        this.proveedorMovimientoService = proveedorMovimientoService;
        this.proveedorArticuloTrackService = proveedorArticuloTrackService;
    }

    @Transactional
    public Boolean addAsignacion(AsignacionCostoDto asignacionCostoDto) {

        try {
            log.debug("AsignacionCosto -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(asignacionCostoDto));
        } catch (JsonProcessingException e) {
            log.debug("AsignacionCosto -> error: {}", e.getMessage());
        }

        try {

            Track track = trackService.add(new Track.Builder()
                    .descripcion("Asignación Costo")
                    .build());
            try {
                log.debug("Track -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(track));
            } catch (JsonProcessingException e) {
                log.debug("Track -> error: {}", e.getMessage());
            }

            int item = 0;
            AsientoInternal asientoInternal = contabilidadService.nextAsiento(asignacionCostoDto.getProveedorMovimiento().getFechaComprobante(), null);
            try {
                log.debug("Asiento Internal -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(asientoInternal));
            } catch (JsonProcessingException e) {
                log.debug("Asiento Internal -> error: {}", e.getMessage());
            }
            Entrega entrega = new Entrega.Builder()
                    .fecha(asignacionCostoDto.getProveedorMovimiento().getFechaComprobante())
                    .ubicacionId(asignacionCostoDto.getUbicacionArticulo().getUbicacionId())
                    .fechaContable(asientoInternal.getFechaContable())
                    .ordenContable(asientoInternal.getOrdenContable())
                    .tipo("asignacion")
                    .trackId(track.getTrackId())
                    .build();
            try {
                log.debug("Entrega -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(entrega));
            } catch (JsonProcessingException e) {
                log.debug("Entrega -> error: {}", e.getMessage());
            }

            // Asiento
            Asiento asiento = new Asiento.Builder()
                    .fecha(asientoInternal.getFechaContable())
                    .orden(asientoInternal.getOrdenContable())
                    .vinculo("Asignación Costos")
                    .trackId(track.getTrackId())
                    .build();
            asiento = asientoService.add(asiento);
            try {
                log.debug("Asiento -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(asiento));
            } catch (JsonProcessingException e) {
                log.debug("Asistencia -> error: {}", e.getMessage());
            }

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
            try {
                log.debug("CuentaMovimiento 1ra Imputación -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("CuentaMovimiento 1ra Imputación -> error: {}", e.getMessage());
            }
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
            try {
                log.debug("CuentaMovimiento 2da Imputación -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("CuentaMovimiento 2da Imputación -> error: {}", e.getMessage());
            }

            entrega = entregaService.add(entrega);
            try {
                log.debug("Entrega -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(entrega));
            } catch (JsonProcessingException e) {
                log.debug("Entrega -> error: {}", e.getMessage());
            }
            EntregaDetalle entregaDetalle = new EntregaDetalle.Builder()
                    .entregaId(entrega.getEntregaId())
                    .articuloId(asignacionCostoDto.getProveedorArticulo().getArticuloId())
                    .proveedorMovimientoId(asignacionCostoDto.getProveedorArticulo().getProveedorMovimientoId())
                    .orden(asignacionCostoDto.getProveedorArticulo().getOrden())
                    .concepto(asignacionCostoDto.getProveedorArticulo().getConcepto())
                    .cantidad(asignacionCostoDto.getImporte())
                    .trackId(track.getTrackId())
                    .build();
            try {
                log.debug("EntregaDetalle pre save -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(entregaDetalle));
            } catch (JsonProcessingException e) {
                log.debug("EntregaDetalle pre save -> error: {}", e.getMessage());
            }
            entregaDetalle = entregaDetalleService.add(entregaDetalle);
            try {
                log.debug("EntregaDetalle post save -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(entregaDetalle));
            } catch (JsonProcessingException e) {
                log.debug("EntregaDetalle post save -> error: {}", e.getMessage());
            }

            ProveedorArticulo proveedorArticulo = proveedorArticuloService.findByProveedorArticuloId(asignacionCostoDto.getProveedorArticulo().getProveedorArticuloId());
            try {
                log.debug("ProveedorArticulo before asignado -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulo));
            } catch (JsonProcessingException e) {
                log.debug("ProveedorArticulo before asignado -> error: {}", e.getMessage());
            }
            proveedorArticulo.setAsignado(proveedorArticulo.getAsignado().add(asignacionCostoDto.getImporte()).setScale(2, RoundingMode.HALF_UP));
            proveedorArticulo = proveedorArticuloService.update(proveedorArticulo, proveedorArticulo.getProveedorArticuloId());
            try {
                log.debug("ProveedorArticulo after asignado -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulo));
            } catch (JsonProcessingException e) {
                log.debug("ProveedorArticulo after asignado -> error: {}", e.getMessage());
            }

            ProveedorArticuloTrack proveedorArticuloTrack = new ProveedorArticuloTrack.Builder()
                    .proveedorMovimientoId(proveedorArticulo.getProveedorMovimientoId())
                    .proveedorArticuloId(proveedorArticulo.getProveedorArticuloId())
                    .trackId(track.getTrackId())
                    .importe(asignacionCostoDto.getImporte())
                    .build();
            try {
                log.debug("ProveedorArticuloTrack before -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticuloTrack));
            } catch (JsonProcessingException e) {
                log.debug("ProveedorArticuloTrack before -> error: {}", e.getMessage());
            }
            proveedorArticuloTrack = proveedorArticuloTrackService.add(proveedorArticuloTrack);
            try {
                log.debug("ProveedorArticuloTrack after -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticuloTrack));
            } catch (JsonProcessingException e) {
                log.debug("ProveedorArticuloTrack after -> error: {}", e.getMessage());
            }
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
                try {
                    log.debug("EntregaDetalle in deleteDesignacion -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(entregaDetalle));
                } catch (JsonProcessingException e) {
                    log.debug("Cannot show EntregaDetalle in deleteDesignacion");
                }
                entrega = entregaDetalle.getEntrega();
                trackId = entrega.getTrackId();
                try {
                    log.debug("Entrega in deleteDesignacion -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(entrega));
                } catch (JsonProcessingException e) {
                    log.debug("Cannot show Entrega in deleteDesignacion");
                }
                if (entregaDetalle.getProveedorArticulo() != null) {
                    ProveedorArticulo proveedorArticulo = entregaDetalle.getProveedorArticulo();
                    try {
                        log.debug("ProveedorArticulo before in deleteDesignacion -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulo));
                    } catch (JsonProcessingException e) {
                        log.debug("Cannot show ProveedorArticulo before in deleteDesignacion");
                    }
                    proveedorArticulo.setAsignado(proveedorArticulo.getAsignado().subtract(entregaDetalle.getCantidad()).setScale(2, RoundingMode.HALF_UP));
                    if (proveedorArticulo.getAsignado().compareTo(BigDecimal.ZERO) < 0) {
                        proveedorArticulo.setAsignado(BigDecimal.ZERO);
                    }
                    proveedorArticulo = proveedorArticuloService.update(proveedorArticulo, proveedorArticulo.getProveedorArticuloId());
                    try {
                        log.debug("ProveedorArticulo after in deleteDesignacion -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulo));
                    } catch (JsonProcessingException e) {
                        log.debug("Cannot show ProveedorArticulo after in deleteDesignacion");
                    }
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

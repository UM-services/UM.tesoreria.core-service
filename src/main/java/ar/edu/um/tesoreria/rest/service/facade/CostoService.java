/**
 *
 */
package ar.edu.um.tesoreria.rest.service.facade;

import ar.edu.um.tesoreria.rest.exception.*;
import ar.edu.um.tesoreria.rest.model.Asiento;
import ar.edu.um.tesoreria.rest.model.CuentaMovimiento;
import ar.edu.um.tesoreria.rest.model.ProveedorArticulo;
import ar.edu.um.tesoreria.rest.model.ProveedorMovimiento;
import ar.edu.um.tesoreria.rest.model.dto.AsignacionCosto;
import ar.edu.um.tesoreria.rest.model.kotlin.Entrega;
import ar.edu.um.tesoreria.rest.model.kotlin.EntregaDetalle;
import ar.edu.um.tesoreria.rest.model.kotlin.ProveedorArticuloTrack;
import ar.edu.um.tesoreria.rest.model.kotlin.Track;
import ar.edu.um.tesoreria.rest.model.kotlin.internal.AsientoInternal;
import ar.edu.um.tesoreria.rest.repository.EntregaDetalleException;
import ar.edu.um.tesoreria.rest.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;

/**
 * @author daniel
 */
@Service
@Slf4j
public class CostoService {

    @Resource
    private TrackService trackService;

    @Resource
    private AsientoService asientoService;

    @Resource
    private ContableService contableService;

    @Resource
    private EntregaService entregaService;

    @Resource
    private EntregaDetalleService entregaDetalleService;

    @Resource
    private CuentaMovimientoService cuentaMovimientoService;

    @Resource
    private ProveedorArticuloService proveedorArticuloService;

    @Resource
    private ProveedorMovimientoService proveedorMovimientoService;

    @Resource
    private ProveedorArticuloTrackService proveedorArticuloTrackService;

    @Transactional
    public Boolean addAsignacion(AsignacionCosto asignacionCosto) {

        try {

            Track track = trackService.add(new Track(null, "Asignación Costo"));
            try {
                log.debug("Track -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(track));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error Track");
            }

            Integer item = 0;
            AsientoInternal asientoInternal = contableService.nextAsiento(asignacionCosto.getFecha(), null);
            try {
                log.debug("Asiento Internal -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(asientoInternal));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error Asiento Internal");
            }
            Entrega entrega = new Entrega(null, asignacionCosto.getFecha(), asignacionCosto.getUbicacionArticulo().getUbicacionId(), "", "", asientoInternal.getFechaContable(), asientoInternal.getOrdenContable(), (byte) 0, "asignacion", track.getTrackId(), null, null);
            try {
                log.debug("Entrega -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(entrega));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error Entrega");
            }

            // Asiento
            Asiento asiento = new Asiento(null, asientoInternal.getFechaContable(), asientoInternal.getOrdenContable(), "Asignación Costos", null, null, track.getTrackId(), null);
            asiento = asientoService.add(asiento);
            try {
                log.debug("Asiento -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(asiento));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error CuentaMovimiento");
            }

            String concepto = MessageFormat.format("{0} - {1}-{2} - Asignación de Costos", asignacionCosto.getComprobante().getDescripcion(), String.format("%04d", asignacionCosto.getProveedorMovimiento().getPrefijo()), String.format("%08d", asignacionCosto.getProveedorMovimiento().getNumeroComprobante()));
            item += 1;
            CuentaMovimiento cuentaMovimiento = new CuentaMovimiento(null, entrega.getFechaContable(), entrega.getOrdenContable(), item, asignacionCosto.getArticulo().getNumeroCuenta(), asignacionCosto.getComprobante().getDebita(), asignacionCosto.getComprobante().getComprobanteId(), concepto, BigDecimal.valueOf(Math.abs(asignacionCosto.getImporte().doubleValue())), asignacionCosto.getProveedorMovimiento().getProveedorId(), 0, 0, asignacionCosto.getProveedorMovimiento().getProveedorMovimientoId(), null, (byte) 0, track.getTrackId(), null, null, null, null, null, null);
            cuentaMovimiento = cuentaMovimientoService.add(cuentaMovimiento);
            try {
                log.debug("CuentaMovimiento -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error CuentaMovimiento");
            }
            item += 1;
            cuentaMovimiento = new CuentaMovimiento(null, entrega.getFechaContable(), entrega.getOrdenContable(), item, asignacionCosto.getUbicacionArticulo().getNumeroCuenta(), (byte) (1 - asignacionCosto.getComprobante().getDebita()), asignacionCosto.getComprobante().getComprobanteId(), concepto, BigDecimal.valueOf(Math.abs(asignacionCosto.getImporte().doubleValue())), asignacionCosto.getProveedorMovimiento().getProveedorId(), 0, 0, asignacionCosto.getProveedorMovimiento().getProveedorMovimientoId(), null, (byte) 0, track.getTrackId(), null, null, null, null, null, null);
            cuentaMovimiento = cuentaMovimientoService.add(cuentaMovimiento);
            try {
                log.debug("CuentaMovimiento -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error CuentaMovimiento");
            }

            entrega = entregaService.add(entrega);
            try {
                log.debug("Entrega -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(entrega));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error Entrega");
            }
            EntregaDetalle entregaDetalle = new EntregaDetalle(null, entrega.getEntregaId(), asignacionCosto.getProveedorArticulo().getArticuloId(), asignacionCosto.getProveedorArticulo().getProveedorMovimientoId(), asignacionCosto.getProveedorArticulo().getOrden(), asignacionCosto.getProveedorArticulo().getConcepto(), asignacionCosto.getImporte(), track.getTrackId(), null, null, null, null, null);
            try {
                log.debug("EntregaDetalle -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(entregaDetalle));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error EntregaDetalle");
            }
            entregaDetalle = entregaDetalleService.add(entregaDetalle);
            try {
                log.debug("EntregaDetalle -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(entregaDetalle));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error EntregaDetalle");
            }

            ProveedorArticulo proveedorArticulo = proveedorArticuloService.findByProveedorArticuloId(asignacionCosto.getProveedorArticulo().getProveedorArticuloId());
            try {
                log.debug("ProveedorArticulo before asignado -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulo));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error ProveedorArticulo");
            }
            proveedorArticulo.setAsignado(proveedorArticulo.getAsignado().add(asignacionCosto.getImporte()).setScale(2, RoundingMode.HALF_UP));
            proveedorArticulo = proveedorArticuloService.update(proveedorArticulo, proveedorArticulo.getProveedorArticuloId());
            try {
                log.debug("ProveedorArticulo after asignado -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulo));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error ProveedorArticulo");
            }

            ProveedorArticuloTrack proveedorArticuloTrack = new ProveedorArticuloTrack(null, proveedorArticulo.getProveedorMovimientoId(), proveedorArticulo.getProveedorArticuloId(), track.getTrackId(), asignacionCosto.getImporte(), null, null, null);
            try {
                log.debug("ProveedorArticuloTrack before -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticuloTrack));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error EntregaDetalle");
            }
            proveedorArticuloTrack = proveedorArticuloTrackService.add(proveedorArticuloTrack);
            try {
                log.debug("ProveedorArticuloTrack after -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticuloTrack));
            } catch (JsonProcessingException e) {
                log.debug("JSON Error EntregaDetalle");
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
            contableService.deleteAsiento(entrega.getFechaContable(), entrega.getOrdenContable());
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

}

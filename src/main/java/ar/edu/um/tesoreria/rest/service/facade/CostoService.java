/**
 *
 */
package ar.edu.um.tesoreria.rest.service.facade;

import ar.edu.um.tesoreria.rest.exception.CuentaMovimientoNotFoundException;
import ar.edu.um.tesoreria.rest.exception.EjercicioBloqueadoException;
import ar.edu.um.tesoreria.rest.exception.EntregaNotFoundException;
import ar.edu.um.tesoreria.rest.exception.ProveedorArticuloNotFoundException;
import ar.edu.um.tesoreria.rest.exception.ProveedorMovimientoNotFoundException;
import ar.edu.um.tesoreria.rest.model.CuentaMovimiento;
import ar.edu.um.tesoreria.rest.model.Entrega;
import ar.edu.um.tesoreria.rest.model.EntregaDetalle;
import ar.edu.um.tesoreria.rest.model.ProveedorArticulo;
import ar.edu.um.tesoreria.rest.model.ProveedorMovimiento;
import ar.edu.um.tesoreria.rest.model.dto.AsignacionCosto;
import ar.edu.um.tesoreria.rest.model.internal.AsientoInternal;
import ar.edu.um.tesoreria.rest.repository.EntregaDetalleNotFoundException;
import ar.edu.um.tesoreria.rest.service.CuentaMovimientoService;
import ar.edu.um.tesoreria.rest.service.EntregaDetalleService;
import ar.edu.um.tesoreria.rest.service.EntregaService;
import ar.edu.um.tesoreria.rest.service.ProveedorArticuloService;
import ar.edu.um.tesoreria.rest.service.ProveedorMovimientoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;

/**
 * @author daniel
 */
@Service
@Slf4j
public class CostoService {

	@Autowired
	private ContableService contableService;

	@Autowired
	private EntregaService entregaService;

	@Autowired
	private EntregaDetalleService entregaDetalleService;

	@Autowired
	private CuentaMovimientoService cuentaMovimientoService;

	@Autowired
	private ProveedorArticuloService proveedorArticuloService;

	@Autowired
	private ProveedorMovimientoService proveedorMovimientoService;

	@Transactional
	public Boolean addAsignacion(AsignacionCosto asignacionCosto) {

		try {
			Integer item = 0;
			AsientoInternal asiento = contableService.nextAsiento(asignacionCosto.getFecha(), null);
			try {
				log.debug("Asiento -> {}", JsonMapper.builder().findAndAddModules().build()
						.writerWithDefaultPrettyPrinter().writeValueAsString(asiento));
			} catch (JsonProcessingException e) {
				log.debug("JSON Error Asiento");
			}
			Entrega entrega = new Entrega(null, asignacionCosto.getFecha(),
					asignacionCosto.getUbicacionArticulo().getUbicacionId(), "", "", asiento.getFechaContable(),
					asiento.getOrdenContable(), (byte) 0, "asignacion", null);
			try {
				log.debug("Entrega -> {}", JsonMapper.builder().findAndAddModules().build()
						.writerWithDefaultPrettyPrinter().writeValueAsString(entrega));
			} catch (JsonProcessingException e) {
				log.debug("JSON Error Entrega");
			}

			// Asiento
			String concepto = MessageFormat.format("{0} - {1}-{2} - Asignación de Costos",
					asignacionCosto.getComprobante().getDescripcion(),
					String.format("%04d", asignacionCosto.getProveedorMovimiento().getPrefijo()),
					String.format("%08d", asignacionCosto.getProveedorMovimiento().getNumeroComprobante()));
			item += 1;
			CuentaMovimiento cuentaMovimiento = new CuentaMovimiento(null, entrega.getFechaContable(),
					entrega.getOrdenContable(), item, asignacionCosto.getArticulo().getNumeroCuenta(),
					asignacionCosto.getComprobante().getDebita(), asignacionCosto.getComprobante().getComprobanteId(),
					concepto, new BigDecimal(Math.abs(asignacionCosto.getImporte().doubleValue())),
					asignacionCosto.getProveedorMovimiento().getProveedorId(), 0, 0,
					asignacionCosto.getProveedorMovimiento().getProveedorMovimientoId(), null, (byte) 0, null, null,
					null, null, null);
			cuentaMovimiento = cuentaMovimientoService.add(cuentaMovimiento);
			try {
				log.debug("CuentaMovimiento -> {}", JsonMapper.builder().findAndAddModules().build()
						.writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimiento));
			} catch (JsonProcessingException e) {
				log.debug("JSON Error CuentaMovimiento");
			}
			item += 1;
			cuentaMovimiento = new CuentaMovimiento(null, entrega.getFechaContable(), entrega.getOrdenContable(), item,
					asignacionCosto.getUbicacionArticulo().getNumeroCuenta(),
					(byte) (1 - asignacionCosto.getComprobante().getDebita()),
					asignacionCosto.getComprobante().getComprobanteId(), concepto,
					new BigDecimal(Math.abs(asignacionCosto.getImporte().doubleValue())),
					asignacionCosto.getProveedorMovimiento().getProveedorId(), 0, 0,
					asignacionCosto.getProveedorMovimiento().getProveedorMovimientoId(), null, (byte) 0, null, null,
					null, null, null);
			cuentaMovimiento = cuentaMovimientoService.add(cuentaMovimiento);
			try {
				log.debug("CuentaMovimiento -> {}", JsonMapper.builder().findAndAddModules().build()
						.writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimiento));
			} catch (JsonProcessingException e) {
				log.debug("JSON Error CuentaMovimiento");
			}

			entrega = entregaService.add(entrega);
			try {
				log.debug("Entrega -> {}", JsonMapper.builder().findAndAddModules().build()
						.writerWithDefaultPrettyPrinter().writeValueAsString(entrega));
			} catch (JsonProcessingException e) {
				log.debug("JSON Error Entrega");
			}
			EntregaDetalle entregaDetalle = new EntregaDetalle(null, entrega.getEntregaId(),
					asignacionCosto.getProveedorArticulo().getArticuloId(),
					asignacionCosto.getProveedorArticulo().getProveedorMovimientoId(),
					asignacionCosto.getProveedorArticulo().getOrden(),
					asignacionCosto.getProveedorArticulo().getConcepto(), asignacionCosto.getImporte(), null, null,
					null, null);
			try {
				log.debug("EntregaDetalle -> {}", JsonMapper.builder().findAndAddModules().build()
						.writerWithDefaultPrettyPrinter().writeValueAsString(entregaDetalle));
			} catch (JsonProcessingException e) {
				log.debug("JSON Error EntregaDetalle");
			}
			entregaDetalle = entregaDetalleService.add(entregaDetalle);
			try {
				log.debug("EntregaDetalle -> {}", JsonMapper.builder().findAndAddModules().build()
						.writerWithDefaultPrettyPrinter().writeValueAsString(entregaDetalle));
			} catch (JsonProcessingException e) {
				log.debug("JSON Error EntregaDetalle");
			}

			ProveedorArticulo proveedorArticulo = asignacionCosto.getProveedorArticulo();
			proveedorArticulo.setAsignado(proveedorArticulo.getAsignado().add(asignacionCosto.getImporte()).setScale(2,
					RoundingMode.HALF_UP));
			proveedorArticulo = proveedorArticuloService.update(proveedorArticulo,
					proveedorArticulo.getProveedorArticuloId());
			try {
				log.debug("ProveedorArticulo -> {}", JsonMapper.builder().findAndAddModules().build()
						.writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulo));
			} catch (JsonProcessingException e) {
				log.debug("JSON Error ProveedorArticulo");
			}
		} catch (CuentaMovimientoNotFoundException e) {
			log.debug("Error CuentaMovimiento - {}", e.getMessage());
			return false;
		} catch (EntregaNotFoundException e) {
			log.debug("Error Entrega - {}", e.getMessage());
			return false;
		} catch (EntregaDetalleNotFoundException e) {
			log.debug("Error EntregaDetalle - {}", e.getMessage());
			return false;
		} catch (ProveedorArticuloNotFoundException e) {
			log.debug("Error EntregaDetalle - {}", e.getMessage());
			return false;
		}
		return true;
	}

	@Transactional
	public Boolean deleteDesignacion(Long entregaId) {

		try {
			for (EntregaDetalle entregaDetalle : entregaDetalleService.findAllByEntregaId(entregaId)) {
				if (entregaDetalle.getProveedorArticulo() != null) {
					ProveedorArticulo proveedorArticulo = entregaDetalle.getProveedorArticulo();
					proveedorArticulo.setAsignado(proveedorArticulo.getAsignado().subtract(entregaDetalle.getCantidad())
							.setScale(2, RoundingMode.HALF_UP));
					if (proveedorArticulo.getAsignado().compareTo(BigDecimal.ZERO) < 0) {
						proveedorArticulo.setAsignado(BigDecimal.ZERO);
						proveedorArticulo = proveedorArticuloService.update(proveedorArticulo,
								proveedorArticulo.getProveedorArticuloId());
					}
				}
				entregaDetalleService.deleteByEntregaDetalleId(entregaDetalle.getEntregaDetalleId());
			}
			Entrega entrega = entregaService.findByEntregaId(entregaId);
			entregaService.deleteByEntregaId(entregaId);
			contableService.deleteAsiento(entrega.getFechaContable(), entrega.getOrdenContable());
		} catch (EjercicioBloqueadoException e) {
			log.debug("Error Ejercicio : {}", e.getMessage());
			return false;
		} catch (EntregaDetalleNotFoundException e) {
			log.debug("Error EntregaDetalle : {}", e.getMessage());
			return false;
		} catch (ProveedorArticuloNotFoundException e) {
			log.debug("Error ProveedorArticulo : {}", e.getMessage());
			return false;
		} catch (EntregaNotFoundException e) {
			log.debug("Error Entrega : {}", e.getMessage());
			return false;
		}
		return true;
	}

	@Transactional
	public Boolean depurarGastosProveedor(Integer proveedorId, Integer geograficaId) {

		try {
			for (ProveedorMovimiento proveedorMovimiento : proveedorMovimientoService.findAllByProveedorId(proveedorId,
					geograficaId)) {
				for (ProveedorArticulo proveedorArticulo : proveedorArticuloService
						.findAllByProveedorMovimientoId(proveedorMovimiento.getProveedorMovimientoId(), false)) {
					if (proveedorArticulo.getArticulo().getDirecto() == 0
							&& proveedorArticulo.getEntregado().compareTo(BigDecimal.ZERO) > 0) {
						proveedorArticulo.setEntregado(BigDecimal.ZERO);
						proveedorArticulo = proveedorArticuloService.update(proveedorArticulo,
								proveedorArticulo.getProveedorArticuloId());
					}
				}
			}
		} catch (ProveedorMovimientoNotFoundException e) {
			log.debug("Error ProveedorMovimiento - {}", e.getMessage());
			return false;
		} catch (ProveedorArticuloNotFoundException e) {
			log.debug("Error ProveedorArtiulo - {}", e.getMessage());
			return false;
		} catch (Exception e) {
			log.debug("Error Exception - {}", e.getMessage());
			return false;
		}
		return true;
	}

}

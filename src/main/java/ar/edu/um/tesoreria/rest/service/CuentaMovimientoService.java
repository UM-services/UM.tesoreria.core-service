/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.CuentaMovimientoNotFoundException;
import ar.edu.um.tesoreria.rest.model.CuentaMovimiento;
import ar.edu.um.tesoreria.rest.model.view.CuentaMovimientoAsiento;
import ar.edu.um.tesoreria.rest.repository.ICuentaMovimientoRepository;
import ar.edu.um.tesoreria.rest.service.view.CuentaMovimientoAsientoService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class CuentaMovimientoService {

	@Autowired
	private ICuentaMovimientoRepository repository;

	@Autowired
	private EntregaService entregaService;

	@Autowired
	private CuentaMovimientoAsientoService cuentaMovimientoAsientoService;

	public List<CuentaMovimiento> findAllByAsiento(OffsetDateTime fechaContable, Integer ordenContable,
			Integer itemDesde, Integer debita) {
		if (debita < 2) {
			return repository.findAllByFechaContableAndOrdenContableAndDebitaAndItemGreaterThanEqual(fechaContable,
					ordenContable, debita.byteValue(), itemDesde,
					Sort.by("debita").descending().and(Sort.by("item").ascending()));
		}
		return repository.findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(fechaContable, ordenContable,
				itemDesde, Sort.by("debita").descending().and(Sort.by("item").ascending()));
	}

	public List<CuentaMovimiento> findAllByCuentaAndFechaContableBetweenAndApertura(BigDecimal cuenta,
			OffsetDateTime desde, OffsetDateTime hasta, Byte apertura) {
		return repository.findAllByCuentaAndFechaContableBetweenAndApertura(cuenta, desde, hasta, apertura,
				Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending()));
	}

	public List<CuentaMovimientoAsiento> findAllEntregaDetalleByProveedorMovimientoId(Long proveedorMovimientoId) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		List<String> asientos = entregaService.findAllDetalleByProveedorMovimientoId(proveedorMovimientoId, false)
				.stream().map(e -> dateTimeFormatter.format(e.getFechaContable()) + "." + e.getOrdenContable())
				.collect(Collectors.toList());
		log.debug("Asientos -> {}", asientos);
		return cuentaMovimientoAsientoService.findAllByAsientoIn(asientos, Sort.by("fechaContable").ascending().and(Sort
				.by("ordenContable").ascending().and(Sort.by("debita").descending().and(Sort.by("item").ascending()))));
	}

	public CuentaMovimiento findLastByFecha(OffsetDateTime fechaContable) {
		return repository.findTopByFechaContableOrderByOrdenContableDesc(fechaContable)
				.orElseGet(() -> new CuentaMovimiento());
	}

	public CuentaMovimiento findByCuentaMovimientoId(Long cuentaMovimientoId) {
		return repository.findByCuentaMovimientoId(cuentaMovimientoId)
				.orElseThrow(() -> new CuentaMovimientoNotFoundException(cuentaMovimientoId));
	}

	public CuentaMovimiento add(CuentaMovimiento cuentaMovimiento) {
		cuentaMovimiento = repository.save(cuentaMovimiento);
		log.debug("CuentaMovimiento -> " + cuentaMovimiento);
		return cuentaMovimiento;
	}

	@Transactional
	public void deleteAsiento(OffsetDateTime fechaContable, Integer ordenContable) {
		repository.deleteAllByFechaContableAndOrdenContable(fechaContable, ordenContable);
	}

	@Transactional
	public void deleteByCuentaMovimientoId(Long cuentaMovimientoId) {
		repository.deleteByCuentaMovimientoId(cuentaMovimientoId);
	}

}

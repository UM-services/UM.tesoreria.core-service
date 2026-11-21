/**
 * 
 */
package um.tesoreria.core.service.facade;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CoeficienteInflacionException;
import um.tesoreria.core.hexagonal.contable.asiento.application.exception.AsientoException;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.entity.CuentaMovimientoEntity;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.UpdateEjercicioUseCase;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.model.CoeficienteInflacion;
import um.tesoreria.core.model.view.CuentaMensual;
import um.tesoreria.core.service.CoeficienteInflacionService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.service.EjercicioService;
import um.tesoreria.core.service.view.CuentaMensualService;
import um.tesoreria.core.util.Periodo;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class AsientoInflacionService {

	private static final int COMPROBANTE_ASIENTO = 1;
	private static final String VINCULO = "Asiento Inflación";

	private final EjercicioService ejercicioService;
	private final UpdateEjercicioUseCase updateEjercicioUseCase;
	private final CuentaMensualService cuentaMensualService;
	private final CoeficienteInflacionService coeficienteInflacionService;
	private final CuentaMovimientoService cuentaMovimientoService;
	private final ReservarOrdenContableUseCase reservarOrdenContable;
	private final AsientoService asientoService;

	public void generateAsientoInflacionResultado(Integer ejercicioId) {
		Ejercicio ejercicio = ejercicioService.findByEjercicioId(ejercicioId);

		OffsetDateTime inicio = ejercicio.getFechaInicio();
		OffsetDateTime fin = ejercicio.getFechaFinal();

		Periodo periodo = new Periodo(inicio.getYear(), inicio.getMonthValue());
		Periodo periodoFinal = new Periodo(fin.getYear(), fin.getMonthValue());

		Map<BigDecimal, CuentaMovimiento> movimientosIngresos = new HashMap<BigDecimal, CuentaMovimiento>();
		Map<BigDecimal, CuentaMovimiento> movimientosGastos = new HashMap<BigDecimal, CuentaMovimiento>();

		OffsetDateTime fechaContable = ejercicio.getFechaFinal();
		Integer ordenContable = ejercicio.getOrdenContableResultado();

		if (ordenContable == 0) {
			ordenContable = reservarOrdenContable.reservar(fechaContable);
		}

		// Solo la orden que cambia: el resto (null) conserva lo guardado, incluido bloqueado.
		updateEjercicioUseCase.updateEjercicio(ejercicioId,
				um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio.builder().ordenContableResultado(ordenContable).build());

		Integer item = 0;

		while (periodo.toPeriodo() <= periodoFinal.toPeriodo()) {
			CoeficienteInflacion coeficienteinflacion = null;
			try {
				coeficienteinflacion = coeficienteInflacionService.findByUnique(periodo.getAnho(), periodo.getMes());
			} catch (CoeficienteInflacionException e) {
				coeficienteinflacion = new CoeficienteInflacion(null, periodo.getAnho(), periodo.getMes(),
						BigDecimal.ONE);
			}

			if (coeficienteinflacion.getCoeficiente().compareTo(BigDecimal.ZERO) == 0) {
				coeficienteinflacion.setAnho(periodo.getAnho());
				coeficienteinflacion.setMes(periodo.getMes());
				coeficienteinflacion.setCoeficiente(BigDecimal.ONE);

				if (coeficienteinflacion.getCoeficienteinflacionId() != null)
					coeficienteInflacionService.update(coeficienteinflacion,
							coeficienteinflacion.getCoeficienteinflacionId());
				else
					coeficienteInflacionService.add(coeficienteinflacion);
			}

			List<CuentaMensual> ingresos = cuentaMensualService.findIngresosByMes(periodo.getAnho(), periodo.getMes());
			List<CuentaMensual> gastos = cuentaMensualService.findGastosByMes(periodo.getAnho(), periodo.getMes());

			for (CuentaMensual cuentamensual : gastos) {
				if (cuentamensual.getDebe().compareTo(cuentamensual.getHaber()) != 0
						&& !cuentamensual.getNombre().contains("***")) {
					if (movimientosIngresos.get(cuentamensual.getCuenta()) == null) {
						CuentaMovimiento cuentaMovimiento = new CuentaMovimiento();
						cuentaMovimiento.setItem(++item);
						cuentaMovimiento.setFechaContable(fechaContable);
						cuentaMovimiento.setOrdenContable(ordenContable);
						cuentaMovimiento.setNumeroCuenta(cuentamensual.getCuenta());
						cuentaMovimiento.setComprobanteId(1);
						cuentaMovimiento.setConcepto("Asiento Ajuste Inflacion");
						BigDecimal importe = cuentamensual.getHaber().subtract(cuentamensual.getDebe());
						importe = importe.multiply(coeficienteinflacion.getCoeficiente().subtract(new BigDecimal(1)));
						cuentaMovimiento.setImporte(importe);
						movimientosIngresos.put(cuentamensual.getCuenta(), cuentaMovimiento);
					} else {
						CuentaMovimiento cuentaMovimiento = movimientosIngresos.get(cuentamensual.getCuenta());
						BigDecimal importe = cuentamensual.getHaber().subtract(cuentamensual.getDebe());
						importe = importe.multiply(coeficienteinflacion.getCoeficiente().subtract(new BigDecimal(1)));
						cuentaMovimiento.setImporte(cuentaMovimiento.getImporte().add(importe));
					}
				}
			}
			for (CuentaMensual cuentamensual : ingresos) {
				if (cuentamensual.getDebe().compareTo(cuentamensual.getHaber()) != 0
						&& !cuentamensual.getNombre().contains("***")) {
					if (movimientosGastos.get(cuentamensual.getCuenta()) == null) {
						CuentaMovimiento cuentaMovimiento = new CuentaMovimiento();
						cuentaMovimiento.setItem(++item);
						cuentaMovimiento.setFechaContable(fechaContable);
						cuentaMovimiento.setOrdenContable(ordenContable);
						cuentaMovimiento.setNumeroCuenta(cuentamensual.getCuenta());
						cuentaMovimiento.setComprobanteId(1);
						cuentaMovimiento.setConcepto("Asiento Ajuste Inflacion");
						BigDecimal importe = cuentamensual.getDebe().subtract(cuentamensual.getHaber());
						importe = importe.multiply(coeficienteinflacion.getCoeficiente().subtract(new BigDecimal(1)));
						cuentaMovimiento.setImporte(importe);
						movimientosGastos.put(cuentamensual.getCuenta(), cuentaMovimiento);
					} else {
						CuentaMovimiento cuentaMovimiento = movimientosGastos.get(cuentamensual.getCuenta());
						BigDecimal importe = cuentamensual.getDebe().subtract(cuentamensual.getHaber());
						importe = importe.multiply(coeficienteinflacion.getCoeficiente().subtract(new BigDecimal(1)));
						cuentaMovimiento.setImporte(cuentaMovimiento.getImporte().add(importe));
					}
				}
			}
			periodo.next();
		}

		borrarAsientoPropio(fechaContable, ordenContable);
		guardarCabecera(fechaContable, ordenContable);

		movimientosGastos.forEach((cuenta, cuentaMovimiento) -> {
			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) > 0)
				cuentaMovimiento.setDebita((byte) 1);
			else
				cuentaMovimiento.setDebita((byte) 0);
			cuentaMovimiento.setImporte(cuentaMovimiento.getImporte().abs());

			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) != 0)
				cuentaMovimientoService.createCuentaMovimiento(cuentaMovimiento);
		});

		movimientosIngresos.forEach((cuenta, cuentaMovimiento) -> {
			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) > 0)
				cuentaMovimiento.setDebita((byte) 0);
			else
				cuentaMovimiento.setDebita((byte) 1);
			cuentaMovimiento.setImporte(cuentaMovimiento.getImporte().abs());

			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) != 0)
				cuentaMovimientoService.createCuentaMovimiento(cuentaMovimiento);
		});
	}

	public void generateAsientoInflacionBienesUso(Integer ejercicioId) {
		Ejercicio ejercicio = ejercicioService.findByEjercicioId(ejercicioId);

		OffsetDateTime inicio = ejercicio.getFechaInicio();
		OffsetDateTime fin = ejercicio.getFechaFinal();

		Periodo periodo = new Periodo(inicio.getYear(), inicio.getMonthValue());
		Periodo periodofinal = new Periodo(fin.getYear(), fin.getMonthValue());

		Map<BigDecimal, CuentaMovimiento> movimientosbienesuso = new HashMap<BigDecimal, CuentaMovimiento>();

		OffsetDateTime fechacontable = ejercicio.getFechaFinal();
		Integer ordencontable = ejercicio.getOrdenContableBienesUso();

		if (ordencontable == 0) {
			ordencontable = reservarOrdenContable.reservar(fechacontable);
		}

		// Solo la orden que cambia: el resto (null) conserva lo guardado, incluido bloqueado.
		updateEjercicioUseCase.updateEjercicio(ejercicioId,
				um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio.builder().ordenContableBienesUso(ordencontable).build());

		Integer item = 0;

		while (periodo.toPeriodo() <= periodofinal.toPeriodo()) {
			CoeficienteInflacion coeficienteinflacion = null;
			try {
				coeficienteinflacion = coeficienteInflacionService.findByUnique(periodo.getAnho(), periodo.getMes());
			} catch (CoeficienteInflacionException e) {
				coeficienteinflacion = new CoeficienteInflacion(null, periodo.getAnho(), periodo.getMes(),
						BigDecimal.ONE);
			}

			if (coeficienteinflacion.getCoeficiente().compareTo(BigDecimal.ZERO) == 0) {
				coeficienteinflacion.setAnho(periodo.getAnho());
				coeficienteinflacion.setMes(periodo.getMes());
				coeficienteinflacion.setCoeficiente(BigDecimal.ONE);

				if (coeficienteinflacion.getCoeficienteinflacionId() != null)
					coeficienteInflacionService.update(coeficienteinflacion,
							coeficienteinflacion.getCoeficienteinflacionId());
				else
					coeficienteInflacionService.add(coeficienteinflacion);
			}

			List<CuentaMensual> bienesuso = cuentaMensualService.findBienesUsoByMes(periodo.getAnho(),
					periodo.getMes());

			for (CuentaMensual cuentamensual : bienesuso) {
				if (cuentamensual.getDebe().compareTo(cuentamensual.getHaber()) != 0
						&& !cuentamensual.getNombre().contains("***")) {
					if (movimientosbienesuso.get(cuentamensual.getCuenta()) == null) {
						CuentaMovimiento cuentaMovimiento = new CuentaMovimiento();
						cuentaMovimiento.setItem(++item);
						cuentaMovimiento.setFechaContable(fechacontable);
						cuentaMovimiento.setOrdenContable(ordencontable);
						cuentaMovimiento.setNumeroCuenta(cuentamensual.getCuenta());
						cuentaMovimiento.setComprobanteId(1);
						cuentaMovimiento.setConcepto("Asiento Ajuste Inflacion");
						BigDecimal importe = cuentamensual.getHaber().subtract(cuentamensual.getDebe());
						importe = importe.multiply(coeficienteinflacion.getCoeficiente().subtract(new BigDecimal(1)));
						cuentaMovimiento.setImporte(importe);
						movimientosbienesuso.put(cuentamensual.getCuenta(), cuentaMovimiento);
					} else {
						CuentaMovimiento cuentaMovimiento = movimientosbienesuso.get(cuentamensual.getCuenta());
						BigDecimal importe = cuentamensual.getHaber().subtract(cuentamensual.getDebe());
						importe = importe.multiply(coeficienteinflacion.getCoeficiente().subtract(new BigDecimal(1)));
						cuentaMovimiento.setImporte(cuentaMovimiento.getImporte().add(importe));
					}
				}
			}
			periodo.next();
		}

		borrarAsientoPropio(fechacontable, ordencontable);
		guardarCabecera(fechacontable, ordencontable);

		movimientosbienesuso.forEach((cuenta, cuentaMovimiento) -> {
			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) > 0)
				cuentaMovimiento.setDebita((byte) 0);
			else
				cuentaMovimiento.setDebita((byte) 1);
			cuentaMovimiento.setImporte(cuentaMovimiento.getImporte().abs());

			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) != 0)
				cuentaMovimientoService.createCuentaMovimiento(cuentaMovimiento);
		});
	}

	/**
	 * Borra sólo las líneas del asiento de inflación (comprobante 1). Si el orden tuviera líneas
	 * de otro comprobante, {@code deleteAsiento} aborta y no se toca nada.
	 */
	private void borrarAsientoPropio(OffsetDateTime fecha, Integer orden) {
		cuentaMovimientoService.deleteAsiento(fecha, orden, COMPROBANTE_ASIENTO);
		try {
			AsientoEntity asiento = asientoService.findByAsiento(fecha, orden);
			asientoService.deleteByAsientoId(asiento.getAsientoId());
		} catch (AsientoException ignored) {
			// Sin cabecera: nada que borrar.
		}
	}

	/** Crea la cabecera del asiento de inflación (o corrige su vínculo si ya existía). */
	private void guardarCabecera(OffsetDateTime fecha, Integer orden) {
		try {
			AsientoEntity asiento = asientoService.findByAsiento(fecha, orden);
			if (!VINCULO.equals(asiento.getVinculo())) {
				asiento.setVinculo(VINCULO);
				asientoService.update(asiento, asiento.getAsientoId());
			}
			return;
		} catch (AsientoException ignored) {
			// No existe: se crea abajo.
		}
		asientoService.add(new AsientoEntity(null, fecha, orden, VINCULO, null, null, null, null));
	}
}

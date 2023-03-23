/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.facade;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.CoeficienteInflacionException;
import ar.edu.um.tesoreria.rest.model.CoeficienteInflacion;
import ar.edu.um.tesoreria.rest.kotlin.model.CuentaMovimiento;
import ar.edu.um.tesoreria.rest.model.Ejercicio;
import ar.edu.um.tesoreria.rest.model.view.CuentaMensual;
import ar.edu.um.tesoreria.rest.service.CoeficienteInflacionService;
import ar.edu.um.tesoreria.rest.service.CuentaMovimientoService;
import ar.edu.um.tesoreria.rest.service.EjercicioService;
import ar.edu.um.tesoreria.rest.service.view.CuentaMensualService;
import ar.edu.um.tesoreria.rest.util.Periodo;

/**
 * @author daniel
 *
 */
@Service
public class AsientoInflacionService {

	@Autowired
	private EjercicioService ejercicioService;

	@Autowired
	private CuentaMensualService cuentaMensualService;

	@Autowired
	private CoeficienteInflacionService coeficienteInflacionService;

	@Autowired
	private CuentaMovimientoService cuentaMovimientoService;

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
			CuentaMovimiento cuentaMovimiento = cuentaMovimientoService.findLastByFecha(fechaContable);
			ordenContable = 1 + cuentaMovimiento.getOrdenContable();
		}

		ejercicio.setOrdenContableResultado(ordenContable);
		ejercicioService.update(ejercicio, ejercicioId);

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

		cuentaMovimientoService.deleteAsiento(fechaContable, ordenContable);

		movimientosGastos.forEach((cuenta, cuentaMovimiento) -> {
			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) > 0)
				cuentaMovimiento.setDebita((byte) 1);
			else
				cuentaMovimiento.setDebita((byte) 0);
			cuentaMovimiento.setImporte(cuentaMovimiento.getImporte().abs());

			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) != 0)
				cuentaMovimientoService.add(cuentaMovimiento);
		});

		movimientosIngresos.forEach((cuenta, cuentaMovimiento) -> {
			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) > 0)
				cuentaMovimiento.setDebita((byte) 0);
			else
				cuentaMovimiento.setDebita((byte) 1);
			cuentaMovimiento.setImporte(cuentaMovimiento.getImporte().abs());

			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) != 0)
				cuentaMovimientoService.add(cuentaMovimiento);
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
			CuentaMovimiento cuentaMovimiento = cuentaMovimientoService.findLastByFecha(fechacontable);
			ordencontable = 1 + cuentaMovimiento.getOrdenContable();
		}

		ejercicio.setOrdenContableBienesUso(ordencontable);
		ejercicioService.update(ejercicio, ejercicioId);

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

		cuentaMovimientoService.deleteAsiento(fechacontable, ordencontable);

		movimientosbienesuso.forEach((cuenta, cuentaMovimiento) -> {
			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) > 0)
				cuentaMovimiento.setDebita((byte) 0);
			else
				cuentaMovimiento.setDebita((byte) 1);
			cuentaMovimiento.setImporte(cuentaMovimiento.getImporte().abs());

			if (cuentaMovimiento.getImporte().compareTo(BigDecimal.ZERO) != 0)
				cuentaMovimientoService.add(cuentaMovimiento);
		});
	}
}

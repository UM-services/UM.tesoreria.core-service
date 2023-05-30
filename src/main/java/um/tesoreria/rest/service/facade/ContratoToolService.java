/**
 * 
 */
package um.tesoreria.rest.service.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.model.Contrato;
import um.tesoreria.rest.model.ContratoFactura;
import um.tesoreria.rest.model.ContratoPeriodo;
import um.tesoreria.rest.model.CursoCargoContratado;
import um.tesoreria.rest.service.ContratoFacturaService;
import um.tesoreria.rest.service.ContratoPeriodoService;
import um.tesoreria.rest.service.ContratoService;
import um.tesoreria.rest.service.CursoCargoContratadoService;
import um.tesoreria.rest.util.Periodo;
import um.tesoreria.rest.util.Tool;
import lombok.extern.slf4j.Slf4j;
import um.tesoreria.rest.model.Contrato;
import um.tesoreria.rest.model.ContratoFactura;
import um.tesoreria.rest.model.ContratoPeriodo;
import um.tesoreria.rest.model.CursoCargoContratado;
import um.tesoreria.rest.service.ContratoFacturaService;
import um.tesoreria.rest.service.ContratoPeriodoService;
import um.tesoreria.rest.service.ContratoService;
import um.tesoreria.rest.service.CursoCargoContratadoService;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ContratoToolService {

	@Autowired
	private ContratoFacturaService contratoFacturaService;

	@Autowired
	private ContratoPeriodoService contratoPeriodoService;

	@Autowired
	private ContratoService contratoService;

	@Autowired
	private CursoCargoContratadoService cursoCargoContratadoService;

	@Transactional
	public Boolean addFactura(ContratoFactura contratofactura) {
		contratoFacturaService.add(contratofactura);
		List<ContratoPeriodo> periodos = contratoPeriodoService
				.findAllMarcadoByContrato(contratofactura.getContratoId());
		for (ContratoPeriodo periodo : periodos) {
			periodo.setContratoFacturaId(contratofactura.getContratofacturaId());
			periodo.setMarcaTemporal((byte) 0);
		}
		contratoPeriodoService.saveAll(periodos);
		return true;
	}

	@Transactional
	public Boolean deleteFactura(Long contratofacturaId) {
		ContratoFactura factura = contratoFacturaService.findByContratofacturaId(contratofacturaId);
		if (factura.getPendiente() == 0) {
			return false;
		}
		for (ContratoPeriodo periodo : contratoPeriodoService.findAllByContratoFactura(contratofacturaId)) {
			periodo.setContratoFacturaId(null);
			contratoPeriodoService.update(periodo, periodo.getContratoPeriodoId());
		}
		contratoFacturaService.deleteByContratofacturaId(contratofacturaId);

		return true;
	}

	@Transactional
	public Boolean deleteContrato(Long contratoId) {
		for (ContratoPeriodo periodo : contratoPeriodoService.findAllPendienteByContrato(contratoId)) {
			log.debug("ContratoPeriodo -> {}", periodo);
			contratoPeriodoService.deleteByContratoPeriodoId(periodo.getContratoPeriodoId());
		}
		contratoService.deleteByContratoId(contratoId);
		return true;
	}

	@Transactional
	public Boolean depuraContrato(Long contratoId) {
		Contrato contrato = contratoService.findByContratoId(contratoId);
		Long desde = contrato.getDesde().getYear() * 100L + contrato.getDesde().getMonthValue();
		Long hasta = contrato.getHasta().getYear() * 100L + contrato.getHasta().getMonthValue();
		for (ContratoPeriodo periodo : contratoPeriodoService.findAllPendienteByContrato(contratoId)) {
			Long periodo_ciclo = periodo.getAnho() * 100L + periodo.getMes();
			if (periodo_ciclo < desde) {
				contratoPeriodoService.deleteByContratoPeriodoId(periodo.getContratoPeriodoId());
			}
			if (periodo_ciclo > hasta) {
				contratoPeriodoService.deleteByContratoPeriodoId(periodo.getContratoPeriodoId());
			}
		}
		return true;
	}

	@Transactional
	public Boolean saveContrato(Contrato contrato) {
		if (contrato.getCanonMensualSinAjuste().compareTo(BigDecimal.ZERO) == 0) {
			contrato.setCanonMensualSinAjuste(contrato.getCanonMensual());
		}
		contrato.setCanonMensualLetras(Tool.number2Text(contrato.getCanonMensual()));
		contrato.setCanonTotal(contrato.getCanonMensual()
				.multiply(new BigDecimal(contrato.getMeses()).setScale(2, RoundingMode.HALF_UP)));
		contrato.setCanonTotalLetras(Tool.number2Text(contrato.getCanonTotal()));
		contrato.setPrimerVencimiento(
				Tool.lastDayOfMonth(contrato.getDesde().getYear(), contrato.getDesde().getMonthValue()));
		contrato.setMesesLetras(Tool.number2TextEntero(new BigDecimal(contrato.getMeses())));
		// Agrega o actualiza el contrato
		if (contrato.getContratoId() == null) {
			contrato = contratoService.add(contrato);
		} else {
			contrato = contratoService.update(contrato, contrato.getContratoId());
		}
		// Carga los períodos ya registrados
		Map<String, ContratoPeriodo> periodokeys = contratoPeriodoService.findAllByContrato(contrato.getContratoId())
				.stream().collect(Collectors.toMap(ContratoPeriodo::periodoKey, periodo -> periodo));
		// Busca los períodos incluídos
		Long desde = contrato.getDesde().getYear() * 100L + contrato.getDesde().getMonthValue();
		Long hasta = contrato.getHasta().getYear() * 100L + contrato.getHasta().getMonthValue();
		List<ContratoPeriodo> periodos = new ArrayList<ContratoPeriodo>();
		for (Periodo mes : Periodo.makePeriodos(desde, hasta)) {
			if (periodokeys.containsKey(mes.periodoKey())) {
				ContratoPeriodo periodo = periodokeys.get(mes.periodoKey());
				if (periodo.getContratoFacturaId() == null && periodo.getContratoChequeId() == null) {
					periodo.setImporte(contrato.getCanonMensual());
					periodos.add(periodo);
				}
			} else {
				periodos.add(new ContratoPeriodo(null, contrato.getContratoId(), mes.getAnho(), mes.getMes(), null,
						null, contrato.getCanonMensual(), (byte) 0, null));
			}
		}
		periodos = contratoPeriodoService.saveAll(periodos);
		// Depura contrato
		this.depuraContrato(contrato.getContratoId());
		return true;
	}

	@Transactional
	public Boolean anulaEnvio(OffsetDateTime fecha, Integer envio) {
		List<ContratoFactura> facturas = contratoFacturaService.findAllByEnvio(fecha, envio);
		for (ContratoFactura factura : facturas) {
			factura.setPendiente((byte) 1);
			factura.setAcreditacion(null);
			factura.setEnvio(0);
		}
		facturas = contratoFacturaService.saveAll(facturas);
		return true;
	}

	@Transactional
	public Boolean saveCurso(Long cursoId, Long contratoId, Integer cargotipoId, BigDecimal horassemanales) {
		// Leer contrato
		Contrato contrato = contratoService.findByContratoId(contratoId);
		log.debug("Contrato -> {}", contrato);
		OffsetDateTime desde = contrato.getDesde().withOffsetSameInstant(ZoneOffset.UTC);
		OffsetDateTime hasta = contrato.getHasta().withOffsetSameInstant(ZoneOffset.UTC);
		// Armar periodos
		List<Periodo> periodos = Periodo.makePeriodos(desde.getYear() * 100L + desde.getMonthValue(),
				hasta.getYear() * 100L + hasta.getMonthValue());
		// Recupera registrados
		Map<String, CursoCargoContratado> asignados = cursoCargoContratadoService
				.findAllByCursoIdAndContratoId(cursoId, contratoId).stream()
				.collect(Collectors.toMap(CursoCargoContratado::getPeriodo, asignado -> asignado));
		// Asociar periodos con cursos
		List<CursoCargoContratado> cargos = new ArrayList<CursoCargoContratado>();
		for (Periodo periodo : periodos) {
			CursoCargoContratado cargo = null;
			if (asignados.containsKey(periodo.getAnho() + "." + periodo.getMes())) {
				cargo = asignados.get(periodo.getAnho() + "." + periodo.getMes());
			} else {
				cargo = new CursoCargoContratado(null, cursoId, periodo.getAnho(), periodo.getMes(),
						contrato.getContratadoId(), contratoId, cargotipoId, horassemanales, BigDecimal.ZERO, null,
						null, null, (byte) 0, null);
			}
			cargos.add(cargo);
		}
		cargos = cursoCargoContratadoService.saveAll(cargos);
		return true;
	}

	@Transactional
	public void generateCurso(Integer anho, Integer mes) {
		for (CursoCargoContratado cargo : cursoCargoContratadoService.findAllByPeriodo(anho, mes)) {
			this.saveCurso(cargo.getCursoId(), cargo.getContratoId(), cargo.getCargoTipoId(),
					cargo.getHorasSemanales());
		}
	}

	@Transactional
	public void deleteCurso(Long cursoCargoContratadoId) {
		CursoCargoContratado cursoCargoContratado = cursoCargoContratadoService
				.findByCursoCargo(cursoCargoContratadoId);
		Long cursoId = cursoCargoContratado.getCursoId();
		Long contratoId = cursoCargoContratado.getContratoId();
		Long contratadoId = cursoCargoContratado.getContratadoId();
		cursoCargoContratadoService.deleteAllByCursoIdAndContratoIdAndContratadoId(cursoId, contratoId, contratadoId);
	}

}

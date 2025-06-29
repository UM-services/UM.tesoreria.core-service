/**
 * 
 */
package um.tesoreria.core.service.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.CursoCargoContratado;
import um.tesoreria.core.model.Contrato;
import um.tesoreria.core.model.ContratoFactura;
import um.tesoreria.core.model.ContratoPeriodo;
import um.tesoreria.core.service.ContratoFacturaService;
import um.tesoreria.core.service.ContratoPeriodoService;
import um.tesoreria.core.service.ContratoService;
import um.tesoreria.core.service.CursoCargoContratadoService;
import um.tesoreria.core.util.Periodo;
import um.tesoreria.core.util.Tool;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ContratoToolService {

	private final ContratoFacturaService contratoFacturaService;
	private final ContratoPeriodoService contratoPeriodoService;
	private final ContratoService contratoService;
	private final CursoCargoContratadoService cursoCargoContratadoService;

	public ContratoToolService(ContratoFacturaService contratoFacturaService,
							   ContratoPeriodoService contratoPeriodoService,
							   ContratoService contratoService,
							   CursoCargoContratadoService cursoCargoContratadoService) {
		this.contratoFacturaService = contratoFacturaService;
		this.contratoPeriodoService = contratoPeriodoService;
		this.contratoService = contratoService;
		this.cursoCargoContratadoService = cursoCargoContratadoService;
	}

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
	public Boolean saveCurso(Long cursoId, Long contratoId, Integer cargoTipoId, BigDecimal horasSemanales) {
		log.debug("Processing ContratoToolService.saveCurso");
		log.debug("ContratoToolService.saveCurso - Leyendo contrato -> {}",  contratoId);
		Contrato contrato = contratoService.findByContratoId(contratoId);
		logContrato(contrato);
		OffsetDateTime desde = contrato.getDesde().withOffsetSameInstant(ZoneOffset.UTC);
		OffsetDateTime hasta = contrato.getHasta().withOffsetSameInstant(ZoneOffset.UTC);
		// Armar periodos
		log.debug("ContratoToolService.saveCurso - Armando periodos");
		List<Periodo> periodos = Periodo.makePeriodos(desde.getYear() * 100L + desde.getMonthValue(),
				hasta.getYear() * 100L + hasta.getMonthValue());
		logPeriodos(periodos);
		// Recupera registrados
		log.debug("ContratoToolService.saveCurso - Leyendo cursos asignados");
		var cursoCargos = cursoCargoContratadoService.findAllByCursoIdAndContratoId(cursoId, contratoId);
		logCursoCargos(cursoCargos);
		Map<String, CursoCargoContratado> asignados = cursoCargos.stream()
				.collect(Collectors.toMap(CursoCargoContratado::getPeriodo, asignado -> asignado));
		// Asociar periodos con cursos
		List<CursoCargoContratado> cargos = new ArrayList<CursoCargoContratado>();
		for (Periodo periodo : periodos) {
			CursoCargoContratado cargo = null;
			if (asignados.containsKey(periodo.getAnho() + "." + periodo.getMes())) {
				cargo = asignados.get(periodo.getAnho() + "." + periodo.getMes());
				cargo.setContratadoId(contrato.getContratadoId());
			} else {
				cargo = new CursoCargoContratado(null,
						cursoId,
						periodo.getAnho(),
						periodo.getMes(),
						contrato.getContratadoId(),
						contratoId,
						cargoTipoId,
						horasSemanales,
						BigDecimal.ZERO,
						null,
						null,
						null,
						(byte) 0,
						null);
			}
			cargos.add(cargo);
		}
		log.debug("ContratoToolService.saveCurso - Cursos a registrar");
		logCursoCargos(cargos);
		cargos = cursoCargoContratadoService.saveAll(cargos);
		log.debug("ContratoToolService.saveCurso - Cursos registrados");
		logCursoCargos(cargos);
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

	private void logContrato(Contrato contrato) {
		try {
			log.debug("Contrato -> {}", JsonMapper
					.builder()
					.findAndAddModules()
					.build()
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(contrato));
		} catch (JsonProcessingException e) {
			log.debug("Contrato jsonify error -> {}", e.getMessage());
		}
	}

	private void logPeriodos(List<Periodo> periodos) {
		try {
			log.debug("Periodos -> {}", JsonMapper
					.builder()
					.findAndAddModules()
					.build()
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(periodos));
		}  catch (JsonProcessingException e) {
			log.debug("Periodos jsonify error -> {}", e.getMessage());
		}
	}

	private void logCursoCargos(List<CursoCargoContratado> cursoCargos) {
		try {
			log.debug("CursoCargos -> {}", JsonMapper
					.builder()
					.findAndAddModules()
					.build()
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(cursoCargos));
		} catch (JsonProcessingException e) {
			log.debug("CursoCargos jsonify error -> {}", e.getMessage());
		}
	}

}

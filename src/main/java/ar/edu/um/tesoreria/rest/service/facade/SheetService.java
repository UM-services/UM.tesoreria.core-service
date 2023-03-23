/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.facade;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.um.tesoreria.rest.exception.EjercicioException;
import ar.edu.um.tesoreria.rest.extern.consumer.BajaFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.CarreraFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.InscripcionFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.PlanFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.PreTurnoFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.view.InscriptoCursoFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.view.LegajoKeyFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.view.PersonaKeyFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.view.PreunivCarreraFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.view.PreunivMatricResumenFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.view.PreunivResumenFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.exception.BajaFacultadNotFoundException;
import ar.edu.um.tesoreria.rest.extern.model.BajaFacultad;
import ar.edu.um.tesoreria.rest.extern.model.CarreraFacultad;
import ar.edu.um.tesoreria.rest.extern.model.InscripcionFacultad;
import ar.edu.um.tesoreria.rest.extern.model.PlanFacultad;
import ar.edu.um.tesoreria.rest.extern.model.PreTurnoFacultad;
import ar.edu.um.tesoreria.rest.extern.model.view.InscriptoCursoFacultad;
import ar.edu.um.tesoreria.rest.extern.model.view.LegajoKeyFacultad;
import ar.edu.um.tesoreria.rest.extern.model.view.PersonaKeyFacultad;
import ar.edu.um.tesoreria.rest.extern.model.view.PreunivCarreraFacultad;
import ar.edu.um.tesoreria.rest.extern.model.view.PreunivMatricResumenFacultad;
import ar.edu.um.tesoreria.rest.extern.model.view.PreunivResumenFacultad;
import ar.edu.um.tesoreria.rest.model.ArancelTipo;
import ar.edu.um.tesoreria.rest.model.Baja;
import ar.edu.um.tesoreria.rest.model.Carrera;
import ar.edu.um.tesoreria.rest.model.ChequeraSerie;
import ar.edu.um.tesoreria.rest.model.ContratoPeriodo;
import ar.edu.um.tesoreria.rest.kotlin.model.CuentaMovimiento;
import ar.edu.um.tesoreria.rest.model.Ejercicio;
import ar.edu.um.tesoreria.rest.model.Facultad;
import ar.edu.um.tesoreria.rest.model.Geografica;
import ar.edu.um.tesoreria.rest.model.IngresoAsiento;
import ar.edu.um.tesoreria.rest.model.Lectivo;
import ar.edu.um.tesoreria.rest.model.Legajo;
import ar.edu.um.tesoreria.rest.model.PersonaSuspendido;
import ar.edu.um.tesoreria.rest.model.Plan;
import ar.edu.um.tesoreria.rest.model.ProveedorMovimiento;
import ar.edu.um.tesoreria.rest.model.TipoChequera;
import ar.edu.um.tesoreria.rest.model.TipoPago;
import ar.edu.um.tesoreria.rest.model.dto.DeudaChequera;
import ar.edu.um.tesoreria.rest.model.view.CarreraKey;
import ar.edu.um.tesoreria.rest.model.view.ChequeraPreuniv;
import ar.edu.um.tesoreria.rest.model.view.DomicilioKey;
import ar.edu.um.tesoreria.rest.model.view.IngresoPeriodo;
import ar.edu.um.tesoreria.rest.model.view.LegajoKey;
import ar.edu.um.tesoreria.rest.model.view.PersonaKey;
import ar.edu.um.tesoreria.rest.model.view.TipoPagoFecha;
import ar.edu.um.tesoreria.rest.service.ArancelTipoService;
import ar.edu.um.tesoreria.rest.service.BajaService;
import ar.edu.um.tesoreria.rest.service.CarreraService;
import ar.edu.um.tesoreria.rest.service.ChequeraCuotaService;
import ar.edu.um.tesoreria.rest.service.ChequeraSerieService;
import ar.edu.um.tesoreria.rest.service.ContratoPeriodoService;
import ar.edu.um.tesoreria.rest.service.CuentaMovimientoService;
import ar.edu.um.tesoreria.rest.service.EjercicioService;
import ar.edu.um.tesoreria.rest.service.FacultadService;
import ar.edu.um.tesoreria.rest.service.GeograficaService;
import ar.edu.um.tesoreria.rest.service.IngresoAsientoService;
import ar.edu.um.tesoreria.rest.service.LectivoService;
import ar.edu.um.tesoreria.rest.service.LegajoService;
import ar.edu.um.tesoreria.rest.service.PersonaSuspendidoService;
import ar.edu.um.tesoreria.rest.service.PlanService;
import ar.edu.um.tesoreria.rest.service.ProveedorMovimientoService;
import ar.edu.um.tesoreria.rest.service.TipoChequeraService;
import ar.edu.um.tesoreria.rest.service.TipoPagoService;
import ar.edu.um.tesoreria.rest.service.view.CarreraKeyService;
import ar.edu.um.tesoreria.rest.service.view.ChequeraPreunivService;
import ar.edu.um.tesoreria.rest.service.view.DomicilioKeyService;
import ar.edu.um.tesoreria.rest.service.view.IngresoPeriodoService;
import ar.edu.um.tesoreria.rest.service.view.LegajoKeyService;
import ar.edu.um.tesoreria.rest.service.view.PersonaKeyService;
import ar.edu.um.tesoreria.rest.service.view.TipoPagoFechaService;

/**
 * @author daniel
 *
 */
@Service
public class SheetService {

	@Autowired
	private FacultadService facultadService;

	@Autowired
	private TipoPagoService tipoPagoService;

	@Autowired
	private GeograficaService geograficaService;

	@Autowired
	private IngresoPeriodoService ingresoPeriodoService;

	@Autowired
	private ChequeraSerieService chequeraSerieService;

	@Autowired
	private PersonaKeyService personaKeyService;

	@Autowired
	private ArancelTipoService arancelTipoService;

	@Autowired
	private ChequeraCuotaService chequeraCuotaService;

	@Autowired
	private LegajoKeyService legajoKeyService;

	@Autowired
	private CarreraKeyService carreraKeyService;

	@Autowired
	private DomicilioKeyService domicilioKeyService;

	@Autowired
	private EjercicioService ejercicioService;

	@Autowired
	private ProveedorMovimientoService proveedorMovimientoService;

	@Autowired
	private SincronizeService sincronizeService;

	@Autowired
	private LectivoService lectivoService;

	@Autowired
	private PlanService planService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private ChequeraPreunivService chequeraPreunivService;

	@Autowired
	private TipoChequeraService tipoChequeraService;

	@Autowired
	private PreunivResumenFacultadConsumer preunivResumenFacultadConsumer;

	@Autowired
	private PreunivMatricResumenFacultadConsumer preunivMatricResumenFacultadConsumer;

	@Autowired
	private PreTurnoFacultadConsumer preTurnoFacultadConsumer;

	@Autowired
	private PreunivCarreraFacultadConsumer preunivCarreraFacultadConsumer;

	@Autowired
	private PersonaKeyFacultadConsumer personaKeyFacultadConsumer;

	@Autowired
	private LegajoKeyFacultadConsumer legajoKeyFacultadConsumer;

	@Autowired
	private InscripcionFacultadConsumer inscripcionFacultadConsumer;

	@Autowired
	private CarreraFacultadConsumer carreraFacultadConsumer;

	@Autowired
	private PlanFacultadConsumer planFacultadConsumer;

	@Autowired
	private InscriptoCursoFacultadConsumer inscriptoCursoFacultadConsumer;

	@Autowired
	private TipoPagoFechaService tipoPagoFechaService;

	@Autowired
	private IngresoAsientoService ingresoAsientoService;

	@Autowired
	private CuentaMovimientoService cuentaMovimientoService;

	@Autowired
	private Environment env;

	@Autowired
	private PersonaSuspendidoService personaSuspendidoService;

	@Autowired
	private ContratoPeriodoService contratoPeriodoService;

	@Autowired
	private BajaService bajaService;

	@Autowired
	private BajaFacultadConsumer bajaFacultadConsumer;

	@Autowired
	private LegajoService legajoService;

	public String generateIngresos(Integer anho, Integer mes) {
		String path = env.getProperty("path.files");

		String filename = path + "ingresos" + anho + String.format("%02d", mes) + ".xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle styleNormal = book.createCellStyle();
		Font fontNormal = book.createFont();
		fontNormal.setBold(false);
		styleNormal.setFont(fontNormal);

		CellStyle styleBold = book.createCellStyle();
		Font fontBold = book.createFont();
		fontBold.setBold(true);
		styleBold.setFont(fontBold);

		Sheet sheet = book.createSheet("Ingresos");
		Row row = null;
		Integer fila = 0;
		row = sheet.createRow(fila);
		this.setCellString(row, 0, "#Facultad", styleBold);
		this.setCellString(row, 1, "Facultad", styleBold);
		this.setCellString(row, 2, "#Sede", styleBold);
		this.setCellString(row, 3, "Sede", styleBold);
		this.setCellString(row, 4, "#Tipo Pago", styleBold);
		this.setCellString(row, 5, "Tipo Pago", styleBold);
		this.setCellString(row, 6, "Periodo", styleBold);
		this.setCellString(row, 7, "Cantidad", styleBold);
		this.setCellString(row, 8, "Total", styleBold);

		Map<Integer, Facultad> facultades = facultadService.findAll().stream()
				.collect(Collectors.toMap(Facultad::getFacultadId, facultad -> facultad));

		Map<Integer, Geografica> geograficas = geograficaService.findAll().stream()
				.collect(Collectors.toMap(Geografica::getGeograficaId, geografica -> geografica));

		Map<Integer, TipoPago> tipos = tipoPagoService.findAll().stream()
				.collect(Collectors.toMap(TipoPago::getTipoPagoId, tipoPago -> tipoPago));

		for (IngresoPeriodo ingresoperiodo : ingresoPeriodoService.findAllByPeriodo(anho, mes)) {
			Facultad facultad = facultades.get(ingresoperiodo.getFacultadId());
			Geografica geografica = geograficas.get(ingresoperiodo.getGeograficaId());
			TipoPago tipopago = tipos.get(ingresoperiodo.getTipopagoId());
			row = sheet.createRow(++fila);
			this.setCellInteger(row, 0, ingresoperiodo.getFacultadId(), styleNormal);
			this.setCellString(row, 1, facultad.getNombre(), styleNormal);
			this.setCellInteger(row, 2, ingresoperiodo.getGeograficaId(), styleNormal);
			this.setCellString(row, 3, geografica.getNombre(), styleNormal);
			this.setCellInteger(row, 4, ingresoperiodo.getTipopagoId(), styleNormal);
			this.setCellString(row, 5, tipopago.getNombre(), styleNormal);
			this.setCellString(row, 6, mes + "/" + anho, styleNormal);
			this.setCellInteger(row, 7, ingresoperiodo.getCantidad(), styleNormal);
			this.setCellBigDecimal(row, 8, ingresoperiodo.getTotal(), styleNormal);
		}

		for (Integer column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		try {
			File file = new File(filename);
			FileOutputStream output = new FileOutputStream(file);
			book.write(output);
			output.flush();
			output.close();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	public String generateDeuda(Integer facultadId, Integer lectivoId, Boolean soloDeudores,
			List<Integer> tipoChequeraIds) {

		String path = env.getProperty("path.files");
		String filename = path + "deuda." + facultadId + "." + lectivoId + ".xlsx";

		Workbook book = new XSSFWorkbook();

		CellStyle styleNormal = book.createCellStyle();
		Font fontNormal = book.createFont();
		fontNormal.setBold(false);
		styleNormal.setFont(fontNormal);

		CellStyle styleBold = book.createCellStyle();
		Font fontBold = book.createFont();
		fontBold.setBold(true);
		styleBold.setFont(fontBold);

		CellStyle styleDate = book.createCellStyle();
		styleDate.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));

		Facultad facultad = facultadService.findByFacultadId(facultadId);
		Lectivo lectivo = lectivoService.findByLectivoId(lectivoId);
		Map<Integer, Lectivo> lectivos = lectivoService.findAll().stream()
				.collect(Collectors.toMap(Lectivo::getLectivoId, ciclo -> ciclo));
		// Sincroniza Carreras
		sincronizeService.sincronizeCarrera(facultadId);

		Sheet sheet = book.createSheet("Deuda");
		Row row = null;
		Integer fila = -1;
		row = sheet.createRow(++fila);
		this.setCellString(row, 1, "Facultad", styleNormal);
		this.setCellString(row, 2, facultad.getNombre(), styleBold);
		row = sheet.createRow(++fila);
		this.setCellString(row, 1, "Lectivo", styleNormal);
		this.setCellString(row, 2, lectivo.getNombre(), styleBold);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "#", styleBold);
		this.setCellString(row, 1, "Documento", styleBold);
		this.setCellString(row, 2, "Apellido, Nombre", styleBold);
		this.setCellString(row, 3, "Chequera", styleBold);
		this.setCellString(row, 4, "Baja Tesorería", styleBold);
		this.setCellString(row, 5, "Baja Facultad", styleBold);
		this.setCellString(row, 6, "Sede", styleBold);
		this.setCellString(row, 7, "Tipo Chequera", styleBold);
		this.setCellString(row, 8, "Tipo Arancel", styleBold);
		this.setCellString(row, 9, "Fecha Emisión", styleBold);
		this.setCellString(row, 10, "Deuda", styleBold);
		this.setCellString(row, 11, "Cuotas", styleBold);
		this.setCellString(row, 12, "Carrera", styleBold);
		this.setCellString(row, 13, "Curso", styleBold);
		this.setCellString(row, 14, "Personal", styleBold);
		this.setCellString(row, 15, "Institucional", styleBold);

		Map<String, CarreraKey> carreras = carreraKeyService.findAllByFacultadId(facultadId).stream()
				.collect(Collectors.toMap(CarreraKey::getUnified, carrera -> carrera));
		Map<Integer, TipoChequera> tipos = tipoChequeraService.findAll().stream()
				.collect(Collectors.toMap(TipoChequera::getTipoChequeraId, tipo -> tipo));
		List<Legajo> legajosUpdate = new ArrayList<Legajo>();

		for (Integer tipochequeraId : tipoChequeraIds) {
			TipoChequera tipoChequera = new TipoChequera();
			Geografica geografica = new Geografica();
			if (tipos.containsKey(tipochequeraId)) {
				tipoChequera = tipos.get(tipochequeraId);
				if (tipoChequera.getGeografica() != null) {
					geografica = tipoChequera.getGeografica();
				}
			}
			List<ChequeraSerie> chequeraList = chequeraSerieService
					.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId, lectivoId, tipochequeraId);
			Map<String, ChequeraSerie> chequeras = chequeraList.stream().collect(Collectors
					.toMap(ChequeraSerie::getPersonaKey, Function.identity(), (chequera, replacement) -> chequera));
			Map<Long, Baja> bajas = bajaService
					.findAllByChequeraIdIn(
							chequeraList.stream().map(c -> c.getChequeraId()).collect(Collectors.toList()))
					.stream()
					.collect(Collectors.toMap(Baja::getChequeraId, Function.identity(), (baja, replacement) -> baja));
			List<String> keys = new ArrayList<String>(chequeras.keySet());
			Map<String, LegajoKey> legajos = legajoKeyService.findAllByFacultadIdAndUnifiedIn(facultadId, keys).stream()
					.collect(Collectors.toMap(LegajoKey::getUnified, Function.identity(),
							(legajo, replacement) -> legajo));
			Map<String, LegajoKeyFacultad> legajosFacultad = legajoKeyFacultadConsumer
					.findAllByFacultadAndKeys(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(),
							keys)
					.stream().collect(Collectors.toMap(LegajoKeyFacultad::getPersonakey, Function.identity(),
							(legajo, replacement) -> legajo));
			Map<String, DomicilioKey> domicilios = domicilioKeyService.findAllByUnifiedIn(keys).stream()
					.collect(Collectors.toMap(DomicilioKey::getUnified, domicilio -> domicilio));

			for (PersonaKey persona : personaKeyService.findAllByUnifiedIn(keys,
					Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()))) {
				ChequeraSerie chequeraSerie = chequeras.get(persona.getUnified());
				DeudaChequera deudaChequera = chequeraCuotaService.calculateDeuda(facultadId, tipochequeraId,
						chequeraSerie.getChequeraSerieId());
				Boolean show = true;
				if (soloDeudores) {
					if (deudaChequera.getDeuda().compareTo(BigDecimal.ZERO) == 0) {
						show = false;
					}
				}
				if (show) {
					Long legajoId = null;
					if (legajos.containsKey(persona.getUnified())) {
						legajoId = legajos.get(persona.getUnified()).getLegajoId();
					}
					Legajo legajo = null;
					LegajoKeyFacultad legajoFacultad = legajosFacultad.get(persona.getUnified());
					if (legajoFacultad != null) {
						Integer lectivoIdAdd = legajoFacultad.getLectivoId();
						if (!lectivos.containsKey(lectivoIdAdd)) {
							lectivoIdAdd = lectivoId;
						}
						legajosUpdate.add(legajo = new Legajo(legajoId, legajoFacultad.getPersonaId(),
								legajoFacultad.getDocumentoId(), legajoFacultad.getFacultadId(),
								legajoFacultad.getNumerolegajo(), legajoFacultad.getFecha(), lectivoIdAdd,
								legajoFacultad.getPlanId(), legajoFacultad.getCarreraId(), (byte) 1,
								legajoFacultad.getGeograficaId(), legajoFacultad.getContrasenha(),
								legajoFacultad.getIntercambio()));
					}
					ArancelTipo arancelTipo = chequeraSerie.getArancelTipo();
					CarreraKey carrera = null;
					if (legajo != null)
						carrera = carreras
								.get(legajo.getFacultadId() + "." + legajo.getPlanId() + "." + legajo.getCarreraId());
					DomicilioKey domicilio = domicilios.get(persona.getUnified());
					row = sheet.createRow(++fila);
					this.setCellInteger(row, 0, fila - 2, styleNormal);
					this.setCellBigDecimal(row, 1, persona.getPersonaId(), styleNormal);
					this.setCellString(row, 2, persona.getApellido() + ", " + persona.getNombre(), styleNormal);
					this.setCellLong(row, 3, chequeraSerie.getChequeraSerieId(), styleNormal);
					// Baja Tsoreria
					if (bajas.containsKey(chequeraSerie.getChequeraId())) {
						Baja baja = bajas.get(chequeraSerie.getChequeraId());
						this.setCellOffsetDateTime(row, 4, baja.getFecha().withOffsetSameInstant(ZoneOffset.UTC),
								styleDate);
					}
					// Baja Facultad
					BajaFacultad bajaFacultad = null;
					try {
						bajaFacultad = bajaFacultadConsumer.findByUnique(facultad.getApiserver(), facultad.getApiport(),
								facultadId, persona.getPersonaId(), persona.getDocumentoId(), lectivoId);
					} catch (BajaFacultadNotFoundException e) {

					}
					if (bajaFacultad != null) {
						this.setCellOffsetDateTime(row, 5,
								bajaFacultad.getFecha().withOffsetSameInstant(ZoneOffset.UTC), styleDate);
					}
					this.setCellString(row, 6, geografica.getNombre(), styleNormal);
					this.setCellString(row, 7, tipoChequera.getNombre(), styleNormal);
					this.setCellString(row, 8, arancelTipo.getDescripcion(), styleNormal);
					this.setCellOffsetDateTime(row, 9, chequeraSerie.getFecha().withOffsetSameInstant(ZoneOffset.UTC),
							styleDate);
					this.setCellBigDecimal(row, 10, deudaChequera.getDeuda(), styleNormal);
					this.setCellInteger(row, 11, deudaChequera.getCuotas(), styleNormal);
					if (carrera != null) {
						this.setCellString(row, 12, carrera.getNombre(), styleNormal);
					}
					this.setCellInteger(row, 13, chequeraSerie.getCursoId(), styleNormal);
					if (domicilio != null) {
						this.setCellString(row, 14, domicilio.getEmailPersonal(), styleNormal);
						this.setCellString(row, 15, domicilio.getEmailInstitucional(), styleNormal);
					}
				}
			}
		}
		// Actualiza los legajos de la tesorería
		legajosUpdate = legajoService.saveAll(legajosUpdate);

		for (Integer column = 0; column < sheet.getRow(2).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		try {
			File file = new File(filename);
			FileOutputStream output = new FileOutputStream(file);
			book.write(output);
			output.flush();
			output.close();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	public String generateEjercicioOp(Integer ejercicioId) {
		String path = env.getProperty("path.files");

		String filename = path + "listaop." + ejercicioId + ".xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle styleNormal = book.createCellStyle();
		Font fontNormal = book.createFont();
		fontNormal.setBold(false);
		styleNormal.setFont(fontNormal);

		CellStyle styleBold = book.createCellStyle();
		Font fontBold = book.createFont();
		fontBold.setBold(true);
		styleBold.setFont(fontBold);

		CellStyle styleDate = book.createCellStyle();
		styleDate.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));

		Sheet sheet = book.createSheet("OP");
		Row row = null;
		Ejercicio ejercicio = null;
		try {
			ejercicio = ejercicioService.findByEjercicioId(ejercicioId);
		} catch (EjercicioException e) {
			ejercicio = new Ejercicio();
		}
		Integer fila = -1;
		row = sheet.createRow(++fila);
		this.setCellString(row, 2, ejercicio.getNombre(), styleBold);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Número", styleBold);
		this.setCellString(row, 1, "Fecha", styleBold);
		this.setCellString(row, 2, "Beneficiario", styleBold);
		this.setCellString(row, 3, "Importe", styleBold);
		this.setCellString(row, 4, "Cancelado", styleBold);
		this.setCellString(row, 5, "Concepto", styleBold);
		this.setCellString(row, 6, "Fecha Anulación", styleBold);
		this.setCellString(row, 7, "Sede", styleBold);

		List<ProveedorMovimiento> ordenes = proveedorMovimientoService.findAllByComprobanteIdAndFechaComprobanteBetween(
				6, ejercicio.getFechaInicio(), ejercicio.getFechaFinal());
		Map<Long, ProveedorMovimiento> ordenes_map = ordenes.stream()
				.collect(Collectors.toMap(ProveedorMovimiento::getNumeroComprobante, movimiento -> movimiento));
		Map<Integer, Geografica> geograficas = geograficaService.findAll().stream()
				.collect(Collectors.toMap(Geografica::getGeograficaId, geografica -> geografica));
		Long orden_minimo = ordenes.stream().mapToLong(movimiento -> movimiento.getNumeroComprobante()).min()
				.orElseThrow(NoSuchElementException::new);
		Long orden_maximo = ordenes.stream().mapToLong(movimiento -> movimiento.getNumeroComprobante()).max()
				.orElseThrow(NoSuchElementException::new);

		for (Long orden_number = orden_minimo; orden_number <= orden_maximo; orden_number++) {
			ProveedorMovimiento movimiento = null;
			if (ordenes_map.containsKey(orden_number)) {
				movimiento = ordenes_map.get(orden_number);
			}
			row = sheet.createRow(++fila);
			this.setCellLong(row, 0, orden_number, styleNormal);
			if (movimiento != null) {
				this.setCellOffsetDateTime(row, 1, movimiento.getFechaComprobante().plusHours(3), styleDate);
				this.setCellString(row, 2, movimiento.getNombreBeneficiario(), styleNormal);
				this.setCellBigDecimal(row, 3, movimiento.getImporte(), styleNormal);
				this.setCellBigDecimal(row, 4, movimiento.getCancelado(), styleNormal);
				this.setCellString(row, 5, movimiento.getConcepto(), styleNormal);
				if (movimiento.getFechaAnulacion() != null) {
					this.setCellOffsetDateTime(row, 6, movimiento.getFechaAnulacion().plusHours(3), styleDate);
				}
				if (movimiento.getGeograficaId() != null) {
					Geografica geografica = geograficas.get(movimiento.getGeograficaId());
					this.setCellString(row, 7, geografica.getNombre(), styleNormal);
				}
			}
		}

		for (Integer column = 0; column < sheet.getRow(1).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		try {
			File file = new File(filename);
			FileOutputStream output = new FileOutputStream(file);
			book.write(output);
			output.flush();
			output.close();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	public String generateEficienciaPre(Integer facultadId, Integer lectivoId) {
		String path = env.getProperty("path.files");

		String filename = path + "eficienciapre." + facultadId + "." + lectivoId + ".xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle styleNormal = book.createCellStyle();
		Font fontNormal = book.createFont();
		fontNormal.setBold(false);
		styleNormal.setFont(fontNormal);

		CellStyle styleBold = book.createCellStyle();
		Font fontBold = book.createFont();
		fontBold.setBold(true);
		styleBold.setFont(fontBold);

		CellStyle styleDate = book.createCellStyle();
		styleDate.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));

		Sheet sheet = book.createSheet("Eficiencia Pre");
		Row row = null;
		Lectivo lectivo = null;
		try {
			lectivo = lectivoService.findByLectivoId(lectivoId);
		} catch (EjercicioException e) {
			lectivo = new Lectivo();
		}

		Facultad facultad = facultadService.findByFacultadId(facultadId);

		Integer fila = -1;
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, lectivo.getNombre(), styleBold);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Facultad", styleBold);
		this.setCellString(row, 1, "Sede", styleBold);
		this.setCellString(row, 2, "Carrera", styleBold);
		this.setCellString(row, 3, "Turno", styleBold);
		this.setCellString(row, 4, "Preuniversitarios", styleBold);
		this.setCellString(row, 5, "Con Chequera", styleBold);
		this.setCellString(row, 6, "Matriculados", styleBold);
		this.setCellString(row, 7, "Deuda", styleBold);

		Map<Integer, Geografica> geograficas = geograficaService.findAll().stream()
				.collect(Collectors.toMap(Geografica::getGeograficaId, geografica -> geografica));

		// Matriculados
		Map<String, PreunivMatricResumenFacultad> matriculas = preunivMatricResumenFacultadConsumer
				.findAllByLectivo(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), lectivoId)
				.stream().collect(Collectors.toMap(PreunivMatricResumenFacultad::getKey, matricula -> matricula));
		// Turnos
		Map<String, PreTurnoFacultad> turnos = preTurnoFacultadConsumer
				.findAllByLectivo(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), lectivoId)
				.stream().collect(Collectors.toMap(PreTurnoFacultad::getKey, turno -> turno));
		// Sincronizar carreras
		sincronizeService.sincronizeCarrera(facultad.getFacultadId());

		Map<String, Plan> planes = planService.findAllByFacultadId(facultad.getFacultadId()).stream()
				.collect(Collectors.toMap(Plan::getPlanKey, plan -> plan));
		Map<String, Carrera> carreras = carreraService.findAllByFacultadId(facultad.getFacultadId()).stream()
				.collect(Collectors.toMap(Carrera::getCarreraKey, carrera -> carrera));

		Map<String, ChequeraPreuniv> chequerasPre = chequeraPreunivService
				.findAllByFacultadIdAndLectivoId(facultadId, lectivoId - 1).stream().collect(Collectors
						.toMap(ChequeraPreuniv::getPersonaKey, Function.identity(), (existing, chequera) -> chequera));

		for (PreunivResumenFacultad resumen : preunivResumenFacultadConsumer.findAllByLectivo(facultad.getApiserver(),
				facultad.getApiport(), facultad.getFacultadId(), lectivoId)) {
			// Recupera lista de alumnos inscriptos en el turno
			List<PreunivCarreraFacultad> preInscriptosCarrera = preunivCarreraFacultadConsumer.findAllByCarrera(
					facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId, resumen.getGeograficaId(),
					resumen.getTurnoId(), resumen.getPlanId(), resumen.getCarreraId());
			List<String> keysFacultad = preInscriptosCarrera.stream()
					.map(pre -> pre.getPersonaId() + "." + pre.getDocumentoId()).collect(Collectors.toList());
			Map<String, ChequeraPreuniv> chequerasPreInscriptos = chequeraPreunivService
					.findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonakeyIn(resumen.getFacultadId(),
							resumen.getLectivoId() - 1, resumen.getGeograficaId(), keysFacultad)
					.stream().collect(Collectors.toMap(ChequeraPreuniv::getPersonaKey, Function.identity(),
							(existing, chequera) -> chequera));

			// Elimina de chequerasPre todas las chequeras de inscriptos
			chequerasPreInscriptos.keySet().forEach(chequerasPre::remove);

			// Fila
			row = sheet.createRow(++fila);
			this.setCellString(row, 0, facultad.getNombre(), styleNormal);
			if (geograficas.containsKey(resumen.getGeograficaId())) {
				Geografica geografica = geograficas.get(resumen.getGeograficaId());
				this.setCellString(row, 1, geografica.getNombre(), styleNormal);
			}
			if (carreras
					.containsKey(resumen.getFacultadId() + "." + resumen.getPlanId() + "." + resumen.getCarreraId())) {
				Plan plan = planes.get(resumen.getFacultadId() + "." + resumen.getPlanId());
				Carrera carrera = carreras
						.get(resumen.getFacultadId() + "." + resumen.getPlanId() + "." + resumen.getCarreraId());
				String nombreCarrera = "";
				if (plan != null) {
					nombreCarrera += plan.getNombre();
				}
				if (carrera != null) {
					nombreCarrera += " / " + carrera.getNombre();
				}
				this.setCellString(row, 2, nombreCarrera, styleBold);
			}
			if (turnos.containsKey(resumen.getFacultadId() + "." + resumen.getLectivoId() + "."
					+ resumen.getGeograficaId() + "." + resumen.getTurnoId())) {
				PreTurnoFacultad turno = turnos.get(resumen.getFacultadId() + "." + resumen.getLectivoId() + "."
						+ resumen.getGeograficaId() + "." + resumen.getTurnoId());
				this.setCellString(row, 3, turno.getNombre(), styleNormal);
			}
			this.setCellInteger(row, 4, resumen.getCantidad(), styleNormal);
			this.setCellInteger(row, 5, chequerasPreInscriptos.size(), styleNormal);
			Integer sinChequera = resumen.getCantidad() - chequerasPreInscriptos.size();
			this.setCellInteger(row, 6, 0, styleNormal);
			if (matriculas.containsKey(resumen.getKey())) {
				PreunivMatricResumenFacultad matricula = matriculas.get(resumen.getKey());
				this.setCellInteger(row, 6, matricula.getCantidad(), styleNormal);
			}
			// Calcula Deuda
			BigDecimal deuda = BigDecimal.ZERO;
			for (ChequeraPreuniv chequera : chequerasPreInscriptos.values()) {
				deuda = deuda.add(chequeraCuotaService.calculateDeuda(chequera.getFacultadId(),
						chequera.getTipoChequeraId(), chequera.getChequeraSerieId()).getDeuda());
			}
			this.setCellBigDecimal(row, 7, deuda, styleNormal);
			// Lista los alumnos sin chequera
			if (sinChequera > 0) {
				List<String> keys = new ArrayList<String>();
				for (String key : keysFacultad)
					if (!chequerasPreInscriptos.containsKey(key))
						keys.add(key);
				for (PersonaKey persona : personaKeyService.findAllByUnifiedIn(keys, Sort.by("apellido").ascending())) {
					row = sheet.createRow(++fila);
					this.setCellString(row, 2,
							"(" + persona.getUnified() + ") - " + persona.getApellido() + ", " + persona.getNombre(),
							styleNormal);
				}
			}
		}

		++fila;
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Chequeras SIN Inscripto", styleBold);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Facultad", styleBold);
		this.setCellString(row, 1, "Sede", styleBold);
		this.setCellString(row, 2, "Apellido, Nombre", styleBold);
		this.setCellString(row, 3, "Número", styleBold);
		this.setCellString(row, 4, "Deuda", styleBold);
		for (ChequeraPreuniv chequeraPreuniv : chequerasPre.values()) {
			row = sheet.createRow(++fila);
			this.setCellString(row, 0, chequeraPreuniv.getFacultad().getNombre(), styleNormal);
			this.setCellString(row, 1, chequeraPreuniv.getGeografica().getNombre(), styleNormal);
			this.setCellString(row, 2, "(" + chequeraPreuniv.getPersonaKey() + ") - "
					+ chequeraPreuniv.getPersona().getApellido() + ", " + chequeraPreuniv.getPersona().getNombre(),
					styleNormal);
			this.setCellString(row, 3, chequeraPreuniv.getChequera(), styleNormal);
			// Calcula Deuda
			BigDecimal deuda = chequeraCuotaService.calculateDeuda(chequeraPreuniv.getFacultadId(),
					chequeraPreuniv.getTipoChequeraId(), chequeraPreuniv.getChequeraSerieId()).getDeuda();
			this.setCellBigDecimal(row, 4, deuda, styleNormal);
		}

		for (Integer column = 0; column < sheet.getRow(1).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		try {
			File file = new File(filename);
			FileOutputStream output = new FileOutputStream(file);
			book.write(output);
			output.flush();
			output.close();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	public String generateComparativoPre(Integer facultadId, Integer lectivoId) {
		String path = env.getProperty("path.files");

		String filename = path + "comparativopre." + facultadId + "." + lectivoId + ".xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle styleNormal = book.createCellStyle();
		Font fontNormal = book.createFont();
		fontNormal.setBold(false);
		styleNormal.setFont(fontNormal);

		CellStyle styleBold = book.createCellStyle();
		Font fontBold = book.createFont();
		fontBold.setBold(true);
		styleBold.setFont(fontBold);

		CellStyle styleDate = book.createCellStyle();
		styleDate.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));

		Sheet sheet = book.createSheet("Comparativo Pre");
		Row row = null;
		Lectivo lectivo = null;
		try {
			lectivo = lectivoService.findByLectivoId(lectivoId);
		} catch (EjercicioException e) {
			lectivo = new Lectivo();
		}

		Facultad facultad = facultadService.findByFacultadId(facultadId);

		Integer fila = -1;
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Facultad", styleNormal);
		this.setCellString(row, 1, facultad.getNombre(), styleBold);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Lectivo", styleNormal);
		this.setCellString(row, 1, lectivo.getNombre(), styleBold);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Tesorería", styleBold);
		this.setCellString(row, 7, "Facultad", styleBold);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Documento", styleBold);
		this.setCellString(row, 1, "Apellido, Nombre", styleBold);
		this.setCellString(row, 2, "Chequera", styleBold);
		this.setCellString(row, 3, "Sede", styleBold);
		this.setCellString(row, 4, "Tipo Chequera", styleBold);
		this.setCellString(row, 5, "Tipo Arancel", styleBold);
		this.setCellString(row, 6, "Carrera", styleBold);
		this.setCellString(row, 7, "Documento", styleBold);
		this.setCellString(row, 8, "Apellido, Nombre", styleBold);
		this.setCellString(row, 9, "Sede", styleBold);
		this.setCellString(row, 10, "Turno", styleBold);
		this.setCellString(row, 11, "Carrera", styleBold);

		Map<Integer, ArancelTipo> aranceles = arancelTipoService.findAll().stream()
				.collect(Collectors.toMap(ArancelTipo::getArancelTipoId, arancelTipo -> arancelTipo));
		Map<Integer, TipoChequera> tipos = tipoChequeraService.findAll().stream()
				.collect(Collectors.toMap(TipoChequera::getTipoChequeraId, tipo -> tipo));
		Map<Integer, Geografica> geograficas = geograficaService.findAll().stream()
				.collect(Collectors.toMap(Geografica::getGeograficaId, geografica -> geografica));
		Map<String, CarreraKey> carreras = carreraKeyService.findAllByFacultadId(facultad.getFacultadId()).stream()
				.collect(Collectors.toMap(CarreraKey::getUnified, carrera -> carrera));
		Map<String, ChequeraPreuniv> chequeras = chequeraPreunivService
				.findAllByFacultadIdAndLectivoId(facultad.getFacultadId(), lectivoId - 1).stream()
				.collect(Collectors.toMap(ChequeraPreuniv::getPersonaKey, Function.identity(),
						(chequera, replacement) -> chequera));
		List<String> keys = new ArrayList<String>(chequeras.keySet());
		Map<String, PersonaKey> personas = personaKeyService.findAllByUnifiedIn(keys, null).stream()
				.collect(Collectors.toMap(PersonaKey::getUnified, persona -> persona));
		Map<String, LegajoKey> legajos = legajoKeyService
				.findAllByFacultadIdAndUnifiedIn(facultad.getFacultadId(), keys).stream()
				.collect(Collectors.toMap(LegajoKey::getUnified, legajo -> legajo));
		// Turnos
		Map<String, PreTurnoFacultad> turnos = preTurnoFacultadConsumer
				.findAllByLectivo(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), lectivoId)
				.stream().collect(Collectors.toMap(PreTurnoFacultad::getKey, turno -> turno));
		// Inscriptos preuniversitario
		List<PreunivCarreraFacultad> preInscriptosCarrera = preunivCarreraFacultadConsumer
				.findAllByLectivo(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), lectivoId);
		Map<String, PreunivCarreraFacultad> preunivs_facultad = preInscriptosCarrera.stream().collect(Collectors.toMap(
				PreunivCarreraFacultad::getUnified, Function.identity(), (inscripcion, replacement) -> inscripcion));
		List<String> keysFacultad = preInscriptosCarrera.stream()
				.map(pre -> pre.getPersonaId() + "." + pre.getDocumentoId()).collect(Collectors.toList());
		// Personas
		Map<String, PersonaKeyFacultad> personas_facultad = personaKeyFacultadConsumer
				.findAllByUnifieds(facultad.getApiserver(), facultad.getApiport(), keysFacultad).stream()
				.collect(Collectors.toMap(PersonaKeyFacultad::getUnified, persona -> persona));

		// Busca diferencias entre facultad y tesoreria
		for (PersonaKey persona : personas.values()) {
			if (personas_facultad.containsKey(persona.getUnified())) {
				persona.setMark_facultad(true);
			}
		}
		for (PersonaKeyFacultad persona : personas_facultad.values()) {
			if (personas.containsKey(persona.getUnified())) {
				persona.setMark_tesoreria(true);
			}
		}

		// Llena planilla comunes
		for (ChequeraPreuniv chequera : chequeras.values()) {
			Geografica geografica = geograficas.get(chequera.getGeograficaId());
			TipoChequera tipoChequera = tipos.get(chequera.getTipoChequeraId());
			ArancelTipo arancelTipo = aranceles.get(chequera.getArancelTipoId());
			CarreraKey carrera = null;
			PersonaKey persona = new PersonaKey();
			Boolean show = false;
			if (personas.containsKey(chequera.getPersonaKey())) {
				persona = personas.get(chequera.getPersonaKey());
				show = persona.getMark_facultad();
			}
			if (show) {
				sincronizeService.sincronizeCarreraAlumno(facultad.getFacultadId(), persona.getPersonaId(),
						persona.getDocumentoId());
				LegajoKey legajo = legajos.get(persona.getUnified());
				if (legajo != null)
					carrera = carreras
							.get(legajo.getFacultadId() + "." + legajo.getPlanId() + "." + legajo.getCarreraId());
				row = sheet.createRow(++fila);
				this.setCellString(row, 0, chequera.getPersonaKey(), styleNormal);
				this.setCellString(row, 1, persona.getApellido() + ", " + persona.getNombre(), styleNormal);
				this.setCellLong(row, 2, chequera.getChequeraSerieId(), styleNormal);
				this.setCellString(row, 3, geografica.getNombre(), styleNormal);
				this.setCellString(row, 4, tipoChequera.getNombre(), styleNormal);
				this.setCellString(row, 5, arancelTipo.getDescripcion(), styleNormal);
				if (carrera != null)
					this.setCellString(row, 6, carrera.getNombre(), styleNormal);

				// Datos Facultad
				PreunivCarreraFacultad inscripcion = preunivs_facultad.get(chequera.getPersonaKey());
				PersonaKeyFacultad persona_facultad = personas_facultad.get(inscripcion.getUnified());
				this.setCellString(row, 7, chequera.getPersonaKey(), styleNormal);
				this.setCellString(row, 8, persona_facultad.getApellido() + ", " + persona_facultad.getNombre(),
						styleNormal);
				geografica = geograficas.get(inscripcion.getGeograficaId());
				this.setCellString(row, 9, geografica.getNombre(), styleNormal);
				PreTurnoFacultad turno = turnos.get(inscripcion.getFacultadId() + "." + inscripcion.getLectivoId() + "."
						+ inscripcion.getGeograficaId() + "." + inscripcion.getTurnoId());
				this.setCellString(row, 10, turno.getNombre(), styleNormal);
				carrera = carreras.get(
						inscripcion.getFacultadId() + "." + inscripcion.getPlanId() + "." + inscripcion.getCarreraId());
				if (carrera != null) {
					this.setCellString(row, 11, carrera.getNombre(), styleNormal);
				}
			}
		}
		// Llena planilla sólo tesoreria
		for (ChequeraPreuniv chequera : chequeras.values()) {
			Geografica geografica = geograficas.get(chequera.getGeograficaId());
			TipoChequera tipoChequera = tipos.get(chequera.getTipoChequeraId());
			ArancelTipo arancelTipo = aranceles.get(chequera.getArancelTipoId());
			CarreraKey carrera = null;
			PersonaKey persona = new PersonaKey();
			Boolean show = false;
			if (personas.containsKey(chequera.getPersonaKey())) {
				persona = personas.get(chequera.getPersonaKey());
				show = persona.getMark_facultad();
			}
			if (!show) {
				LegajoKey legajo = legajos.get(persona.getUnified());
				if (legajo != null)
					carrera = carreras
							.get(legajo.getFacultadId() + "." + legajo.getPlanId() + "." + legajo.getCarreraId());
				row = sheet.createRow(++fila);
				this.setCellString(row, 0, chequera.getPersonaKey(), styleNormal);
				this.setCellString(row, 1, persona.getApellido() + ", " + persona.getNombre(), styleNormal);
				this.setCellLong(row, 2, chequera.getChequeraSerieId(), styleNormal);
				this.setCellString(row, 3, geografica.getNombre(), styleNormal);
				this.setCellString(row, 4, tipoChequera.getNombre(), styleNormal);
				this.setCellString(row, 5, arancelTipo.getDescripcion(), styleNormal);
				if (carrera != null)
					this.setCellString(row, 6, carrera.getNombre(), styleNormal);
			}
		}
		// Llena planilla sólo facultad
		for (PreunivCarreraFacultad inscripcion : preInscriptosCarrera) {
			Boolean show = false;
			if (personas_facultad.containsKey(inscripcion.getUnified())) {
				PersonaKeyFacultad persona_facultad = personas_facultad.get(inscripcion.getUnified());
				show = persona_facultad.getMark_tesoreria();
			}
			if (!show) {
				row = sheet.createRow(++fila);
				// Datos Facultad
				PersonaKeyFacultad persona_facultad = personas_facultad.get(inscripcion.getUnified());
				this.setCellString(row, 7, inscripcion.getUnified(), styleNormal);
				this.setCellString(row, 8, persona_facultad.getApellido() + ", " + persona_facultad.getNombre(),
						styleNormal);
				Geografica geografica = geograficas.get(inscripcion.getGeograficaId());
				this.setCellString(row, 9, geografica.getNombre(), styleNormal);
				PreTurnoFacultad turno = turnos.get(inscripcion.getFacultadId() + "." + inscripcion.getLectivoId() + "."
						+ inscripcion.getGeograficaId() + "." + inscripcion.getTurnoId());
				this.setCellString(row, 10, turno.getNombre(), styleNormal);
				CarreraKey carrera = carreras.get(
						inscripcion.getFacultadId() + "." + inscripcion.getPlanId() + "." + inscripcion.getCarreraId());
				this.setCellString(row, 11, carrera.getNombre(), styleNormal);
			}
		}

		for (Integer column = 0; column < sheet.getRow(3).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		try {
			File file = new File(filename);
			FileOutputStream output = new FileOutputStream(file);
			book.write(output);
			output.flush();
			output.close();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	public String generateMatriculados(Integer facultadId, Integer lectivoId) {
		String path = env.getProperty("path.files");

		String filename = path + "matriculados." + facultadId + "." + lectivoId + ".xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle styleNormal = book.createCellStyle();
		Font fontNormal = book.createFont();
		fontNormal.setBold(false);
		styleNormal.setFont(fontNormal);

		CellStyle styleBold = book.createCellStyle();
		Font fontBold = book.createFont();
		fontBold.setBold(true);
		styleBold.setFont(fontBold);

		CellStyle styleDate = book.createCellStyle();
		styleDate.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));

		Sheet sheet = book.createSheet("Matriculados");
		Row row = null;
		Lectivo lectivo = null;
		try {
			lectivo = lectivoService.findByLectivoId(lectivoId);
		} catch (EjercicioException e) {
			lectivo = new Lectivo();
		}

		Facultad facultad = facultadService.findByFacultadId(facultadId);

		Integer fila = -1;
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Facultad", styleNormal);
		this.setCellString(row, 1, facultad.getNombre(), styleBold);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Lectivo", styleNormal);
		this.setCellString(row, 1, lectivo.getNombre(), styleBold);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Documento", styleBold);
		this.setCellString(row, 1, "Apellido, Nombre", styleBold);
		this.setCellString(row, 2, "Sede", styleBold);
		this.setCellString(row, 3, "Carrera", styleBold);
		this.setCellString(row, 4, "Fecha Matriculación", styleBold);
		this.setCellString(row, 5, "Intercambio", styleBold);
		this.setCellString(row, 6, "Curso", styleBold);
		this.setCellString(row, 7, "Tipo Chequera", styleBold);
		this.setCellString(row, 8, "Tipo Arancel", styleBold);
		this.setCellString(row, 9, "Chequera", styleBold);
		this.setCellString(row, 10, "Curso", styleBold);

		List<InscripcionFacultad> inscriptos = inscripcionFacultadConsumer.findAllByLectivo(facultad.getApiserver(),
				facultad.getApiport(), facultadId, lectivoId);
		List<String> unifieds = inscriptos.stream().map(inscripto -> inscripto.getPersonaKey())
				.collect(Collectors.toList());
		Map<String, PersonaKeyFacultad> personas = personaKeyFacultadConsumer
				.findAllByUnifieds(facultad.getApiserver(), facultad.getApiport(), unifieds).stream().collect(Collectors
						.toMap(PersonaKeyFacultad::getUnified, Function.identity(), (persona, replacement) -> persona));
		Map<String, LegajoKeyFacultad> legajos = legajoKeyFacultadConsumer
				.findAllByFacultadAndKeys(facultad.getApiserver(), facultad.getApiport(), facultadId, unifieds).stream()
				.collect(Collectors.toMap(LegajoKeyFacultad::getLegajoKey, Function.identity(),
						(legajo, replacemente) -> legajo));
		Map<Integer, Geografica> geograficas = geograficaService.findAll().stream()
				.collect(Collectors.toMap(Geografica::getGeograficaId, geografica -> geografica));
		Map<String, PlanFacultad> planes = planFacultadConsumer.findAll(facultad.getApiserver(), facultad.getApiport())
				.stream().collect(Collectors.toMap(PlanFacultad::getPlanKey, plan -> plan));
		Map<String, CarreraFacultad> carreras = carreraFacultadConsumer
				.findAll(facultad.getApiserver(), facultad.getApiport()).stream()
				.collect(Collectors.toMap(CarreraFacultad::getCarreraKey, carrera -> carrera));
		Map<String, ChequeraSerie> chequeras = chequeraSerieService
				.findAllByLectivoIdAndFacultadId(lectivoId, facultadId).stream().collect(Collectors.toMap(
						ChequeraSerie::getFacultadKey, Function.identity(), (chequera, replacement) -> chequera));
		Map<Integer, TipoChequera> tipos = tipoChequeraService.findAll().stream()
				.collect(Collectors.toMap(TipoChequera::getTipoChequeraId, tipo -> tipo));
		Map<Integer, ArancelTipo> aranceles = arancelTipoService.findAll().stream()
				.collect(Collectors.toMap(ArancelTipo::getArancelTipoId, arancelTipo -> arancelTipo));
		for (InscripcionFacultad inscripcion : inscriptos) {
			row = sheet.createRow(++fila);
			this.setCellString(row, 0, inscripcion.getPersonaKey(), styleNormal);
			if (personas.containsKey(inscripcion.getPersonaKey())) {
				this.setCellString(row, 1, personas.get(inscripcion.getPersonaKey()).apellidonombre(), styleNormal);
			}
			if (geograficas.containsKey(inscripcion.getGeograficaId())) {
				this.setCellString(row, 2, geograficas.get(inscripcion.getGeograficaId()).getNombre(), styleNormal);
			}
			if (carreras.containsKey(inscripcion.getCarreraKey())) {
				this.setCellString(row, 3, planes.get(inscripcion.getPlanKey()).getNombre() + " / "
						+ carreras.get(inscripcion.getCarreraKey()).getNombre(), styleNormal);
			}
			this.setCellOffsetDateTime(row, 4, inscripcion.getFecha(), styleDate);

			if (legajos.containsKey(inscripcion.getLegajoKey())) {
				this.setCellString(row, 5, legajos.get(inscripcion.getLegajoKey()).getIntercambio() == 0 ? "No" : "Si",
						styleNormal);
			}
			this.setCellInteger(row, 6, inscripcion.getCurso(), styleNormal);

			String facultadKey = facultadId + "." + lectivoId + "." + inscripcion.getGeograficaId() + "."
					+ inscripcion.getPersonaKey();
			if (chequeras.containsKey(facultadKey)) {
				ChequeraSerie chequera = chequeras.get(facultadKey);
				this.setCellString(row, 7, tipos.get(chequera.getTipoChequeraId()).getNombre(), styleNormal);
				this.setCellString(row, 8, aranceles
						.get(getArancel(inscripcion.getPersonaId(), chequera.getArancelTipoId())).getDescripcion(),
						styleNormal);
				this.setCellLong(row, 9, chequera.getChequeraSerieId(), styleNormal);
				this.setCellInteger(row, 10, chequera.getCursoId(), styleNormal);
			}
		}

		for (Integer column = 0; column < sheet.getRow(2).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		try {
			File file = new File(filename);
			FileOutputStream output = new FileOutputStream(file);
			book.write(output);
			output.flush();
			output.close();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	public String generateInscriptoCurso(Integer lectivoId) {
		String path = env.getProperty("path.files");

		String filename = path + "inscriptos." + lectivoId + ".xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle styleNormal = book.createCellStyle();
		Font fontNormal = book.createFont();
		fontNormal.setBold(false);
		styleNormal.setFont(fontNormal);

		CellStyle styleBold = book.createCellStyle();
		Font fontBold = book.createFont();
		fontBold.setBold(true);
		styleBold.setFont(fontBold);

		CellStyle styleDate = book.createCellStyle();
		styleDate.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));

		Sheet sheet = book.createSheet("Inscriptos");
		Row row = null;
		Lectivo lectivo = null;
		try {
			lectivo = lectivoService.findByLectivoId(lectivoId);
		} catch (EjercicioException e) {
			lectivo = new Lectivo();
		}

		Map<Integer, Geografica> geograficas = geograficaService.findAll().stream()
				.collect(Collectors.toMap(Geografica::getGeograficaId, geografica -> geografica));

		Integer fila = -1;
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, lectivo.getNombre(), styleBold);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Facultad", styleBold);
		this.setCellString(row, 1, "Sede", styleBold);
		this.setCellString(row, 2, "Carrera", styleBold);
		this.setCellString(row, 3, "Curso", styleBold);
		this.setCellString(row, 4, "Provisoria", styleBold);
		this.setCellString(row, 5, "Cantidad", styleBold);
		this.setCellString(row, 6, "Bajas", styleBold);
		this.setCellString(row, 7, "Egresos", styleBold);

		for (Facultad facultad : facultadService.findFacultades()) {
			Map<String, PlanFacultad> planes = planFacultadConsumer
					.findAll(facultad.getApiserver(), facultad.getApiport()).stream()
					.collect(Collectors.toMap(PlanFacultad::getPlanKey, plan -> plan));
			Map<String, CarreraFacultad> carreras = carreraFacultadConsumer
					.findAll(facultad.getApiserver(), facultad.getApiport()).stream()
					.collect(Collectors.toMap(CarreraFacultad::getCarreraKey, carrera -> carrera));
			for (InscriptoCursoFacultad curso : inscriptoCursoFacultadConsumer.findAllByLectivo(facultad.getApiserver(),
					facultad.getApiport(), facultad.getFacultadId(), lectivo.getLectivoId())) {
				row = sheet.createRow(++fila);
				this.setCellString(row, 0, facultad.getNombre(), styleNormal);
				if (geograficas.containsKey(curso.getGeograficaId())) {
					this.setCellString(row, 1, geograficas.get(curso.getGeograficaId()).getNombre(), styleNormal);
				}
				if (carreras.containsKey(curso.getCarreraKey())) {
					this.setCellString(row, 2, planes.get(curso.getPlanKey()).getNombre() + " / "
							+ carreras.get(curso.getCarreraKey()).getNombre(), styleNormal);
				}

				this.setCellInteger(row, 3, curso.getCurso(), styleNormal);
				this.setCellString(row, 4, curso.getProvisoria() == 1 ? "Sí" : "", styleNormal);
				this.setCellInteger(row, 5, curso.getCantidad(), styleNormal);
				this.setCellInteger(row, 6, curso.getBajas(), styleNormal);
				this.setCellInteger(row, 7, curso.getEgresos(), styleNormal);
			}
		}

		for (Integer column = 0; column < sheet.getRow(1).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		try {
			File file = new File(filename);
			FileOutputStream output = new FileOutputStream(file);
			book.write(output);
			output.flush();
			output.close();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	public String generateEjercicioIngreso(Integer ejercicioId) {
		String path = env.getProperty("path.files");

		String filename = path + "ingresos." + (int) (ejercicioId + 2011) + ".xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle styleNormal = book.createCellStyle();
		Font fontNormal = book.createFont();
		fontNormal.setBold(false);
		styleNormal.setFont(fontNormal);

		CellStyle style_normal_red = book.createCellStyle();
		style_normal_red.setFillBackgroundColor(IndexedColors.RED.getIndex());
		style_normal_red.setFillPattern(FillPatternType.LESS_DOTS);
		Font font_normal_red = book.createFont();
		font_normal_red.setBold(false);
		style_normal_red.setFont(font_normal_red);

		CellStyle styleBold = book.createCellStyle();
		Font fontBold = book.createFont();
		fontBold.setBold(true);
		styleBold.setFont(fontBold);

		CellStyle styleDate = book.createCellStyle();
		styleDate.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));

		Sheet sheet = book.createSheet("Ingresos");
		Row row = null;

		Integer fila = -1;
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Fecha", styleBold);
		this.setCellString(row, 1, "Pago", styleBold);
		this.setCellString(row, 2, "Asiento", styleBold);
		this.setCellString(row, 3, "Debe", styleBold);
		this.setCellString(row, 4, "Haber", styleBold);

		Map<Integer, TipoPago> tipos = tipoPagoService.findAll().stream()
				.collect(Collectors.toMap(TipoPago::getTipoPagoId, tipo -> tipo));

		Ejercicio ejercicio = ejercicioService.findByEjercicioId(ejercicioId);
		OffsetDateTime desde = ejercicio.getFechaInicio().withOffsetSameInstant(ZoneOffset.UTC);
		OffsetDateTime hasta = ejercicio.getFechaFinal().withOffsetSameInstant(ZoneOffset.UTC);
		for (OffsetDateTime fecha = desde; fecha.compareTo(hasta) <= 0; fecha = fecha.plusDays(1)) {
			for (TipoPagoFecha pago : tipoPagoFechaService.findAllByFechaAcreditacion(fecha)) {
				row = sheet.createRow(++fila);
				// Sumo 1 día para mostrar bien la fecha
				this.setCellOffsetDateTime(row, 0, fecha.plusDays(1), styleDate);
				this.setCellString(row, 1, tipos.get(pago.getTipoPagoId()).getNombre(), styleNormal);
				try {
					IngresoAsiento asiento = ingresoAsientoService.findByUnique(fecha, pago.getTipoPagoId());
					this.setCellInteger(row, 2, asiento.getOrdenContable(), styleNormal);
					BigDecimal debe = BigDecimal.ZERO;
					BigDecimal haber = BigDecimal.ZERO;
					for (CuentaMovimiento movimiento : cuentaMovimientoService
							.findAllByAsiento(asiento.getFechaContable(), asiento.getOrdenContable(), 0, 2)) {
						if (movimiento.getDebita() == 1) {
							debe = debe.add(movimiento.getImporte()).setScale(2, RoundingMode.HALF_UP);
						} else {
							haber = haber.add(movimiento.getImporte()).setScale(2, RoundingMode.HALF_UP);
						}
					}
					this.setCellBigDecimal(row, 3, debe, styleNormal);
					this.setCellBigDecimal(row, 4, haber, styleNormal);
					if (debe.compareTo(haber) != 0) {
						this.setCellBigDecimal(row, 4, haber, style_normal_red);
					}
				} catch (Exception e) {

				}
			}
		}

		for (Integer column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		try {
			File file = new File(filename);
			FileOutputStream output = new FileOutputStream(file);
			book.write(output);
			output.flush();
			output.close();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	@Transactional
	public String generateSuspendido(Integer facultadId, Integer geograficaId) {
		String path = env.getProperty("path.files");

		String filename = path + "suspendidos." + facultadId + "." + geograficaId + ".xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle styleNormal = book.createCellStyle();
		Font fontNormal = book.createFont();
		fontNormal.setBold(false);
		styleNormal.setFont(fontNormal);

		CellStyle style_normal_red = book.createCellStyle();
		style_normal_red.setFillBackgroundColor(IndexedColors.RED.getIndex());
		style_normal_red.setFillPattern(FillPatternType.LESS_DOTS);
		Font font_normal_red = book.createFont();
		font_normal_red.setBold(false);
		style_normal_red.setFont(font_normal_red);

		CellStyle styleBold = book.createCellStyle();
		Font fontBold = book.createFont();
		fontBold.setBold(true);
		styleBold.setFont(fontBold);

		CellStyle styleDate = book.createCellStyle();
		styleDate.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));

		Sheet sheet = book.createSheet("Suspendidos");
		Row row = null;

		Integer fila = -1;
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Documento", styleBold);
		this.setCellString(row, 1, "Apellido, Nombre", styleBold);
		this.setCellString(row, 2, "Correo Personal", styleBold);
		this.setCellString(row, 3, "Correo Institucional", styleBold);
		this.setCellString(row, 4, "Telefono", styleBold);
		this.setCellString(row, 5, "Móvil", styleBold);

		List<PersonaSuspendido> suspendidos = personaSuspendidoService.findAllBySede(facultadId, geograficaId);
		List<String> keys = suspendidos.stream().map(suspendido -> suspendido.personaKey())
				.collect(Collectors.toList());
		Map<String, PersonaKey> personas = personaKeyService.findAllByUnifiedIn(keys, null).stream().collect(
				Collectors.toMap(PersonaKey::getUnified, Function.identity(), (persona, replacement) -> persona));
		Map<String, DomicilioKey> domicilios = domicilioKeyService.findAllByUnifiedIn(keys).stream().collect(Collectors
				.toMap(DomicilioKey::getUnified, Function.identity(), (domicilio, replacemente) -> domicilio));
		Map<Integer, Lectivo> lectivos = lectivoService.findAll().stream()
				.collect(Collectors.toMap(Lectivo::getLectivoId, lectivo -> lectivo));
		Map<Integer, Facultad> facultades = facultadService.findAll().stream()
				.collect(Collectors.toMap(Facultad::getFacultadId, facultad -> facultad));
		Map<Integer, TipoChequera> tipos = tipoChequeraService.findAll().stream()
				.collect(Collectors.toMap(TipoChequera::getTipoChequeraId, tipo -> tipo));

		for (PersonaSuspendido suspendido : suspendidos) {
			row = sheet.createRow(++fila);
			this.setCellBigDecimal(row, 0, suspendido.getPersonaId(), styleNormal);
			if (personas.containsKey(suspendido.personaKey())) {
				this.setCellString(row, 1, personas.get(suspendido.personaKey()).getApellidoNombre(), styleBold);
			}
			if (domicilios.containsKey(suspendido.personaKey())) {
				DomicilioKey domicilio = domicilios.get(suspendido.personaKey());
				this.setCellString(row, 2, domicilio.getEmailPersonal(), styleNormal);
				this.setCellString(row, 3, domicilio.getEmailInstitucional(), styleNormal);
				this.setCellString(row, 4, domicilio.getTelefono(), styleNormal);
				this.setCellString(row, 5, domicilio.getMovil(), styleNormal);
			}
			// Lista la deuda
			Boolean deudor = false;
			for (ChequeraSerie serie : chequeraSerieService.findAllByPersona(suspendido.getPersonaId(),
					suspendido.getDocumentoId())) {
				DeudaChequera deuda = chequeraCuotaService.calculateDeuda(serie.getFacultadId(),
						serie.getTipoChequeraId(), serie.getChequeraSerieId());
				if (deuda.getCuotas() > 0) {
					deudor = true;
					row = sheet.createRow(++fila);
					this.setCellString(row, 1, lectivos.get(serie.getLectivoId()).getNombre(), styleNormal);
					this.setCellString(row, 2, facultades.get(serie.getFacultadId()).getNombre(), styleNormal);
					this.setCellString(row, 3, tipos.get(serie.getTipoChequeraId()).getNombre(), styleNormal);
					this.setCellString(row, 4, MessageFormat.format("Chequera: {0}/{1}/{2}", serie.getFacultadId(),
							serie.getTipoChequeraId(), serie.getChequeraSerieId()), styleNormal);
					this.setCellString(row, 5,
							MessageFormat.format("Deuda: {0}/{1}", deuda.getCuotas(), deuda.getDeuda()), styleNormal);
				}
			}
			if (!deudor) {
				personaSuspendidoService.delete(suspendido.getPersonaSuspendidoId());
				fila -= 2;
			}
			++fila;
		}

		for (Integer column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		try {
			File file = new File(filename);
			FileOutputStream output = new FileOutputStream(file);
			book.write(output);
			output.flush();
			output.close();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	public String generateContratados(Integer anho, Integer mes) {
		String path = env.getProperty("path.files");

		String filename = path + "contratados." + anho + "." + mes + ".xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle styleNormal = book.createCellStyle();
		Font fontNormal = book.createFont();
		fontNormal.setBold(false);
		styleNormal.setFont(fontNormal);

		CellStyle styleBold = book.createCellStyle();
		Font fontBold = book.createFont();
		fontBold.setBold(true);
		styleBold.setFont(fontBold);

		CellStyle styleDate = book.createCellStyle();
		styleDate.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));

		Sheet sheet = book.createSheet("Contratados");
		Row row = null;

		Integer fila = -1;
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Documento", styleBold);
		this.setCellString(row, 1, "Apellido, Nombre", styleBold);
		this.setCellString(row, 2, "CUIL", styleBold);
		this.setCellString(row, 3, "Facultad", styleBold);
		this.setCellString(row, 4, "Sede", styleBold);
		this.setCellString(row, 5, "Desde", styleBold);
		this.setCellString(row, 6, "Hasta", styleBold);

		for (ContratoPeriodo contratoPeriodo : contratoPeriodoService.findAllByPeriodo(anho, mes)) {
			row = sheet.createRow(++fila);
			this.setCellBigDecimal(row, 0, contratoPeriodo.getContrato().getContratado().getPersona().getPersonaId(),
					styleNormal);
			this.setCellString(row, 1, contratoPeriodo.getContrato().getContratado().getPersona().getApellidoNombre(),
					styleNormal);
			this.setCellString(row, 2, contratoPeriodo.getContrato().getContratado().getPersona().getCuit(),
					styleNormal);
			this.setCellString(row, 3, contratoPeriodo.getContrato().getFacultad().getNombre(), styleNormal);
			this.setCellString(row, 4, contratoPeriodo.getContrato().getGeografica().getNombre(), styleNormal);
			this.setCellOffsetDateTime(row, 5, contratoPeriodo.getContrato().getDesde(), styleDate);
			this.setCellOffsetDateTime(row, 6, contratoPeriodo.getContrato().getHasta(), styleDate);
		}

		for (Integer column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		try {
			File file = new File(filename);
			FileOutputStream output = new FileOutputStream(file);
			book.write(output);
			output.flush();
			output.close();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	private Integer getArancel(BigDecimal personaId, Integer arancelTipoId) {
		BigDecimal test[] = new BigDecimal[] { new BigDecimal(23693840), new BigDecimal(40596925),
				new BigDecimal(41699055), new BigDecimal(41868947) };
		for (BigDecimal value : test) {
			if (value.compareTo(personaId) == 0)
				return 1;
		}
		return arancelTipoId;
	}

	private void setCellOffsetDateTime(Row row, int column, OffsetDateTime value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(new Date(value.toInstant().toEpochMilli()));
		cell.setCellStyle(style);
	}

	private void setCellLong(Row row, int column, Long value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private void setCellBigDecimal(Row row, int column, BigDecimal value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value.doubleValue());
		cell.setCellStyle(style);
	}

	private void setCellString(Row row, int column, String value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private void setCellInteger(Row row, int column, Integer value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

}

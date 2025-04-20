/**
 *
 */
package um.tesoreria.core.service.facade;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import um.tesoreria.core.exception.EjercicioException;
import um.tesoreria.core.extern.consumer.BajaFacultadConsumer;
import um.tesoreria.core.extern.consumer.CarreraFacultadConsumer;
import um.tesoreria.core.extern.consumer.InscripcionFacultadConsumer;
import um.tesoreria.core.extern.consumer.PlanFacultadConsumer;
import um.tesoreria.core.extern.consumer.PreTurnoFacultadConsumer;
import um.tesoreria.core.extern.consumer.view.InscriptoCursoFacultadConsumer;
import um.tesoreria.core.extern.consumer.view.LegajoKeyFacultadConsumer;
import um.tesoreria.core.extern.consumer.view.PersonaKeyFacultadConsumer;
import um.tesoreria.core.extern.consumer.view.PreunivCarreraFacultadConsumer;
import um.tesoreria.core.extern.consumer.view.PreunivMatricResumenFacultadConsumer;
import um.tesoreria.core.extern.consumer.view.PreunivResumenFacultadConsumer;
import um.tesoreria.core.extern.exception.BajaFacultadNotFoundException;
import um.tesoreria.core.extern.model.kotlin.*;
import um.tesoreria.core.extern.model.view.InscriptoCursoFacultad;
import um.tesoreria.core.extern.model.view.LegajoKeyFacultad;
import um.tesoreria.core.extern.model.view.PersonaKeyFacultad;
import um.tesoreria.core.extern.model.view.PreunivCarreraFacultad;
import um.tesoreria.core.extern.model.view.PreunivMatricResumenFacultad;
import um.tesoreria.core.extern.model.view.PreunivResumenFacultad;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.kotlin.model.view.FacturaPendiente;
import um.tesoreria.core.model.*;
import um.tesoreria.core.model.dto.DeudaChequera;
import um.tesoreria.core.model.view.CarreraKey;
import um.tesoreria.core.model.view.ChequeraPreuniv;
import um.tesoreria.core.model.view.DomicilioKey;
import um.tesoreria.core.model.view.IngresoPeriodo;
import um.tesoreria.core.model.view.LegajoKey;
import um.tesoreria.core.model.view.PersonaKey;
import um.tesoreria.core.model.view.TipoPagoFechaAcreditacion;
import um.tesoreria.core.service.ArancelTipoService;
import um.tesoreria.core.service.BajaService;
import um.tesoreria.core.service.CarreraService;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.service.ChequeraSerieService;
import um.tesoreria.core.service.ContratoPeriodoService;
import um.tesoreria.core.service.CuentaMovimientoService;
import um.tesoreria.core.service.EjercicioService;
import um.tesoreria.core.service.FacultadService;
import um.tesoreria.core.service.GeograficaService;
import um.tesoreria.core.service.IngresoAsientoService;
import um.tesoreria.core.service.LectivoService;
import um.tesoreria.core.service.LegajoService;
import um.tesoreria.core.service.PersonaSuspendidoService;
import um.tesoreria.core.service.PlanService;
import um.tesoreria.core.service.ProveedorMovimientoService;
import um.tesoreria.core.service.TipoChequeraService;
import um.tesoreria.core.service.TipoPagoService;
import um.tesoreria.core.service.view.*;
import um.tesoreria.core.model.PersonaSuspendido;
import um.tesoreria.core.service.*;

/**
 * @author daniel
 */
@Service
@Slf4j
public class SheetService {

    private final FacultadService facultadService;
    private final TipoPagoService tipoPagoService;
    private final GeograficaService geograficaService;
    private final IngresoPeriodoService ingresoPeriodoService;
    private final ChequeraSerieService chequeraSerieService;
    private final PersonaKeyService personaKeyService;
    private final ArancelTipoService arancelTipoService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final LegajoKeyService legajoKeyService;
    private final CarreraKeyService carreraKeyService;
    private final DomicilioKeyService domicilioKeyService;
    private final EjercicioService ejercicioService;
    private final ProveedorMovimientoService proveedorMovimientoService;
    private final SincronizeService sincronizeService;
    private final LectivoService lectivoService;
    private final PlanService planService;
    private final CarreraService carreraService;
    private final ChequeraPreunivService chequeraPreunivService;
    private final TipoChequeraService tipoChequeraService;
    private final PreunivResumenFacultadConsumer preunivResumenFacultadConsumer;
    private final PreunivMatricResumenFacultadConsumer preunivMatricResumenFacultadConsumer;
    private final PreTurnoFacultadConsumer preTurnoFacultadConsumer;
    private final PreunivCarreraFacultadConsumer preunivCarreraFacultadConsumer;
    private final PersonaKeyFacultadConsumer personaKeyFacultadConsumer;
    private final LegajoKeyFacultadConsumer legajoKeyFacultadConsumer;
    private final InscripcionFacultadConsumer inscripcionFacultadConsumer;
    private final CarreraFacultadConsumer carreraFacultadConsumer;
    private final PlanFacultadConsumer planFacultadConsumer;
    private final InscriptoCursoFacultadConsumer inscriptoCursoFacultadConsumer;
    private final TipoPagoFechaAcreditacionService tipoPagoFechaAcreditacionService;
    private final IngresoAsientoService ingresoAsientoService;
    private final CuentaMovimientoService cuentaMovimientoService;
    private final Environment environment;
    private final PersonaSuspendidoService personaSuspendidoService;
    private final ContratoPeriodoService contratoPeriodoService;
    private final BajaService bajaService;
    private final BajaFacultadConsumer bajaFacultadConsumer;
    private final LegajoService legajoService;
    private final FacturacionElectronicaService facturacionElectronicaService;
    private final ProveedorService proveedorService;
    private final FacturaPendienteService facturaPendienteService;

    public SheetService(FacultadService facultadService, TipoPagoService tipoPagoService, GeograficaService geograficaService, IngresoPeriodoService ingresoPeriodoService, ChequeraSerieService chequeraSerieService, PersonaKeyService personaKeyService, ArancelTipoService arancelTipoService, ChequeraCuotaService chequeraCuotaService, LegajoKeyService legajoKeyService, CarreraKeyService carreraKeyService, DomicilioKeyService domicilioKeyService, EjercicioService ejercicioService, ProveedorMovimientoService proveedorMovimientoService, SincronizeService sincronizeService, LectivoService lectivoService, PlanService planService, CarreraService carreraService, ChequeraPreunivService chequeraPreunivService, TipoChequeraService tipoChequeraService, PreunivResumenFacultadConsumer preunivResumenFacultadConsumer, PreunivMatricResumenFacultadConsumer preunivMatricResumenFacultadConsumer, PreTurnoFacultadConsumer preTurnoFacultadConsumer, PreunivCarreraFacultadConsumer preunivCarreraFacultadConsumer, PersonaKeyFacultadConsumer personaKeyFacultadConsumer, LegajoKeyFacultadConsumer legajoKeyFacultadConsumer, InscripcionFacultadConsumer inscripcionFacultadConsumer, CarreraFacultadConsumer carreraFacultadConsumer, PlanFacultadConsumer planFacultadConsumer, InscriptoCursoFacultadConsumer inscriptoCursoFacultadConsumer, TipoPagoFechaAcreditacionService tipoPagoFechaAcreditacionService, IngresoAsientoService ingresoAsientoService, CuentaMovimientoService cuentaMovimientoService, PersonaSuspendidoService personaSuspendidoService, ContratoPeriodoService contratoPeriodoService, BajaService bajaService, BajaFacultadConsumer bajaFacultadConsumer, LegajoService legajoService, FacturacionElectronicaService facturacionElectronicaService, ProveedorService proveedorService, Environment environment, FacturaPendienteService facturaPendienteService) {
        this.facultadService = facultadService;
        this.tipoPagoService = tipoPagoService;
        this.geograficaService = geograficaService;
        this.ingresoPeriodoService = ingresoPeriodoService;
        this.chequeraSerieService = chequeraSerieService;
        this.personaKeyService = personaKeyService;
        this.arancelTipoService = arancelTipoService;
        this.chequeraCuotaService = chequeraCuotaService;
        this.legajoKeyService = legajoKeyService;
        this.carreraKeyService = carreraKeyService;
        this.domicilioKeyService = domicilioKeyService;
        this.ejercicioService = ejercicioService;
        this.proveedorMovimientoService = proveedorMovimientoService;
        this.sincronizeService = sincronizeService;
        this.lectivoService = lectivoService;
        this.planService = planService;
        this.carreraService = carreraService;
        this.chequeraPreunivService = chequeraPreunivService;
        this.tipoChequeraService = tipoChequeraService;
        this.preunivResumenFacultadConsumer = preunivResumenFacultadConsumer;
        this.preunivMatricResumenFacultadConsumer = preunivMatricResumenFacultadConsumer;
        this.preTurnoFacultadConsumer = preTurnoFacultadConsumer;
        this.preunivCarreraFacultadConsumer = preunivCarreraFacultadConsumer;
        this.personaKeyFacultadConsumer = personaKeyFacultadConsumer;
        this.legajoKeyFacultadConsumer = legajoKeyFacultadConsumer;
        this.inscripcionFacultadConsumer = inscripcionFacultadConsumer;
        this.carreraFacultadConsumer = carreraFacultadConsumer;
        this.planFacultadConsumer = planFacultadConsumer;
        this.inscriptoCursoFacultadConsumer = inscriptoCursoFacultadConsumer;
        this.tipoPagoFechaAcreditacionService = tipoPagoFechaAcreditacionService;
        this.ingresoAsientoService = ingresoAsientoService;
        this.cuentaMovimientoService = cuentaMovimientoService;
        this.personaSuspendidoService = personaSuspendidoService;
        this.contratoPeriodoService = contratoPeriodoService;
        this.bajaService = bajaService;
        this.bajaFacultadConsumer = bajaFacultadConsumer;
        this.legajoService = legajoService;
        this.facturacionElectronicaService = facturacionElectronicaService;
        this.proveedorService = proveedorService;
        this.environment = environment;
        this.facturaPendienteService = facturaPendienteService;
    }

    public String generateIngresos(Integer anho, Integer mes) {
        String path = environment.getProperty("path.files");

        String filename = path + "ingresos" + anho + String.format("%02d", mes) + ".xlsx";

        Workbook book = null;
        CellStyle styleNormal = null;
        CellStyle styleBold = null;
        Sheet sheet = makeWorkbook(book, styleNormal, styleBold, "Ingresos");

        Row row = null;
        int fila = 0;
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

        Map<Integer, Facultad> facultades = facultadService.findAll().stream().collect(Collectors.toMap(Facultad::getFacultadId, facultad -> facultad));

        Map<Integer, Geografica> geograficas = geograficaService.findAll().stream().collect(Collectors.toMap(Geografica::getGeograficaId, geografica -> geografica));

        Map<Integer, TipoPago> tipos = tipoPagoService.findAll().stream().collect(Collectors.toMap(TipoPago::getTipoPagoId, tipoPago -> tipoPago));

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

        for (int column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error escribiendo ingresos");
        }
        return filename;
    }

    public String generateDeuda(Integer facultadId, Integer lectivoId, Boolean soloDeudores, List<Integer> tipoChequeraIds) {

        String path = environment.getProperty("path.files");
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
        Map<Integer, Lectivo> lectivos = lectivoService.findAll().stream().collect(Collectors.toMap(Lectivo::getLectivoId, ciclo -> ciclo));
        // Sincroniza Carreras
        sincronizeService.sincronizeCarrera(facultadId);

        Sheet sheet = book.createSheet("Deuda");
        Row row = null;
        int fila = -1;
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

        Map<String, CarreraKey> carreras = carreraKeyService.findAllByFacultadId(facultadId).stream().collect(Collectors.toMap(CarreraKey::getUnified, carrera -> carrera));
        Map<Integer, TipoChequera> tipos = tipoChequeraService.findAll().stream().collect(Collectors.toMap(TipoChequera::getTipoChequeraId, tipo -> tipo));
        List<Legajo> legajosUpdate = new ArrayList<>();

        for (Integer tipochequeraId : tipoChequeraIds) {
            TipoChequera tipoChequera = new TipoChequera();
            Geografica geografica = new Geografica();
            if (tipos.containsKey(tipochequeraId)) {
                tipoChequera = tipos.get(tipochequeraId);
                if (tipoChequera.getGeografica() != null) {
                    geografica = tipoChequera.getGeografica();
                }
            }
            List<ChequeraSerie> chequeraList = chequeraSerieService.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId, lectivoId, tipochequeraId);
            Map<String, ChequeraSerie> chequeras = chequeraList.stream().collect(Collectors.toMap(ChequeraSerie::getPersonaKey, Function.identity(), (chequera, replacement) -> chequera));
            Map<Long, Baja> bajas = bajaService.findAllByChequeraIdIn(chequeraList.stream().map(ChequeraSerie::getChequeraId).collect(Collectors.toList())).stream().collect(Collectors.toMap(Baja::getChequeraId, Function.identity(), (baja, replacement) -> baja));
            List<String> keys = new ArrayList<>(chequeras.keySet());
            Map<String, LegajoKey> legajos = legajoKeyService.findAllByFacultadIdAndUnifiedIn(facultadId, keys).stream().collect(Collectors.toMap(LegajoKey::getUnified, Function.identity(), (legajo, replacement) -> legajo));
            Map<String, LegajoKeyFacultad> legajosFacultad = legajoKeyFacultadConsumer.findAllByFacultadAndKeys(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), keys).stream().collect(Collectors.toMap(LegajoKeyFacultad::getPersonakey, Function.identity(), (legajo, replacement) -> legajo));
            Map<String, DomicilioKey> domicilios = domicilioKeyService.findAllByUnifiedIn(keys).stream().collect(Collectors.toMap(DomicilioKey::getUnified, domicilio -> domicilio));

            for (PersonaKey persona : personaKeyService.findAllByUnifiedIn(keys, Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()))) {
                ChequeraSerie chequeraSerie = chequeras.get(persona.getUnified());
                DeudaChequera deudaChequera = chequeraCuotaService.calculateDeuda(facultadId, tipochequeraId, chequeraSerie.getChequeraSerieId());
                boolean show = true;
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
                        legajosUpdate.add(legajo = new Legajo(legajoId,
                                legajoFacultad.getPersonaId(),
                                legajoFacultad.getDocumentoId(),
                                legajoFacultad.getFacultadId(),
                                legajoFacultad.getNumerolegajo(),
                                legajoFacultad.getFecha(),
                                lectivoIdAdd,
                                legajoFacultad.getPlanId(),
                                legajoFacultad.getCarreraId(),
                                (byte) 1,
                                legajoFacultad.getGeograficaId(),
                                legajoFacultad.getContrasenha(),
                                legajoFacultad.getIntercambio(),
                                null));
                    }
                    ArancelTipo arancelTipo = chequeraSerie.getArancelTipo();
                    CarreraKey carrera = null;
                    if (legajo != null)
                        carrera = carreras.get(legajo.getFacultadId() + "." + legajo.getPlanId() + "." + legajo.getCarreraId());
                    DomicilioKey domicilio = domicilios.get(persona.getUnified());
                    row = sheet.createRow(++fila);
                    this.setCellInteger(row, 0, fila - 2, styleNormal);
                    this.setCellBigDecimal(row, 1, persona.getPersonaId(), styleNormal);
                    this.setCellString(row, 2, persona.getApellido() + ", " + persona.getNombre(), styleNormal);
                    this.setCellLong(row, 3, chequeraSerie.getChequeraSerieId(), styleNormal);
                    // Baja Tsoreria
                    if (bajas.containsKey(chequeraSerie.getChequeraId())) {
                        Baja baja = bajas.get(chequeraSerie.getChequeraId());
                        this.setCellOffsetDateTime(row, 4, Objects.requireNonNull(baja.getFecha()).withOffsetSameInstant(ZoneOffset.UTC), styleDate);
                    }
                    // Baja Facultad
                    BajaFacultad bajaFacultad = null;
                    try {
                        bajaFacultad = bajaFacultadConsumer.findByUnique(facultad.getApiserver(), facultad.getApiport(), facultadId, persona.getPersonaId(), persona.getDocumentoId(), lectivoId);
                    } catch (BajaFacultadNotFoundException e) {
                        log.debug("Error buscando facultad en otro servicio");
                    }
                    if (bajaFacultad != null) {
                        this.setCellOffsetDateTime(row, 5, Objects.requireNonNull(bajaFacultad.getFecha()).withOffsetSameInstant(ZoneOffset.UTC), styleDate);
                    }
                    this.setCellString(row, 6, geografica.getNombre(), styleNormal);
                    this.setCellString(row, 7, tipoChequera.getNombre(), styleNormal);
                    assert arancelTipo != null;
                    this.setCellString(row, 8, arancelTipo.getDescripcion(), styleNormal);
                    this.setCellOffsetDateTime(row, 9, Objects.requireNonNull(chequeraSerie.getFecha()).withOffsetSameInstant(ZoneOffset.UTC), styleDate);
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
        legajoService.saveAll(legajosUpdate);

        for (int column = 0; column < sheet.getRow(2).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error generando deuda");
        }
        return filename;
    }

    public String generateEjercicioOp(Integer ejercicioId) {
        String path = environment.getProperty("path.files");

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
            try {
                log.debug("Ejercicio -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(ejercicio));
            } catch (JsonProcessingException e) {
                log.debug("Sin Ejercicio");
            }
        } catch (EjercicioException e) {
            ejercicio = new Ejercicio();
        }
        int fila = -1;
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

        List<ProveedorMovimiento> ordenes = proveedorMovimientoService.findAllByComprobanteIdAndFechaComprobanteBetween(6, ejercicio.getFechaInicio(), ejercicio.getFechaFinal());
        Map<Long, ProveedorMovimiento> ordenes_map = ordenes.stream().collect(Collectors.toMap(ProveedorMovimiento::getNumeroComprobante, Function.identity(), (movimiento, replacement) -> movimiento));
        Map<Integer, Geografica> geograficas = geograficaService.findAll().stream().collect(Collectors.toMap(Geografica::getGeograficaId, Function.identity(), (geografica, replacement) -> geografica));
        long orden_minimo = ordenes.stream().mapToLong(ProveedorMovimiento::getNumeroComprobante).min().orElseThrow(NoSuchElementException::new);
        long orden_maximo = ordenes.stream().mapToLong(ProveedorMovimiento::getNumeroComprobante).max().orElseThrow(NoSuchElementException::new);

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

        for (int column = 0; column < sheet.getRow(1).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error  en la generación de deuda");
        }
        return filename;
    }

    public String generateEficienciaPre(Integer facultadId, Integer lectivoId) {
        String path = environment.getProperty("path.files");

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

        int fila = -1;
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

        Map<Integer, Geografica> geograficas = geograficaService.findAll().stream().collect(Collectors.toMap(Geografica::getGeograficaId, geografica -> geografica));

        // Matriculados
        Map<String, PreunivMatricResumenFacultad> matriculas = preunivMatricResumenFacultadConsumer.findAllByLectivo(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), lectivoId).stream().collect(Collectors.toMap(PreunivMatricResumenFacultad::getKey, matricula -> matricula));
        // Turnos
        Map<String, PreTurnoFacultad> turnos = preTurnoFacultadConsumer.findAllByLectivo(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), lectivoId).stream().collect(Collectors.toMap(PreTurnoFacultad::getKey, turno -> turno));
        // Sincronizar carreras
        sincronizeService.sincronizeCarrera(facultad.getFacultadId());

        Map<String, Plan> planes = planService.findAllByFacultadId(facultad.getFacultadId()).stream().collect(Collectors.toMap(Plan::getPlanKey, plan -> plan));
        Map<String, Carrera> carreras = carreraService.findAllByFacultadId(facultad.getFacultadId()).stream().collect(Collectors.toMap(Carrera::getCarreraKey, carrera -> carrera));

        Map<String, ChequeraPreuniv> chequerasPre = chequeraPreunivService.findAllByFacultadIdAndLectivoId(facultadId, lectivoId - 1).stream().collect(Collectors.toMap(ChequeraPreuniv::getPersonaKey, Function.identity(), (existing, chequera) -> chequera));

        for (PreunivResumenFacultad resumen : preunivResumenFacultadConsumer.findAllByLectivo(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), lectivoId)) {
            // Recupera lista de alumnos inscriptos en el turno
            List<PreunivCarreraFacultad> preInscriptosCarrera = preunivCarreraFacultadConsumer.findAllByCarrera(facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId, resumen.getGeograficaId(), resumen.getTurnoId(), resumen.getPlanId(), resumen.getCarreraId());
            List<String> keysFacultad = preInscriptosCarrera.stream().map(pre -> pre.getPersonaId() + "." + pre.getDocumentoId()).collect(Collectors.toList());
            Map<String, ChequeraPreuniv> chequerasPreInscriptos = chequeraPreunivService.findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonakeyIn(resumen.getFacultadId(), resumen.getLectivoId() - 1, resumen.getGeograficaId(), keysFacultad).stream().collect(Collectors.toMap(ChequeraPreuniv::getPersonaKey, Function.identity(), (existing, chequera) -> chequera));

            // Elimina de chequerasPre todas las chequeras de inscriptos
            chequerasPreInscriptos.keySet().forEach(chequerasPre::remove);

            // Fila
            row = sheet.createRow(++fila);
            this.setCellString(row, 0, facultad.getNombre(), styleNormal);
            if (geograficas.containsKey(resumen.getGeograficaId())) {
                Geografica geografica = geograficas.get(resumen.getGeograficaId());
                this.setCellString(row, 1, geografica.getNombre(), styleNormal);
            }
            if (carreras.containsKey(resumen.getFacultadId() + "." + resumen.getPlanId() + "." + resumen.getCarreraId())) {
                Plan plan = planes.get(resumen.getFacultadId() + "." + resumen.getPlanId());
                Carrera carrera = carreras.get(resumen.getFacultadId() + "." + resumen.getPlanId() + "." + resumen.getCarreraId());
                String nombreCarrera = "";
                if (plan != null) {
                    nombreCarrera += plan.getNombre();
                }
                if (carrera != null) {
                    nombreCarrera += " / " + carrera.getNombre();
                }
                this.setCellString(row, 2, nombreCarrera, styleBold);
            }
            if (turnos.containsKey(resumen.getFacultadId() + "." + resumen.getLectivoId() + "." + resumen.getGeograficaId() + "." + resumen.getTurnoId())) {
                PreTurnoFacultad turno = turnos.get(resumen.getFacultadId() + "." + resumen.getLectivoId() + "." + resumen.getGeograficaId() + "." + resumen.getTurnoId());
                this.setCellString(row, 3, turno.getNombre(), styleNormal);
            }
            this.setCellInteger(row, 4, resumen.getCantidad(), styleNormal);
            this.setCellInteger(row, 5, chequerasPreInscriptos.size(), styleNormal);
            int sinChequera = resumen.getCantidad() - chequerasPreInscriptos.size();
            this.setCellInteger(row, 6, 0, styleNormal);
            if (matriculas.containsKey(resumen.getKey())) {
                PreunivMatricResumenFacultad matricula = matriculas.get(resumen.getKey());
                this.setCellInteger(row, 6, matricula.getCantidad(), styleNormal);
            }
            // Calcula Deuda
            BigDecimal deuda = BigDecimal.ZERO;
            for (ChequeraPreuniv chequera : chequerasPreInscriptos.values()) {
                deuda = deuda.add(chequeraCuotaService.calculateDeuda(chequera.getFacultadId(), chequera.getTipoChequeraId(), chequera.getChequeraSerieId()).getDeuda());
            }
            this.setCellBigDecimal(row, 7, deuda, styleNormal);
            // Lista los alumnos sin chequera
            if (sinChequera > 0) {
                List<String> keys = new ArrayList<>();
                for (String key : keysFacultad)
                    if (!chequerasPreInscriptos.containsKey(key)) keys.add(key);
                for (PersonaKey persona : personaKeyService.findAllByUnifiedIn(keys, Sort.by("apellido").ascending())) {
                    row = sheet.createRow(++fila);
                    this.setCellString(row, 2, "(" + persona.getUnified() + ") - " + persona.getApellido() + ", " + persona.getNombre(), styleNormal);
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
            this.setCellString(row, 2, "(" + chequeraPreuniv.getPersonaKey() + ") - " + chequeraPreuniv.getPersona().getApellido() + ", " + chequeraPreuniv.getPersona().getNombre(), styleNormal);
            this.setCellString(row, 3, chequeraPreuniv.getChequera(), styleNormal);
            // Calcula Deuda
            BigDecimal deuda = chequeraCuotaService.calculateDeuda(chequeraPreuniv.getFacultadId(), chequeraPreuniv.getTipoChequeraId(), chequeraPreuniv.getChequeraSerieId()).getDeuda();
            this.setCellBigDecimal(row, 4, deuda, styleNormal);
        }

        for (int column = 0; column < sheet.getRow(1).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error generando eficiencia pre");
        }
        return filename;
    }

    public String generateComparativoPre(Integer facultadId, Integer lectivoId) {
        String path = environment.getProperty("path.files");

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

        int fila = -1;
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

        Map<Integer, ArancelTipo> aranceles = arancelTipoService.findAll().stream().collect(Collectors.toMap(ArancelTipo::getArancelTipoId, arancelTipo -> arancelTipo));
        Map<Integer, TipoChequera> tipos = tipoChequeraService.findAll().stream().collect(Collectors.toMap(TipoChequera::getTipoChequeraId, tipo -> tipo));
        Map<Integer, Geografica> geograficas = geograficaService.findAll().stream().collect(Collectors.toMap(Geografica::getGeograficaId, geografica -> geografica));
        Map<String, CarreraKey> carreras = carreraKeyService.findAllByFacultadId(facultad.getFacultadId()).stream().collect(Collectors.toMap(CarreraKey::getUnified, carrera -> carrera));
        Map<String, ChequeraPreuniv> chequeras = chequeraPreunivService.findAllByFacultadIdAndLectivoId(facultad.getFacultadId(), lectivoId - 1).stream().collect(Collectors.toMap(ChequeraPreuniv::getPersonaKey, Function.identity(), (chequera, replacement) -> chequera));
        List<String> keys = new ArrayList<>(chequeras.keySet());
        Map<String, PersonaKey> personas = personaKeyService.findAllByUnifiedIn(keys, null).stream().collect(Collectors.toMap(PersonaKey::getUnified, persona -> persona));
        Map<String, LegajoKey> legajos = legajoKeyService.findAllByFacultadIdAndUnifiedIn(facultad.getFacultadId(), keys).stream().collect(Collectors.toMap(LegajoKey::getUnified, legajo -> legajo));
        // Turnos
        Map<String, PreTurnoFacultad> turnos = preTurnoFacultadConsumer.findAllByLectivo(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), lectivoId).stream().collect(Collectors.toMap(PreTurnoFacultad::getKey, turno -> turno));
        // Inscriptos preuniversitario
        List<PreunivCarreraFacultad> preInscriptosCarrera = preunivCarreraFacultadConsumer.findAllByLectivo(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), lectivoId);
        Map<String, PreunivCarreraFacultad> preunivs_facultad = preInscriptosCarrera.stream().collect(Collectors.toMap(PreunivCarreraFacultad::getUnified, Function.identity(), (inscripcion, replacement) -> inscripcion));
        List<String> keysFacultad = preInscriptosCarrera.stream().map(pre -> pre.getPersonaId() + "." + pre.getDocumentoId()).collect(Collectors.toList());
        // Personas
        Map<String, PersonaKeyFacultad> personas_facultad = personaKeyFacultadConsumer.findAllByUnifieds(facultad.getApiserver(), facultad.getApiport(), keysFacultad).stream().collect(Collectors.toMap(PersonaKeyFacultad::getUnified, persona -> persona));

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
                sincronizeService.sincronizeCarreraAlumno(facultad.getFacultadId(), persona.getPersonaId(), persona.getDocumentoId());
                LegajoKey legajo = legajos.get(persona.getUnified());
                if (legajo != null)
                    carrera = carreras.get(legajo.getFacultadId() + "." + legajo.getPlanId() + "." + legajo.getCarreraId());
                row = sheet.createRow(++fila);
                this.setCellString(row, 0, chequera.getPersonaKey(), styleNormal);
                this.setCellString(row, 1, persona.getApellido() + ", " + persona.getNombre(), styleNormal);
                this.setCellLong(row, 2, chequera.getChequeraSerieId(), styleNormal);
                this.setCellString(row, 3, geografica.getNombre(), styleNormal);
                this.setCellString(row, 4, tipoChequera.getNombre(), styleNormal);
                this.setCellString(row, 5, arancelTipo.getDescripcion(), styleNormal);
                if (carrera != null) this.setCellString(row, 6, carrera.getNombre(), styleNormal);

                // Datos Facultad
                PreunivCarreraFacultad inscripcion = preunivs_facultad.get(chequera.getPersonaKey());
                PersonaKeyFacultad persona_facultad = personas_facultad.get(inscripcion.getUnified());
                this.setCellString(row, 7, chequera.getPersonaKey(), styleNormal);
                this.setCellString(row, 8, persona_facultad.getApellido() + ", " + persona_facultad.getNombre(), styleNormal);
                geografica = geograficas.get(inscripcion.getGeograficaId());
                this.setCellString(row, 9, geografica.getNombre(), styleNormal);
                PreTurnoFacultad turno = turnos.get(inscripcion.getFacultadId() + "." + inscripcion.getLectivoId() + "." + inscripcion.getGeograficaId() + "." + inscripcion.getTurnoId());
                this.setCellString(row, 10, turno.getNombre(), styleNormal);
                carrera = carreras.get(inscripcion.getFacultadId() + "." + inscripcion.getPlanId() + "." + inscripcion.getCarreraId());
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
                    carrera = carreras.get(legajo.getFacultadId() + "." + legajo.getPlanId() + "." + legajo.getCarreraId());
                row = sheet.createRow(++fila);
                this.setCellString(row, 0, chequera.getPersonaKey(), styleNormal);
                this.setCellString(row, 1, persona.getApellido() + ", " + persona.getNombre(), styleNormal);
                this.setCellLong(row, 2, chequera.getChequeraSerieId(), styleNormal);
                this.setCellString(row, 3, geografica.getNombre(), styleNormal);
                this.setCellString(row, 4, tipoChequera.getNombre(), styleNormal);
                this.setCellString(row, 5, arancelTipo.getDescripcion(), styleNormal);
                if (carrera != null) this.setCellString(row, 6, carrera.getNombre(), styleNormal);
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
                this.setCellString(row, 8, persona_facultad.getApellido() + ", " + persona_facultad.getNombre(), styleNormal);
                Geografica geografica = geograficas.get(inscripcion.getGeograficaId());
                this.setCellString(row, 9, geografica.getNombre(), styleNormal);
                PreTurnoFacultad turno = turnos.get(inscripcion.getFacultadId() + "." + inscripcion.getLectivoId() + "." + inscripcion.getGeograficaId() + "." + inscripcion.getTurnoId());
                this.setCellString(row, 10, turno.getNombre(), styleNormal);
                CarreraKey carrera = carreras.get(inscripcion.getFacultadId() + "." + inscripcion.getPlanId() + "." + inscripcion.getCarreraId());
                this.setCellString(row, 11, carrera.getNombre(), styleNormal);
            }
        }

        for (int column = 0; column < sheet.getRow(3).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error generando comparativo Pre");
        }
        return filename;
    }

    public String generateMatriculados(Integer facultadId, Integer lectivoId) {
        String path = environment.getProperty("path.files");

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

        int fila = -1;
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

        List<InscripcionFacultad> inscriptos = inscripcionFacultadConsumer.findAllByLectivo(facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId);
        List<String> unifieds = inscriptos.stream().map(InscripcionFacultad::getPersonaKey).collect(Collectors.toList());
        Map<String, PersonaKeyFacultad> personas = personaKeyFacultadConsumer.findAllByUnifieds(facultad.getApiserver(), facultad.getApiport(), unifieds).stream().collect(Collectors.toMap(PersonaKeyFacultad::getUnified, Function.identity(), (persona, replacement) -> persona));
        Map<String, LegajoKeyFacultad> legajos = legajoKeyFacultadConsumer.findAllByFacultadAndKeys(facultad.getApiserver(), facultad.getApiport(), facultadId, unifieds).stream().collect(Collectors.toMap(LegajoKeyFacultad::getLegajoKey, Function.identity(), (legajo, replacemente) -> legajo));
        Map<Integer, Geografica> geograficas = geograficaService.findAll().stream().collect(Collectors.toMap(Geografica::getGeograficaId, geografica -> geografica));
        Map<String, PlanFacultad> planes = planFacultadConsumer.findAll(facultad.getApiserver(), facultad.getApiport()).stream().collect(Collectors.toMap(PlanFacultad::getPlanKey, plan -> plan));
        Map<String, CarreraFacultad> carreras = carreraFacultadConsumer.findAll(facultad.getApiserver(), facultad.getApiport()).stream().collect(Collectors.toMap(CarreraFacultad::getCarreraKey, carrera -> carrera));
        Map<String, ChequeraSerie> chequeras = chequeraSerieService.findAllByLectivoIdAndFacultadId(lectivoId, facultadId).stream().collect(Collectors.toMap(ChequeraSerie::getFacultadKey, Function.identity(), (chequera, replacement) -> chequera));
        Map<Integer, TipoChequera> tipos = tipoChequeraService.findAll().stream().collect(Collectors.toMap(TipoChequera::getTipoChequeraId, tipo -> tipo));
        Map<Integer, ArancelTipo> aranceles = arancelTipoService.findAll().stream().collect(Collectors.toMap(ArancelTipo::getArancelTipoId, arancelTipo -> arancelTipo));
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
                this.setCellString(row, 3, planes.get(inscripcion.getPlanKey()).getNombre() + " / " + carreras.get(inscripcion.getCarreraKey()).getNombre(), styleNormal);
            }
            this.setCellOffsetDateTime(row, 4, inscripcion.getFecha(), styleDate);

            if (legajos.containsKey(inscripcion.getLegajoKey())) {
                this.setCellString(row, 5, legajos.get(inscripcion.getLegajoKey()).getIntercambio() == 0 ? "No" : "Si", styleNormal);
            }
            this.setCellInteger(row, 6, inscripcion.getCurso(), styleNormal);

            String facultadKey = facultadId + "." + lectivoId + "." + inscripcion.getGeograficaId() + "." + inscripcion.getPersonaKey();
            if (chequeras.containsKey(facultadKey)) {
                ChequeraSerie chequera = chequeras.get(facultadKey);
                this.setCellString(row, 7, tipos.get(chequera.getTipoChequeraId()).getNombre(), styleNormal);
                this.setCellString(row, 8, aranceles.get(getArancel(inscripcion.getPersonaId(), chequera.getArancelTipoId())).getDescripcion(), styleNormal);
                this.setCellLong(row, 9, chequera.getChequeraSerieId(), styleNormal);
                this.setCellInteger(row, 10, chequera.getCursoId(), styleNormal);
            }
        }

        for (int column = 0; column < sheet.getRow(2).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error generando matriculados");
        }
        return filename;
    }

    public String generateInscriptoCurso(Integer lectivoId) {
        String path = environment.getProperty("path.files");

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

        Map<Integer, Geografica> geograficas = geograficaService.findAll().stream().collect(Collectors.toMap(Geografica::getGeograficaId, geografica -> geografica));

        int fila = -1;
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
            Map<String, PlanFacultad> planes = planFacultadConsumer.findAll(facultad.getApiserver(), facultad.getApiport()).stream().collect(Collectors.toMap(PlanFacultad::getPlanKey, plan -> plan));
            Map<String, CarreraFacultad> carreras = carreraFacultadConsumer.findAll(facultad.getApiserver(), facultad.getApiport()).stream().collect(Collectors.toMap(CarreraFacultad::getCarreraKey, carrera -> carrera));
            for (InscriptoCursoFacultad curso : inscriptoCursoFacultadConsumer.findAllByLectivo(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), lectivo.getLectivoId())) {
                row = sheet.createRow(++fila);
                this.setCellString(row, 0, facultad.getNombre(), styleNormal);
                if (geograficas.containsKey(curso.getGeograficaId())) {
                    this.setCellString(row, 1, geograficas.get(curso.getGeograficaId()).getNombre(), styleNormal);
                }
                if (carreras.containsKey(curso.getCarreraKey())) {
                    this.setCellString(row, 2, planes.get(curso.getPlanKey()).getNombre() + " / " + carreras.get(curso.getCarreraKey()).getNombre(), styleNormal);
                }

                this.setCellInteger(row, 3, curso.getCurso(), styleNormal);
                this.setCellString(row, 4, curso.getProvisoria() == 1 ? "Sí" : "", styleNormal);
                this.setCellInteger(row, 5, curso.getCantidad(), styleNormal);
                this.setCellInteger(row, 6, curso.getBajas(), styleNormal);
                this.setCellInteger(row, 7, curso.getEgresos(), styleNormal);
            }
        }

        for (int column = 0; column < sheet.getRow(1).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error generando Inscripto Curso");
        }
        return filename;
    }

    public String generateEjercicioIngreso(Integer ejercicioId) {
        String path = environment.getProperty("path.files");

        String filename = path + "ingresos." + (ejercicioId + 2011) + ".xlsx";

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

        int fila = -1;
        row = sheet.createRow(++fila);
        this.setCellString(row, 0, "Fecha", styleBold);
        this.setCellString(row, 1, "Pago", styleBold);
        this.setCellString(row, 2, "Asiento", styleBold);
        this.setCellString(row, 3, "Debe", styleBold);
        this.setCellString(row, 4, "Haber", styleBold);

        Map<Integer, TipoPago> tipos = tipoPagoService.findAll().stream().collect(Collectors.toMap(TipoPago::getTipoPagoId, tipo -> tipo));

        Ejercicio ejercicio = ejercicioService.findByEjercicioId(ejercicioId);
        OffsetDateTime desde = ejercicio.getFechaInicio().withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime hasta = ejercicio.getFechaFinal().withOffsetSameInstant(ZoneOffset.UTC);
        for (OffsetDateTime fecha = desde; !fecha.isAfter(hasta); fecha = fecha.plusDays(1)) {
            for (TipoPagoFechaAcreditacion pago : tipoPagoFechaAcreditacionService.findAllByFechaAcreditacion(fecha)) {
                row = sheet.createRow(++fila);
                // Sumo 1 día para mostrar bien la fecha
                this.setCellOffsetDateTime(row, 0, fecha.plusDays(1), styleDate);
                this.setCellString(row, 1, tipos.get(pago.getTipoPagoId()).getNombre(), styleNormal);
                try {
                    IngresoAsiento asiento = ingresoAsientoService.findByUnique(fecha, pago.getTipoPagoId());
                    this.setCellInteger(row, 2, asiento.getOrdenContable(), styleNormal);
                    BigDecimal debe = BigDecimal.ZERO;
                    BigDecimal haber = BigDecimal.ZERO;
                    for (CuentaMovimiento movimiento : cuentaMovimientoService.findAllByAsiento(asiento.getFechaContable(), asiento.getOrdenContable(), 0, 2)) {
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
                    log.debug("Error cargando asiento ingreso");
                }
            }
        }

        for (int column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error generando Ejercicio Ingreso");
        }
        return filename;
    }

    @Transactional
    public String generateSuspendido(Integer facultadId, Integer geograficaId) {
        String path = environment.getProperty("path.files");

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

        int fila = -1;
        row = sheet.createRow(++fila);
        this.setCellString(row, 0, "Documento", styleBold);
        this.setCellString(row, 1, "Apellido, Nombre", styleBold);
        this.setCellString(row, 2, "Correo Personal", styleBold);
        this.setCellString(row, 3, "Correo Institucional", styleBold);
        this.setCellString(row, 4, "Telefono", styleBold);
        this.setCellString(row, 5, "Móvil", styleBold);

        List<PersonaSuspendido> suspendidos = personaSuspendidoService.findAllBySede(facultadId, geograficaId);
        List<String> keys = suspendidos.stream().map(PersonaSuspendido::personaKey).collect(Collectors.toList());
        Map<String, PersonaKey> personas = personaKeyService.findAllByUnifiedIn(keys, null).stream().collect(Collectors.toMap(PersonaKey::getUnified, Function.identity(), (persona, replacement) -> persona));
        Map<String, DomicilioKey> domicilios = domicilioKeyService.findAllByUnifiedIn(keys).stream().collect(Collectors.toMap(DomicilioKey::getUnified, Function.identity(), (domicilio, replacemente) -> domicilio));
        Map<Integer, Lectivo> lectivos = lectivoService.findAll().stream().collect(Collectors.toMap(Lectivo::getLectivoId, lectivo -> lectivo));
        Map<Integer, Facultad> facultades = facultadService.findAll().stream().collect(Collectors.toMap(Facultad::getFacultadId, facultad -> facultad));
        Map<Integer, TipoChequera> tipos = tipoChequeraService.findAll().stream().collect(Collectors.toMap(TipoChequera::getTipoChequeraId, tipo -> tipo));

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
            boolean deudor = false;
            for (ChequeraSerie serie : chequeraSerieService.findAllByPersona(suspendido.getPersonaId(), suspendido.getDocumentoId())) {
                DeudaChequera deuda = chequeraCuotaService.calculateDeuda(serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId());
                if (deuda.getCuotas() > 0) {
                    deudor = true;
                    row = sheet.createRow(++fila);
                    this.setCellString(row, 1, lectivos.get(serie.getLectivoId()).getNombre(), styleNormal);
                    this.setCellString(row, 2, facultades.get(serie.getFacultadId()).getNombre(), styleNormal);
                    this.setCellString(row, 3, tipos.get(serie.getTipoChequeraId()).getNombre(), styleNormal);
                    this.setCellString(row, 4, MessageFormat.format("Chequera: {0}/{1}/{2}", serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId()), styleNormal);
                    this.setCellString(row, 5, MessageFormat.format("Deuda: {0}/{1}", deuda.getCuotas(), deuda.getDeuda()), styleNormal);
                }
            }
            if (!deudor) {
                personaSuspendidoService.delete(suspendido.getPersonaSuspendidoId());
                fila -= 2;
            }
            ++fila;
        }

        for (int column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error generando Suspendidos");
        }
        return filename;
    }

    public String generateContratados(Integer anho, Integer mes) {
        String path = environment.getProperty("path.files");

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

        int fila = -1;
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
            this.setCellBigDecimal(row, 0, contratoPeriodo.getContrato().getContratado().getPersona().getPersonaId(), styleNormal);
            this.setCellString(row, 1, contratoPeriodo.getContrato().getContratado().getPersona().getApellidoNombre(), styleNormal);
            this.setCellString(row, 2, contratoPeriodo.getContrato().getContratado().getPersona().getCuit(), styleNormal);
            this.setCellString(row, 3, contratoPeriodo.getContrato().getFacultad().getNombre(), styleNormal);
            this.setCellString(row, 4, contratoPeriodo.getContrato().getGeografica().getNombre(), styleNormal);
            this.setCellOffsetDateTime(row, 5, contratoPeriodo.getContrato().getDesde(), styleDate);
            this.setCellOffsetDateTime(row, 6, contratoPeriodo.getContrato().getHasta(), styleDate);
        }

        for (int column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error generando Contratados");
        }
        return filename;
    }

    public String generateLibroIva(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        String path = environment.getProperty("path.files");

        String filename = path + "libro." + fechaDesde + "." + fechaHasta + ".xlsx";

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

        Sheet sheet = book.createSheet("Facturados");
        Row row = null;

        int fila = -1;
        row = sheet.createRow(++fila);
        this.setCellString(row, 0, "Fecha Recibo", styleBold);
        this.setCellString(row, 1, "Comprobante", styleBold);
        this.setCellString(row, 2, "Punto", styleBold);
        this.setCellString(row, 3, "Número", styleBold);
        this.setCellString(row, 4, "CUIT/DU", styleBold);
        this.setCellString(row, 5, "Apellido Nombre/Razón Social", styleBold);
        this.setCellString(row, 6, "Condición IVA", styleBold);
        this.setCellString(row, 7, "Fecha Pago", styleBold);
        this.setCellString(row, 8, "Importe", styleBold);
        this.setCellString(row, 9, "CAE", styleBold);
        this.setCellString(row, 10, "Vencimiento CAE", styleBold);
        this.setCellString(row, 11, "Enviada", styleBold);
        this.setCellString(row, 12, "Facultad", styleBold);
        this.setCellString(row, 13, "Sede", styleBold);
        this.setCellString(row, 14, "Tipo Chequera", styleBold);
        this.setCellString(row, 15, "Periodo", styleBold);

        for (FacturacionElectronica facturacionElectronica : facturacionElectronicaService.findAllByPeriodo(fechaDesde, fechaHasta)) {
            ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facturacionElectronica.getChequeraPago().getFacultadId(), facturacionElectronica.getChequeraPago().getTipoChequeraId(), facturacionElectronica.getChequeraPago().getChequeraSerieId());
            row = sheet.createRow(++fila);
            this.setCellOffsetDateTime(row, 0, facturacionElectronica.getFechaRecibo(), styleDate);
            this.setCellString(row, 1, facturacionElectronica.getComprobante().getDescripcion(), styleNormal);
            this.setCellInteger(row, 2, facturacionElectronica.getComprobante().getPuntoVenta(), styleNormal);
            this.setCellLong(row, 3, facturacionElectronica.getNumeroComprobante(), styleNormal);
            this.setCellString(row, 4, facturacionElectronica.getCuit(), styleNormal);
            this.setCellString(row, 5, facturacionElectronica.getApellido() + " " + facturacionElectronica.getNombre(), styleNormal);
            this.setCellString(row, 6, facturacionElectronica.getCondicionIva(), styleNormal);
            this.setCellOffsetDateTime(row, 7, facturacionElectronica.getChequeraPago().getFecha(), styleDate);
            this.setCellBigDecimal(row, 8, facturacionElectronica.getImporte(), styleNormal);
            this.setCellString(row, 9, facturacionElectronica.getCae(), styleNormal);
            this.setCellOffsetDateTime(row, 10, facturacionElectronica.getFechaVencimientoCae(), styleDate);
            this.setCellString(row, 11, facturacionElectronica.getEnviada() == 1 ? "Sí" : "No", styleNormal);
            this.setCellString(row, 12, chequeraSerie.getFacultad().getNombre(), styleNormal);
            this.setCellString(row, 13, chequeraSerie.getGeografica().getNombre(), styleNormal);
            this.setCellString(row, 14, chequeraSerie.getTipoChequera().getNombre(), styleNormal);
            this.setCellString(row, 15, facturacionElectronica.getChequeraPago().getChequeraCuota().getMes() + "/" + facturacionElectronica.getChequeraPago().getChequeraCuota().getAnho(), styleNormal);
        }

        for (int column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error generando Libro Iva");
        }
        return filename;
    }

    public String generateProveedores() {
        String path = environment.getProperty("path.files");

        String filename = path + "proveedores.xlsx";

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

        Sheet sheet = book.createSheet("Proveedores");
        Row row = null;

        int fila = -1;
        row = sheet.createRow(++fila);
        this.setCellString(row, 0, "#", styleBold);
        this.setCellString(row, 1, "CUIT", styleBold);
        this.setCellString(row, 2, "Nombre Fantasía", styleBold);
        this.setCellString(row, 3, "Razón Social", styleBold);
        this.setCellString(row, 4, "Orden Cheque", styleBold);
        this.setCellString(row, 5, "Domicilio", styleBold);
        this.setCellString(row, 6, "Teléfono", styleBold);
        this.setCellString(row, 7, "Fax", styleBold);
        this.setCellString(row, 8, "Celular", styleBold);
        this.setCellString(row, 9, "e-mail", styleBold);
        this.setCellString(row, 10, "e-mail interno", styleBold);
        this.setCellString(row, 11, "Cuenta Contable", styleBold);
        this.setCellString(row, 12, "Cuenta Contable", styleBold);
        this.setCellString(row, 13, "Habilitado", styleBold);
        this.setCellString(row, 14, "CBU", styleBold);

        for (Proveedor proveedor : proveedorService.findAll()) {
            row = sheet.createRow(++fila);
            this.setCellInteger(row, 0, proveedor.getProveedorId(), styleNormal);
            this.setCellString(row, 1, proveedor.getCuit(), styleNormal);
            this.setCellString(row, 2, proveedor.getNombreFantasia(), styleNormal);
            this.setCellString(row, 3, proveedor.getRazonSocial(), styleNormal);
            this.setCellString(row, 4, proveedor.getOrdenCheque(), styleNormal);
            this.setCellString(row, 5, proveedor.getDomicilio(), styleNormal);
            this.setCellString(row, 6, proveedor.getTelefono(), styleNormal);
            this.setCellString(row, 7, proveedor.getFax(), styleNormal);
            this.setCellString(row, 8, proveedor.getCelular(), styleNormal);
            this.setCellString(row, 9, proveedor.getEmail(), styleNormal);
            this.setCellString(row, 10, proveedor.getEmailInterno(), styleNormal);
            this.setCellBigDecimal(row, 11, proveedor.getNumeroCuenta(), styleNormal);
            this.setCellString(row, 12, proveedor.getCuenta().getNombre(), styleNormal);
            this.setCellString(row, 13, proveedor.getHabilitado() == 1 ? "Si" : "No", styleNormal);
            this.setCellString(row, 14, proveedor.getCbu(), styleNormal);
        }

        for (int column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error generando proveedores");
        }
        return filename;
    }

    public String generateFacturasPendientes(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        String path = environment.getProperty("path.files");

        String filename = path + "pendientes.xlsx";

        Workbook book = new XSSFWorkbook();
        CellStyle styleNormal = book.createCellStyle();
        Font fontNormal = book.createFont();
        fontNormal.setBold(false);
        styleNormal.setFont(fontNormal);

        CellStyle styleBold = book.createCellStyle();
        Font fontBold = book.createFont();
        fontBold.setBold(true);
        styleBold.setFont(fontBold);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Sheet sheet = book.createSheet("Pendientes");
        Row row = null;

        int fila = -1;
        row = sheet.createRow(++fila);
        this.setCellString(row, 0, "Facturas Pendientes", styleBold);
        row = sheet.createRow(++fila);
        this.setCellString(row, 0, "Periodo", styleBold);
        this.setCellString(row, 1, format.format(fechaDesde.withOffsetSameInstant(ZoneOffset.UTC)), styleNormal);
        this.setCellString(row, 2, format.format(fechaHasta.withOffsetSameInstant(ZoneOffset.UTC)), styleNormal);

        int columnaRazonSocial = 0;
        int columnaCUIT = 1;
        int columnaFechaComprobante = 2;
        int columnaTipoComprobante = 3;
        int columnaComprobantePrefijo = 4;
        int columnaComprobanteNumero = 5;
        int columnaImporteFactura = 6;
        int columnaSaldo = 7;
        row = sheet.createRow(++fila);
        this.setCellString(row, columnaRazonSocial, "Razón Social", styleBold);
        this.setCellString(row, columnaCUIT, "CUIT", styleBold);
        this.setCellString(row, columnaFechaComprobante, "Fecha Comprobante", styleBold);
        this.setCellString(row, columnaTipoComprobante, "Tipo Comprobante", styleBold);
        this.setCellString(row, columnaComprobantePrefijo, "Prefijo", styleBold);
        this.setCellString(row, columnaComprobanteNumero, "Numero", styleBold);
        this.setCellString(row, columnaImporteFactura, "Importe Factura", styleBold);
        this.setCellString(row, columnaSaldo, "Saldo al " + format.format(fechaHasta.withOffsetSameInstant(ZoneOffset.UTC)), styleBold);

        for (FacturaPendiente facturaPendiente : facturaPendienteService.findAllFacturasPendientesBetweenDates(fechaDesde, fechaHasta)) {
            try {
                log.debug("FacturaPendiente -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(facturaPendiente));
            } catch (JsonProcessingException e) {
                log.debug("FacturaPendiente error -> {}", e.getMessage());
            }
            BigDecimal importeFactura = facturaPendiente.getImporteFactura();
            BigDecimal importePagado = BigDecimal.ZERO;
            if (facturaPendiente.getImportePagado() != null) {
                importePagado = facturaPendiente.getImportePagado();
            }
            BigDecimal saldo = importeFactura.subtract(importePagado).setScale(2, RoundingMode.HALF_UP);
            log.debug("factura {} - pagado {} - saldo {}", importeFactura, importePagado, saldo);
            if (saldo.compareTo(BigDecimal.ZERO) != 0) {
                row = sheet.createRow(++fila);
                this.setCellString(row, columnaRazonSocial, facturaPendiente.getRazonSocial(), styleNormal);
                this.setCellString(row, columnaCUIT, facturaPendiente.getCuit(), styleNormal);
                this.setCellString(row, columnaFechaComprobante, format.format(facturaPendiente.getFechaComprobante().withOffsetSameInstant(ZoneOffset.UTC)), styleNormal);
                this.setCellString(row, columnaTipoComprobante, facturaPendiente.getComprobante(), styleNormal);
                this.setCellInteger(row, columnaComprobantePrefijo, facturaPendiente.getPrefijo(), styleNormal);
                this.setCellLong(row, columnaComprobanteNumero, facturaPendiente.getNumeroComprobante(), styleNormal);
                this.setCellBigDecimal(row, columnaImporteFactura, facturaPendiente.getImporteFactura(), styleNormal);
                this.setCellBigDecimal(row, columnaSaldo, saldo, styleNormal);
            }
        }

        for (int column = 0; column < sheet.getRow(2).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            book.close();
        } catch (Exception e) {
            log.debug("Error generando pendientes");
        }
        return filename;
    }

    private Integer getArancel(BigDecimal personaId, Integer arancelTipoId) {
        BigDecimal[] test = new BigDecimal[]{new BigDecimal(23693840), new BigDecimal(40596925), new BigDecimal(41699055), new BigDecimal(41868947)};
        for (BigDecimal value : test) {
            if (value.compareTo(personaId) == 0) return 1;
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

    private Sheet makeWorkbook(Workbook book, CellStyle styleNormal, CellStyle styleBold, String sheetName) {
        book = new XSSFWorkbook();
        styleNormal = book.createCellStyle();
        Font fontNormal = book.createFont();
        fontNormal.setBold(false);
        styleNormal.setFont(fontNormal);

        styleBold = book.createCellStyle();
        Font fontBold = book.createFont();
        fontBold.setBold(true);
        styleBold.setFont(fontBold);

        return book.createSheet(sheetName);
    }

}

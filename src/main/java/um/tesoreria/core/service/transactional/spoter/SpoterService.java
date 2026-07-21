package um.tesoreria.core.service.transactional.spoter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.exception.ChequeraSerieControlException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.exception.LegajoException;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.model.ChequeraSerieControl;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.model.LectivoTotal;
import um.tesoreria.core.service.*;
import um.tesoreria.core.util.Jsonifier;
import um.tesoreria.core.util.Tool;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpoterService {

    private final BuildService buildService;
    private final PersonaService personaService;
    private final ChequeraSerieService chequeraSerieService;
    private final DomicilioService domicilioService;
    private final ChequeraSerieControlService chequeraSerieControlService;
    private final LegajoService legajoService;
    private final LectivoTotalService lectivoTotalService;
    private final ChequeraTotalService chequeraTotalService;
    private final LectivoAlternativaService lectivoAlternativaService;
    private final ChequeraAlternativaService chequeraAlternativaService;
    private final LectivoCuotaService lectivoCuotaService;
    private final ChequeraCuotaService chequeraCuotaService;

    @Transactional
    public ChequeraSerie makeChequeraSpoter(SpoterData spoterData, Integer lectivoId, Curso curso,
                                                  CarreraChequera carreraChequera) {
        log.debug("Processing SpoterService.makeChequeraSpoter");
        Build build = buildService.findLast();
        Persona persona;
        try {
            persona = personaService.findByUnique(spoterData.getPersonaId(), spoterData.getDocumentoId());
        } catch (PersonaException e) {
            persona = personaService.create(new Persona(null, spoterData.getPersonaId(), spoterData.getDocumentoId(),
                    spoterData.getApellido(), spoterData.getNombre(), "", (byte) 0, "", "", "", (byte) 0));
        }
        log.debug("PersonaEntity: {}", persona.jsonify());
        Domicilio domicilio;
        try {
            domicilio = domicilioService.findByUnique(spoterData.getPersonaId(), spoterData.getDocumentoId());
        } catch (DomicilioException e) {
            domicilio = domicilioService.create(new Domicilio(null, spoterData.getPersonaId(), spoterData.getDocumentoId(),
                    Tool.hourAbsoluteArgentina(), "", "", "", "", "", spoterData.getCelular(), "", "",
                    spoterData.getFacultadId(), null, null, spoterData.getEmailPersonal(), "", "", ""));
        }
        log.debug("Domicilio -> {}", domicilio.jsonify());

        // Determinar el número usando ChequeraSerieControl
        ChequeraSerieControl chequeraSerieControl;
        long chequeraSerieId = 1L;
        try {
            chequeraSerieControl = chequeraSerieControlService.findLastByTipoChequera(carreraChequera.getFacultadId(),
                    carreraChequera.getTipoChequeraId());
            chequeraSerieId = 1L + chequeraSerieControl.getChequeraSerieId();
        } catch (ChequeraSerieControlException e) {
            log.error("Error al obtener el último chequeraSerieControl -> {}", e.getMessage());
        }
        log.debug("SpoterService.makeChequeraSpoter - chequeraSerieId -> {}", chequeraSerieId);
        // Llenar Legajo tesorería
        Legajo legajo;
        try {
            legajo = legajoService.findByFacultadIdAndPersonaIdAndDocumentoId(spoterData.getFacultadId(),
                    spoterData.getPersonaId(), spoterData.getDocumentoId());
        } catch (LegajoException e) {
            legajo = legajoService.add(new Legajo(null, spoterData.getPersonaId(), spoterData.getDocumentoId(),
                    spoterData.getFacultadId(), 0L, Tool.dateAbsoluteArgentina(), lectivoId, spoterData.getPlanId(),
                    spoterData.getCarreraId(), (byte) 1, spoterData.getGeograficaId(), "", (byte) 0, null));
        }
        log.debug("Legajo -> {}", legajo.jsonify());

        // Generar ChequeraSerieControl
        chequeraSerieControl = chequeraSerieControlService
                .add(new ChequeraSerieControl(null, carreraChequera.getFacultadId(),
                        carreraChequera.getTipoChequeraId(), chequeraSerieId, (byte) 0, 0, 0, 0L, (byte) 0));
        log.debug("ChequeraSerieControl -> {}", chequeraSerieControl.jsonify());

        // Generar ChequeraSerie
        ChequeraSerie chequeraSerie = chequeraSerieService.add(new ChequeraSerie(null,
                chequeraSerieControl.getFacultadId(),
                chequeraSerieControl.getTipoChequeraId(),
                chequeraSerieControl.getChequeraSerieId(),
                spoterData.getPersonaId(),
                spoterData.getDocumentoId(),
                lectivoId,
                1,
                curso.getCursoId(),
                (byte) 0,
                spoterData.getGeograficaId(),
                Tool.dateAbsoluteArgentina(),
                0,
                "Generated By Spoter",
                1,
                (byte) 0,
                2,
                (byte) 0,
                "fsarabia",
                (byte) 0,
                (byte) 0,
                build.getBuild(),
                (byte) 0,
                BigDecimal.ZERO,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                BigDecimal.ZERO,
                null));
        log.debug("ChequeraSerie -> {}", chequeraSerie.jsonify());

        // Generar ChequeraTotal
        List<ChequeraTotal> chequeraTotals = new ArrayList<>();
        for (LectivoTotal lectivoTotal : lectivoTotalService.findAllByTipo(chequeraSerie.getFacultadId(),
                chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId())) {
            chequeraTotals.add(ChequeraTotal.builder()
                    .facultadId(chequeraSerie.getFacultadId())
                    .tipoChequeraId(chequeraSerie.getTipoChequeraId())
                    .chequeraSerieId(chequeraSerie.getChequeraSerieId())
                    .productoId(lectivoTotal.getProductoId())
                    .total(lectivoTotal.getTotal())
                    .pagado(BigDecimal.ZERO)
                    .build());
        }
        chequeraTotals = chequeraTotalService.saveAll(chequeraTotals);
        log.debug("ChequeraTotals -> {}", Jsonifier.builder(chequeraTotals).build());

        // Generar ChequeraAlternativa
        List<ChequeraAlternativa> chequeraAlternativas = new ArrayList<>();
        for (LectivoAlternativa lectivoAlternativa : lectivoAlternativaService.findAllByTipo(
                chequeraSerie.getFacultadId(), chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId(),
                chequeraSerie.getAlternativaId())) {
            chequeraAlternativas.add(new ChequeraAlternativa(null, chequeraSerie.getFacultadId(),
                    chequeraSerie.getTipoChequeraId(), chequeraSerie.getChequeraSerieId(),
                    lectivoAlternativa.getProductoId(), lectivoAlternativa.getAlternativaId(),
                    Objects.requireNonNull(lectivoAlternativa.getTitulo()),
                    Objects.requireNonNull(lectivoAlternativa.getCuotas())));
        }
        chequeraAlternativas = chequeraAlternativaService.saveAll(chequeraAlternativas);
        log.debug("ChequeraAlternativas -> {}", Jsonifier.builder(chequeraAlternativas).build());

        // Generar Cuotas
        List<ChequeraCuota> chequeraCuotas = new ArrayList<>();
        int offset = 0;
        for (LectivoCuota lectivoCuota : lectivoCuotaService.findAllByTipo(chequeraSerie.getFacultadId(),
                chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId(), chequeraSerie.getAlternativaId())) {
            OffsetDateTime vencimiento1 = lectivoCuota.getVencimiento1();
            OffsetDateTime vencimiento2 = lectivoCuota.getVencimiento2();
            OffsetDateTime vencimiento3 = lectivoCuota.getVencimiento3();
            if (OffsetDateTime.now().isAfter(vencimiento1)) {
                vencimiento1 = Tool.dateAbsoluteArgentina().plusDays(7 + 30L * offset);
                vencimiento2 = Tool.dateAbsoluteArgentina().plusDays(20 + 30L * offset);
                vencimiento3 = Tool.dateAbsoluteArgentina().plusDays(40 + 30L * offset);
                offset++;
            }
            ChequeraCuota chequeraCuota = new ChequeraCuota(null, chequeraSerie.getChequeraId(), chequeraSerie.getFacultadId(),
                    chequeraSerie.getTipoChequeraId(), chequeraSerie.getChequeraSerieId(), lectivoCuota.getProductoId(),
                    lectivoCuota.getAlternativaId(), lectivoCuota.getCuotaId(), lectivoCuota.getMes(),
                    lectivoCuota.getAnho(), chequeraSerie.getArancelTipoId(), vencimiento1, lectivoCuota.getImporte1(),
                    lectivoCuota.getImporte1(), vencimiento2, lectivoCuota.getImporte2(), lectivoCuota.getImporte2(),
                    vencimiento3, lectivoCuota.getImporte3(), lectivoCuota.getImporte3(), "", "", (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, 0, null, null, null, null);
            log.debug("mail_chequera_service.make_chequera_spoter.chequera_cuota -> -> {}", chequeraCuota.jsonify());
            log.debug("mail_chequera_service.make_chequera_spoter - calling calculate_codigo_barras");
            chequeraCuota.setCodigoBarras(chequeraCuotaService.calculateCodigoBarras(chequeraCuota));
            log.debug("mail_chequera_service.make_chequera_spoter - after calculate_codigo_barras");
            chequeraCuotas.add(chequeraCuota);
        }
        chequeraCuotas = chequeraCuotaService.saveAll(chequeraCuotas);
        log.debug("ChequeraCuotas -> {}", Jsonifier.builder(chequeraCuotas).build());
        return chequeraSerie;
    }

}

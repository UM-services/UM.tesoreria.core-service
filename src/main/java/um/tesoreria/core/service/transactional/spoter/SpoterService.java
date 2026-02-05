package um.tesoreria.core.service.transactional.spoter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.exception.ChequeraSerieControlException;
import um.tesoreria.core.exception.DomicilioException;
import um.tesoreria.core.exception.LegajoException;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.hexagonal.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.model.ChequeraSerieControl;
import um.tesoreria.core.model.ChequeraTotal;
import um.tesoreria.core.model.LectivoCuota;
import um.tesoreria.core.model.LectivoTotal;
import um.tesoreria.core.service.*;
import um.tesoreria.core.util.Tool;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
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

    public SpoterService(BuildService buildService,
                         PersonaService personaService,
                         ChequeraSerieService chequeraSerieService,
                         DomicilioService domicilioService,
                         ChequeraSerieControlService chequeraSerieControlService,
                         LegajoService legajoService,
                         LectivoTotalService lectivoTotalService,
                         ChequeraTotalService chequeraTotalService,
                         LectivoAlternativaService lectivoAlternativaService,
                         ChequeraAlternativaService chequeraAlternativaService,
                         LectivoCuotaService lectivoCuotaService,
                         ChequeraCuotaService chequeraCuotaService
    ) {
        this.buildService = buildService;
        this.personaService = personaService;
        this.chequeraSerieService = chequeraSerieService;
        this.domicilioService = domicilioService;
        this.chequeraSerieControlService = chequeraSerieControlService;
        this.legajoService = legajoService;
        this.lectivoTotalService = lectivoTotalService;
        this.chequeraTotalService = chequeraTotalService;
        this.lectivoAlternativaService = lectivoAlternativaService;
        this.chequeraAlternativaService = chequeraAlternativaService;
        this.lectivoCuotaService = lectivoCuotaService;
        this.chequeraCuotaService = chequeraCuotaService;
    }

    @Transactional
    public ChequeraSerie makeChequeraSpoter(SpoterData spoterData, Integer lectivoId, Curso curso,
                                            CarreraChequera carreraChequera) {
        log.debug("Processing SpoterService.makeChequeraSpoter");
        Build build = buildService.findLast();
        PersonaEntity personaEntity;
        try {
            personaEntity = personaService.findByUnique(spoterData.getPersonaId(), spoterData.getDocumentoId());
        } catch (PersonaException e) {
            personaEntity = personaService.add(new PersonaEntity(null, spoterData.getPersonaId(), spoterData.getDocumentoId(),
                    spoterData.getApellido(), spoterData.getNombre(), "", (byte) 0, "", "", ""));
        }
        logPersona(personaEntity);
        Domicilio domicilio;
        try {
            domicilio = domicilioService.findByUnique(spoterData.getPersonaId(), spoterData.getDocumentoId());
        } catch (DomicilioException e) {
            domicilio = domicilioService.add(new Domicilio(null, spoterData.getPersonaId(), spoterData.getDocumentoId(),
                    Tool.hourAbsoluteArgentina(), "", "", "", "", "", spoterData.getCelular(), "", "",
                    spoterData.getFacultadId(), null, null, spoterData.getEmailPersonal(), "", "", ""), false);
        }
        logDomicilio(domicilio);

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
        logLegajo(legajo);

        // Generar ChequeraSerieControl
        chequeraSerieControl = chequeraSerieControlService
                .add(new ChequeraSerieControl(null, carreraChequera.getFacultadId(),
                        carreraChequera.getTipoChequeraId(), chequeraSerieId, (byte) 0, 0, 0, 0L, (byte) 0));
        logChequeraSerieControl(chequeraSerieControl);

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
                0,
                BigDecimal.ZERO,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null));
        logChequeraSerie(chequeraSerie);

        // Generar ChequeraTotal
        List<ChequeraTotal> chequeraTotals = new ArrayList<>();
        for (LectivoTotal lectivoTotal : lectivoTotalService.findAllByTipo(chequeraSerie.getFacultadId(),
                chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId())) {
            chequeraTotals.add(new ChequeraTotal(null, chequeraSerie.getFacultadId(), chequeraSerie.getTipoChequeraId(),
                    chequeraSerie.getChequeraSerieId(), lectivoTotal.getProductoId(), lectivoTotal.getTotal(),
                    BigDecimal.ZERO));
        }
        chequeraTotals = chequeraTotalService.saveAll(chequeraTotals);
        logChequeraTotals(chequeraTotals);

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
        logChequeraAlternativas(chequeraAlternativas);
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
            try {
                log.debug("mail_chequera_service.make_chequera_spoter.chequera_cuota -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(chequeraCuota));
            } catch (JsonProcessingException e) {
                log.debug("mail_chequera_service.make_chequera_spoter.chequera_cuota -> error");
            }
            log.debug("mail_chequera_service.make_chequera_spoter - calling calculate_codigo_barras");
            chequeraCuota.setCodigoBarras(chequeraCuotaService.calculateCodigoBarras(chequeraCuota));
            log.debug("mail_chequera_service.make_chequera_spoter - after calculate_codigo_barras");
            chequeraCuotas.add(chequeraCuota);
        }
        chequeraCuotas = chequeraCuotaService.saveAll(chequeraCuotas);
        logChequeraCuotas(chequeraCuotas);
        return chequeraSerie;
    }

    private void logLegajo(Legajo legajo) {
        try {
            log.debug("Legajo -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(legajo));
        } catch (JsonProcessingException e) {
            log.debug("Legajo jsonify error -> {}", e.getMessage());
        }
    }

    private void logDomicilio(Domicilio domicilio) {
        try {
            log.debug("Domicilio -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(domicilio));
        } catch (JsonProcessingException e) {
            log.debug("Domicilio jsonify error -> {}", e.getMessage());
        }
    }

    private void logPersona(PersonaEntity personaEntity) {
        try {
            log.debug("Persona -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(personaEntity));
        } catch (JsonProcessingException e) {
            log.debug("Persona jsonify error -> {}", e.getMessage());
        }
    }

    private void logChequeraCuotas(List<ChequeraCuota> chequeraCuotas) {
        try {
            log.debug("ChequeraCuota -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraCuotas));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraCuota jsonify error -> {}", e.getMessage());
        }
    }

    private void logChequeraAlternativas(List<ChequeraAlternativa> chequeraAlternativas) {
        try {
            log.debug("ChequeraAlternativa -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraAlternativas));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraAlternativa jsonify error -> {}", e.getMessage());
        }
    }

    private void logChequeraTotals(List<ChequeraTotal> chequeraTotals) {
        try {
            log.debug("ChequeraTotal -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraTotals));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraTotal jsonify error -> {}", e.getMessage());
        }

    }

    private void logChequeraSerie(ChequeraSerie chequeraSerie) {
        try {
            log.debug("ChequeraSerie -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraSerie));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraSerie jsonify error -> {}", e.getMessage());
        }
    }

    private void logChequeraSerieControl(ChequeraSerieControl chequeraSerieControl) {
        try {
            log.debug("ChequeraSerieControl -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraSerieControl));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraSerieControl jsonify error -> {}", e.getMessage());
        }
    }

}

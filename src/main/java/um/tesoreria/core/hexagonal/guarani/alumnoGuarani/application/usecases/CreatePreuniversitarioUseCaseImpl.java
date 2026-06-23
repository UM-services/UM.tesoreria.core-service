package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.application.usecases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.ChequeraSerieControlException;
import um.tesoreria.core.exception.LegajoException;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.hexagonal.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.hexagonal.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.hexagonal.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in.CreatePreuniversitarioUseCase;
import um.tesoreria.core.hexagonal.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.kotlin.model.Build;
import um.tesoreria.core.kotlin.model.ChequeraAlternativa;
import um.tesoreria.core.kotlin.model.LectivoAlternativa;
import um.tesoreria.core.kotlin.model.Legajo;
import um.tesoreria.core.model.ChequeraSerieControl;
import um.tesoreria.core.model.ChequeraTotal;
import um.tesoreria.core.model.LectivoCuota;
import um.tesoreria.core.model.LectivoTotal;
import um.tesoreria.core.service.*;
import um.tesoreria.core.util.Jsonifier;
import um.tesoreria.core.util.Tool;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreatePreuniversitarioUseCaseImpl implements CreatePreuniversitarioUseCase {

    private final PersonaService personaService;
    private final DomicilioService domicilioService;
    private final ChequeraSerieControlService chequeraSerieControlService;
    private final LegajoService legajoService;
    private final LectivoService lectivoService;
    private final ChequeraSerieService chequeraSerieService;
    private final BuildService buildService;
    private final LectivoTotalService lectivoTotalService;
    private final ChequeraTotalService chequeraTotalService;
    private final LectivoAlternativaService lectivoAlternativaService;
    private final ChequeraAlternativaService chequeraAlternativaService;
    private final LectivoCuotaService lectivoCuotaService;
    private final ChequeraCuotaService chequeraCuotaService;

    @Override
    public AlumnoGuarani createPreuniversitario(AlumnoGuarani alumnoGuarani) {
        log.debug("\n\nProcessing CreatePreuniversitarioUseCaseImpl.createPreuniversitario\n\n");
        log.debug("AlumnoGuarani -> {}", alumnoGuarani.jsonify());
//        var created = this.createChequeraPreuniversitario(alumnoGuarani);
        return null;
    }

    protected ChequeraSerieEntity createChequeraPreuniversitario(AlumnoGuarani alumnoGuarani) {
        log.debug("\n\nProcessing CreatePreuniversitarioUseCaseImpl.createChequeraPreuniversitario\n\n");
        int lectivoId = lectivoService.findByFecha(OffsetDateTime.now()).getLectivoId();
        Build build = buildService.findLast();

        var context = Tool.convert2Tesium(alumnoGuarani.getUbicacion(), alumnoGuarani.getPropuesta());
        if (context.getTipoChequeraId() == 0) {
            return null;
        }

        // create persona
        BigDecimal personaId = new BigDecimal(alumnoGuarani.getPersonaRel().getDocumentoPrincipalRel().getNroDocumento());
        int documentoId = alumnoGuarani.getPersonaRel().getDocumentoPrincipalRel().getTipoDocumentoRel().getTipoDocumento() == 0 ? 1 : 0;
        Persona persona;
        try {
            persona = personaService.findByUnique(personaId, documentoId);
        } catch (PersonaException e) {
            log.debug(e.getMessage());
            persona = Persona.builder()
                    .personaId(personaId)
                    .documentoId(documentoId)
                    .apellido(alumnoGuarani.getPersonaRel().getApellido())
                    .nombre(alumnoGuarani.getPersonaRel().getNombres())
                    .cbu("")
                    .cuit("")
                    .hpum((byte) 0)
                    .primero((byte) 0)
                    .sexo(alumnoGuarani.getPersonaRel().getSexo())
                    .build();
            persona = personaService.create(persona);
        }
        log.debug("Persona -> {}", persona.jsonify());
        // Verificar domicilio
        Domicilio domicilio;
        try {
            domicilio = domicilioService.findByUnique(persona.getPersonaId(), persona.getDocumentoId());
        } catch (DomicilioException e) {
            domicilio = Domicilio.builder()
                    .personaId(persona.getPersonaId())
                    .documentoId(persona.getDocumentoId())
                    .fecha(OffsetDateTime.now())
                    .emailPersonal(alumnoGuarani.getPersonaRel().getContactos().getFirst().getEmail())
                    .emailInstitucional("")
                    .calle("")
                    .puerta("")
                    .codigoPostal("")
                    .piso("")
                    .dpto("")
                    .laboral("")
                    .telefono("")
                    .movil("")
                    .observaciones("")
                    .build();
            domicilio = domicilioService.create(domicilio);
        }
        log.debug("Domicilio -> {}", domicilio.jsonify());

        // Determinar el número usando ChequeraSerieControl
        ChequeraSerieControl chequeraSerieControl;
        long chequeraSerieId = 1L;
        try {
            chequeraSerieControl = chequeraSerieControlService.findLastByTipoChequera(context.getFacultadId(), context.getTipoChequeraId());
            chequeraSerieId = 1L + chequeraSerieControl.getChequeraSerieId();
        } catch (ChequeraSerieControlException e) {
            log.error("Error al obtener el último chequeraSerieControl -> {}", e.getMessage());
        }
        log.debug("SpoterService.makeChequeraSpoter - chequeraSerieId -> {}", chequeraSerieId);
        // Llenar Legajo tesorería
        Legajo legajo;
        try {
            legajo = legajoService.findByFacultadIdAndPersonaIdAndDocumentoId(context.getFacultadId(), personaId, documentoId);
        } catch (LegajoException e) {
            legajo = legajoService.add(new Legajo(null, personaId, documentoId,
                    context.getFacultadId(), 0L, Tool.dateAbsoluteArgentina(), lectivoId, null,
                    null, (byte) 1, context.getGeograficaId(), "", (byte) 0, null));
        }
        log.debug("Legajo -> {}", legajo.jsonify());

        // Generar ChequeraSerieControl
        chequeraSerieControl = chequeraSerieControlService
                .add(new ChequeraSerieControl(null, context.getFacultadId(),
                        context.getTipoChequeraId(), chequeraSerieId, (byte) 0, 0, 0, 0L, (byte) 0));
        log.debug("ChequeraSerieControl -> {}", chequeraSerieControl.jsonify());

        // Generar ChequeraSerie
        ChequeraSerieEntity chequeraSerie = chequeraSerieService.add(new ChequeraSerieEntity(null,
                chequeraSerieControl.getFacultadId(),
                chequeraSerieControl.getTipoChequeraId(),
                chequeraSerieControl.getChequeraSerieId(),
                personaId,
                documentoId,
                lectivoId,
                1,
                7,
                (byte) 0,
                context.getGeograficaId(),
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
        log.debug("ChequeraSerie -> {}", chequeraSerie.jsonify());

        // Generar ChequeraTotal
        List<ChequeraTotal> chequeraTotals = new ArrayList<>();
        for (LectivoTotal lectivoTotal : lectivoTotalService.findAllByTipo(chequeraSerie.getFacultadId(),
                chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId())) {
            chequeraTotals.add(new ChequeraTotal(null, chequeraSerie.getFacultadId(), chequeraSerie.getTipoChequeraId(),
                    chequeraSerie.getChequeraSerieId(), lectivoTotal.getProductoId(), lectivoTotal.getTotal(),
                    BigDecimal.ZERO));
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
        List<ChequeraCuotaEntity> chequeraCuotas = new ArrayList<>();
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
            ChequeraCuotaEntity chequeraCuota = new ChequeraCuotaEntity(null, chequeraSerie.getChequeraId(), chequeraSerie.getFacultadId(),
                    chequeraSerie.getTipoChequeraId(), chequeraSerie.getChequeraSerieId(), lectivoCuota.getProductoId(),
                    lectivoCuota.getAlternativaId(), lectivoCuota.getCuotaId(), lectivoCuota.getMes(),
                    lectivoCuota.getAnho(), chequeraSerie.getArancelTipoId(), vencimiento1, lectivoCuota.getImporte1(),
                    lectivoCuota.getImporte1(), vencimiento2, lectivoCuota.getImporte2(), lectivoCuota.getImporte2(),
                    vencimiento3, lectivoCuota.getImporte3(), lectivoCuota.getImporte3(), "", "", (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, 0, null, null, null, null);
            log.debug("chequera_cuota -> {}", chequeraCuota.jsonify());
            chequeraCuota.setCodigoBarras(chequeraCuotaService.calculateCodigoBarras(chequeraCuota));
            chequeraCuotas.add(chequeraCuota);
        }
        chequeraCuotas = chequeraCuotaService.saveAll(chequeraCuotas);
        log.debug("ChequeraCuotas -> {}", Jsonifier.builder(chequeraCuotas).build());
        return chequeraSerie;
    }

}

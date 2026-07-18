package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.ChequeraSerieControlException;
import um.tesoreria.core.exception.LegajoException;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.documento.application.exception.DocumentoException;
import um.tesoreria.core.hexagonal.documento.application.service.DocumentoService;
import um.tesoreria.core.hexagonal.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.hexagonal.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.kotlin.model.Build;
import um.tesoreria.core.kotlin.model.ChequeraAlternativa;
import um.tesoreria.core.kotlin.model.LectivoAlternativa;
import um.tesoreria.core.kotlin.model.Legajo;
import um.tesoreria.core.model.ChequeraSerieControl;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class PreuniversitarioChequeraService {

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
    private final DocumentoService documentoService;

    @Transactional
    public ChequeraSerie create(AlumnoGuarani alumnoGuarani) {
        log.debug("\n\nProcessing PreuniversitarioChequeraService.create\n\n");
        int lectivoId = lectivoService.findByFecha(OffsetDateTime.now()).getLectivoId();
        Build build = buildService.findLast();

        var context = Tool.convert2Tesium(alumnoGuarani.getUbicacion(), alumnoGuarani.getPropuesta());
        if (context.getTipoChequeraId() == 0) {
            return null;
        }

        BigDecimal personaId = new BigDecimal(alumnoGuarani.getPersonaRel().getDocumentoPrincipalRel().getNroDocumento());
        int documentoId = alumnoGuarani.getPersonaRel().getDocumentoPrincipalRel().getTipoDocumentoRel().getTipoDocumento() == 0 ? 1 : alumnoGuarani.getPersonaRel().getDocumentoPrincipalRel().getTipoDocumentoRel().getTipoDocumento();
        // Verifica si el tipo de documento existe, suponemos que casi todos serán tipo 1
        if (documentoId > 1) {
            try {
                documentoService.findByDocumentoId(documentoId);
            } catch (DocumentoException e) {
                documentoId = 1;
            }
        }
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
            try {
                persona = personaService.create(persona);
            } catch (PersonaException ex) {
                log.info(ex.getMessage());
                return null;
            }
        }
        log.debug("Persona -> {}", persona.jsonify());

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
            try {
                domicilio = domicilioService.create(domicilio);
            } catch (DomicilioException ex) {
                log.info(ex.getMessage());
                return null;
            }
        }
        log.debug("Domicilio -> {}", domicilio.jsonify());

        ChequeraSerieControl chequeraSerieControl;
        long chequeraSerieId = 1L;
        try {
            chequeraSerieControl = chequeraSerieControlService.findLastByTipoChequera(context.getFacultadId(), context.getTipoChequeraId());
            chequeraSerieId = 1L + chequeraSerieControl.getChequeraSerieId();
        } catch (ChequeraSerieControlException e) {
            log.error("Error al obtener el último chequeraSerieControl -> {}", e.getMessage());
        }

        log.debug("PreuniversitarioChequeraService.create - chequeraSerieId -> {}", chequeraSerieId);

        Legajo legajo;
        try {
            legajo = legajoService.findByFacultadIdAndPersonaIdAndDocumentoId(context.getFacultadId(), personaId, documentoId);
        } catch (LegajoException e) {
            legajo = legajoService.add(new Legajo(null, personaId, documentoId,
                    context.getFacultadId(), 0L, Tool.dateAbsoluteArgentina(), lectivoId, null,
                    null, (byte) 1, context.getGeograficaId(), "", (byte) 0, null));
        }
        log.debug("Legajo -> {}", legajo.jsonify());

        chequeraSerieControl = chequeraSerieControlService
                .add(new ChequeraSerieControl(null, context.getFacultadId(),
                        context.getTipoChequeraId(), chequeraSerieId, (byte) 0, 0, 0, 0L, (byte) 0));
        log.debug("ChequeraSerieControl -> {}", chequeraSerieControl.jsonify());

        ChequeraSerie chequeraSerie = chequeraSerieService.add(ChequeraSerie.builder()
                .facultadId(chequeraSerieControl.getFacultadId())
                .tipoChequeraId(chequeraSerieControl.getTipoChequeraId())
                .chequeraSerieId(chequeraSerieControl.getChequeraSerieId())
                .personaId(personaId)
                .documentoId(documentoId)
                .lectivoId(lectivoId)
                .arancelTipoId(1)
                .cursoId(7)
                .asentado((byte) 0)
                .geograficaId(context.getGeograficaId())
                .fecha(Tool.dateAbsoluteArgentina())
                .cuotasPagadas(0)
                .observaciones("Generated From Guaraní")
                .alternativaId(context.getFacultadId() == 14 ? 1 : 2)
                .algoPagado((byte) 0)
                .tipoImpresionId(2)
                .flagPayperTic((byte) 0)
                .usuarioId("guarani")
                .enviado((byte) 0)
                .retenida((byte) 0)
                .version(build.getBuild())
                .hpum((byte) 0)
                .becaPorcentaje(BigDecimal.ZERO)
                .build());
        log.debug("ChequeraSerie -> {}", Jsonifier.builder(chequeraSerie).build());

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
            log.debug("chequera_cuota -> {}", chequeraCuota.jsonify());
            chequeraCuota.setCodigoBarras(chequeraCuotaService.calculateCodigoBarras(chequeraCuota));
            chequeraCuotas.add(chequeraCuota);
        }
        chequeraCuotas = chequeraCuotaService.saveAll(chequeraCuotas);
        log.debug("ChequeraCuotas -> {}", Jsonifier.builder(chequeraCuotas).build());
        return chequeraSerie;
    }

}

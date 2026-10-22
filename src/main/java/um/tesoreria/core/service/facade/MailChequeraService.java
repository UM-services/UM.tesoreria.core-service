package um.tesoreria.core.service.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.event.CorreoPayload;
import um.tesoreria.core.event.Remitente;
import um.tesoreria.core.event.SendChequeraEvent;
import um.tesoreria.core.exception.CarreraChequeraException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.personas.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.exception.SpoterDataException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto.ChequeraSerieDto;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto.SpoterDataResponse;
import um.tesoreria.core.service.*;
import um.tesoreria.core.service.transactional.spoter.SpoterService;
import um.tesoreria.core.util.Tool;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailChequeraService {

    static final String MENSAJE_ENVIO = "Solicitud de envío en proceso";
    static final String MENSAJE_SIN_CORREOS = "ERROR: Sin correos para ENVIAR";
    private static final String REPLY_TO = "no-reply@um.edu.ar";

    private final ChequeraSerieService chequeraSerieService;
    private final DomicilioService domicilioService;
    private final FacultadService facultadService;
    private final PersonaService personaService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final LectivoService lectivoService;
    private final CarreraChequeraService carreraChequeraService;
    private final CursoService cursoService;
    private final SpoterDataService spoterDataService;
    private final ChequeraService chequeraService;
    private final SpoterService spoterService;
    private final CorreoEventPublisher correoEventPublisher;

    public String sendChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId,
            Boolean copiaInformes, Boolean incluyeMatricula, Boolean codigoBarras) {
        log.debug("\n\nProcessing MailChequeraService.sendChequera\n\n");
        var event = SendChequeraEvent.builder()
                .facultadId(facultadId)
                .tipoChequeraId(tipoChequeraId)
                .chequeraSerieId(chequeraSerieId)
                .alternativaId(alternativaId)
                .copiaInformes(copiaInformes)
                .incluyeMatricula(incluyeMatricula)
                .codigoBarras(codigoBarras)
                .build();
        log.debug("Publishing SendChequeraEvent: {}", event);
        correoEventPublisher.publicarChequera(event);
        return MENSAJE_ENVIO;
    }

    public String sendCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId,
            Integer productoId, Integer cuotaId,
            Boolean copiaInformes, Boolean incluyeMatricula) {
        log.debug("mail_chequera_service.send_cuota");
        Long chequeraCuotaId = chequeraCuotaService
                .findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId)
                .getChequeraCuotaId();
        correoEventPublisher.publicarCuota(chequeraCuotaId);
        return MENSAJE_ENVIO;
    }

    public SpoterDataResponse sendChequeraPreSpoter(SpoterData spoterData, Boolean updateMailPersonal,
            Boolean responseSinEnvio) {
        log.debug("Processing MailChequeraService.sendChequeraPreSpoter");
        log.debug("spoterData={}", spoterData.jsonify());
        // Determina lectivoId
        Lectivo lectivo;
        OffsetDateTime ahora = Tool.dateAbsoluteArgentina();
        Integer lectivoId = (lectivo = lectivoService.findByFecha(ahora)).getLectivoId();
        log.debug("Lectivo={}", lectivo.jsonify());
        // si corresponde al segundo semestre entonces la genera para el ciclo lectivo
        // siguiente
        OffsetDateTime referenciaHasta = lectivo.getFechaFinal();

        // Ajusta la referenciaDesde para que sea en septiembre del año anterior
        assert referenciaHasta != null;
        OffsetDateTime referenciaDesde = referenciaHasta.minusYears(1).withMonth(8).withDayOfMonth(1);

        if (ahora.isAfter(referenciaDesde) && ahora.isBefore(referenciaHasta)) {
            assert lectivoId != null;
            lectivo = lectivoService.findByLectivoId(++lectivoId);
            log.debug("Lectivo ajustado por fecha");
        }
        log.debug("Lectivo={}", lectivo.jsonify());

        if (updateMailPersonal) {
            try {
                Domicilio domicilio = domicilioService.findByUnique(spoterData.getPersonaId(),
                        spoterData.getDocumentoId());
                domicilio.setEmailInstitucional(spoterData.getEmailPersonal());
                domicilio = domicilioService.update(domicilio.getDomicilioId(), domicilio)
                        .orElseThrow();
                log.debug("Domicilio={}", domicilio.jsonify());
            } catch (DomicilioException e) {
                log.debug("Persona SIN Domicilio para Actualizar");
            }
        }
        // Verifica si ya existe chequera asociada
        log.debug("Verificando si existe chequera asociada");
        try {
            SpoterData data = spoterDataService.findOne(spoterData.getPersonaId(),
                    spoterData.getDocumentoId(), spoterData.getFacultadId(), spoterData.getGeograficaId(), lectivoId);
            log.debug("SporterData leído");
            log.debug("SpoterData={}", spoterData.jsonify());
            ChequeraSerieDto chequeraSerieDTO = chequeraService.constructChequeraDataDTO(chequeraSerieService
                    .findByUnique(data.getFacultadId(), data.getTipoChequeraId(), data.getChequeraSerieId()));
            String messageSender = "Response Sin Envío";
            if (!responseSinEnvio) {
                messageSender = this.sendChequera(data.getFacultadId(), data.getTipoChequeraId(),
                        data.getChequeraSerieId(), data.getAlternativaId(), false, false, false);
            }
            return new SpoterDataResponse(true, messageSender, data.getFacultadId(), data.getTipoChequeraId(),
                    data.getChequeraSerieId(), chequeraSerieDTO);
        } catch (SpoterDataException e) {
            log.debug("SpoterData error -> {}", e.getMessage());
        }
        // Determina curso
        Curso curso = cursoService.findTopByClaseChequera(1);
        log.debug("Curso={}", curso.jsonify());
        // Determina facultadId, lectivoId, tipoChequeraId, chequeraSerieId,
        // arancelTipoId, alternativaId
        CarreraChequera carreraChequera;
        try {
            carreraChequera = carreraChequeraService.findByUnique(spoterData.getFacultadId(), lectivoId,
                    spoterData.getPlanId(), spoterData.getCarreraId(), 1, curso.getCursoId(),
                    spoterData.getGeograficaId());
            log.debug("CarreraChequera -> {}", carreraChequera.jsonify());
        } catch (CarreraChequeraException e) {
            log.debug(e.getMessage());
            return new SpoterDataResponse(false, "SIN Tipo Chequera ASIGNADA", null, null, null, null);
        }

        // Genera la chequera nueva con los datos encontrados
        ChequeraSerie chequeraSerie = spoterService.makeChequeraSpoter(spoterData, lectivoId, curso, carreraChequera);
        log.debug("ChequeraSerie={}", chequeraSerie.jsonify());

        // Enviar chequera
        String message = this.sendChequera(chequeraSerie.getFacultadId(), chequeraSerie.getTipoChequeraId(),
                chequeraSerie.getChequeraSerieId(), chequeraSerie.getAlternativaId(), false, false, false);
        log.debug("MailChequeraService.sendChequeraPreSpoter - message -> {}", message);
        // Registrar SpoterData
        spoterData.setLectivoId(lectivoId);
        spoterData.setStatus(MENSAJE_ENVIO.equals(message) ? (byte) 1 : (byte) 0);
        spoterData.setMessage(message);
        spoterData.setTipoChequeraId(chequeraSerie.getTipoChequeraId());
        spoterData.setChequeraSerieId(chequeraSerie.getChequeraSerieId());
        spoterData.setAlternativaId(chequeraSerie.getAlternativaId());
        spoterData = spoterDataService.add(spoterData);
        log.debug("SpoterData={}", spoterData.jsonify());
        log.debug("Construyendo chequeraDTO");
        ChequeraSerieDto chequeraSerieDTO = chequeraService.constructChequeraDataDTO(chequeraSerie);
        log.debug("ChequeraSerieDto -> {}", chequeraSerieDTO.jsonify());
        return new SpoterDataResponse(spoterData.getStatus() == (byte) 1, spoterData.getMessage(),
                spoterData.getFacultadId(), spoterData.getTipoChequeraId(), spoterData.getChequeraSerieId(),
                chequeraSerieDTO);
    }

    public String notificaDeudor(BigDecimal personaId, Integer documentoId) {
        String data;

        Domicilio domicilio;
        try {
            domicilio = domicilioService.findByUnique(personaId, documentoId);
        } catch (DomicilioException e) {
            return "ERROR: Sin correos para ENVIAR";
        }

        data = "Estimad@ Estudiante:" + (char) 10;
        data = data + (char) 10;
        data = data + "Le informamos que debido a que en nuestros registros consta una deuda previa," + (char) 10;
        data = data + "dejaremos en suspenso la matriculación solicitada para el presente ciclo lectivo" + (char) 10;
        data = data + "hasta tanto pueda regularizar su situación." + (char) 10;
        data = data + (char) 10;
        data = data + "Le pedimos que se comunique a la mayor brevedad al correo tesoreria@um.edu.ar" + (char) 10;
        data = data + (char) 10;
        data = data + "Atentamente." + (char) 10;
        data = data + (char) 10;
        data = data + "Universidad de Mendoza" + (char) 10;
        data = data + (char) 10;
        data = data + (char) 10
                + "Por favor no responda este mail, fue generado automáticamente. Su respuesta no será leída."
                + (char) 10;

        // Envia correo
        List<String> addresses = destinatarios(domicilio);
        if (addresses.isEmpty()) {
            return MENSAJE_SIN_CORREOS;
        }
        correoEventPublisher.publicar(CorreoPayload.builder()
                .remitente(Remitente.NOTIFICACION)
                .para(addresses)
                .replyTo(REPLY_TO)
                .asunto("Notificación por Deuda")
                .cuerpo(data)
                .build());

        return MENSAJE_ENVIO;
    }

    public String notificaDeudorChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        String data;

        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        Domicilio domicilio;
        try {
            domicilio = domicilioService.findByUnique(serie.getPersonaId(), serie.getDocumentoId());
        } catch (DomicilioException e) {
            return MENSAJE_SIN_CORREOS;
        }

        data = notaDeudorChequera(facultadId, tipoChequeraId, chequeraSerieId);

        // Envia correo
        List<String> addresses = destinatarios(domicilio);
        if (addresses.isEmpty()) {
            return MENSAJE_SIN_CORREOS;
        }
        correoEventPublisher.publicar(CorreoPayload.builder()
                .remitente(Remitente.NOTIFICACION)
                .para(addresses)
                .replyTo(REPLY_TO)
                .asunto("Notificación por Deuda")
                .cuerpo(data)
                .build());

        return MENSAJE_ENVIO;
    }

    public String notaDeudorChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        Facultad facultad = facultadService.findByFacultadId(facultadId);
        Persona persona = personaService.findByUnique(serie.getPersonaId(), serie.getDocumentoId());

        StringBuilder data = new StringBuilder("" + (char) 10);
        data.append("UNIVERSIDAD DE MENDOZA" + (char) 10);
        data.append("Rectorado - Arístides Villanueva 794 (5500) Mendoza" + (char) 10);
        data.append((char) 10);
        data.append(persona.getApellido()).append(", ").append(persona.getNombre()).append(" (").append(persona.getPersonaId()).append(")").append((char) 10);
        data.append(facultad.getNombre()).append((char) 10);
        data.append((char) 10);
        data.append("De nuestra mayor consideración:" + (char) 10);
        data.append("         Tenemos el agrado de dirigirnos a Ud. a efectos de llevar a su conocimiento que" + (char) 10);
        data.append("al ").append(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(OffsetDateTime.now())).append(" figuran en nuestros registros pendientes de cancelación las").append((char) 10);
        data.append("siguientes cuotas. Por tal motivo le rogamos tenga a bien regularizar su situación arancelaria," + (char) 10);
        data.append("para lo cual podrá dirigir un e-mail a tesoreria@um.edu.ar a efectos de generar la chequera" + (char) 10);
        data.append("de pago correspondiente." + (char) 10);
        data.append((char) 10);
        data.append("                   Detalle DEUDA Chequera: ").append(serie.getFacultadId()).append("/").append(serie.getTipoChequeraId()).append("/").append(serie.getChequeraSerieId()).append((char) 10);
        data.append((char) 10);
        for (ChequeraCuota cuota : chequeraCuotaService.findAllDebidas(facultadId, tipoChequeraId, chequeraSerieId,
                serie.getAlternativaId(), OffsetDateTime.now())) {
            data.append("         Período: ").append(String.format("%02d/%04d", cuota.getMes(), cuota.getAnho())).append("     Vencimiento: ").append(DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    .format(Objects.requireNonNull(cuota.getVencimiento1())
                            .withOffsetSameInstant(ZoneOffset.UTC))).append("     Importe: ").append(cuota.getImporte1().setScale(2, RoundingMode.HALF_UP)).append((char) 10);

        }
        data.append((char) 10);
        data.append("         Del mismo modo deseamos recordarle que, a fin de poder acceder a las próximas" + (char) 10);
        data.append("Mesas de Exámenes, deberá encontrarse al día con sus pagos." + (char) 10);
        data.append((char) 10);
        data.append("         Por último le recordamos que, si ha decidido no continuar sus estudios durante" + (char) 10);
        data.append("el presente Ciclo Lectivo, debe comunicar esta decisión lo antes posible a la Secretaría" + (char) 10);
        data.append("de su Facultad, de modo de no continuar generando deuda." + (char) 10);
        data.append((char) 10);
        data.append("         Si al recibir la presente, su deuda estuviese cancelada, le pedimos disculpas y" + (char) 10);
        data.append("le solicitamos remitir un e-mail a tesoreria@um.edu.ar a fin de tomar nota de los pagos" + (char) 10);
        data.append("realizados." + (char) 10);
        data.append((char) 10);
        data.append("         Le saludamos cordialmente." + (char) 10);
        data.append((char) 10);
        data.append((char) 10);
        data.append("Por favor no responda este mail, fue generado automáticamente. Su respuesta no será leída." + (char) 10);

        return data.toString();
    }

    private List<String> destinatarios(Domicilio domicilio) {
        List<String> direcciones = new ArrayList<>();
        if (domicilio.getEmailPersonal() != null && !domicilio.getEmailPersonal().isEmpty()) {
            direcciones.add(domicilio.getEmailPersonal());
        }
        if (domicilio.getEmailInstitucional() != null && !domicilio.getEmailInstitucional().isEmpty()) {
            direcciones.add(domicilio.getEmailInstitucional());
        }
        return direcciones;
    }

}

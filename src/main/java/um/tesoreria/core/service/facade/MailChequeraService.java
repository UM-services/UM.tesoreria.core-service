package um.tesoreria.core.service.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import um.tesoreria.core.client.tesoreria.sender.ChequeraClient;
import um.tesoreria.core.exception.CarreraChequeraException;
import um.tesoreria.core.exception.DomicilioException;
import um.tesoreria.core.exception.SpoterDataException;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.kotlin.model.dto.ChequeraSerieDTO;
import um.tesoreria.core.kotlin.model.dto.SpoterDataResponse;
import um.tesoreria.core.kotlin.model.dto.message.ChequeraMessageDto;
import um.tesoreria.core.model.MailSender;
import um.tesoreria.core.service.*;
import um.tesoreria.core.service.queue.ChequeraQueueService;
import um.tesoreria.core.service.transactional.spoter.SpoterService;
import um.tesoreria.core.util.Tool;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MailChequeraService {

    private final JavaMailSender javaMailSender;
    private final ChequeraSerieService chequeraSerieService;
    private final DomicilioService domicilioService;
    private final FacultadService facultadService;
    private final PersonaService personaService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final LectivoService lectivoService;
    private final CarreraChequeraService carreraChequeraService;
    private final CursoService cursoService;
    private final SpoterDataService spoterDataService;
    private final MailSenderService mailSenderService;
    private final ChequeraService chequeraService;
    private final ChequeraClient chequeraClient;
    private final SpoterService spoterService;
    private final ChequeraQueueService chequeraQueueService;

    public MailChequeraService(JavaMailSender javaMailSender,
                               ChequeraSerieService chequeraSerieService,
                               DomicilioService domicilioService,
                               FacultadService facultadService,
                               PersonaService personaService,
                               ChequeraCuotaService chequeraCuotaService,
                               LectivoService lectivoService,
                               CarreraChequeraService carreraChequeraService,
                               CursoService cursoService,
                               SpoterDataService spoterDataService,
                               MailSenderService mailSenderService,
                               ChequeraService chequeraService,
                               ChequeraClient chequeraClient,
                               SpoterService spoterService,
                               ChequeraQueueService chequeraQueueService) {
        this.javaMailSender = javaMailSender;
        this.chequeraSerieService = chequeraSerieService;
        this.domicilioService = domicilioService;
        this.facultadService = facultadService;
        this.personaService = personaService;
        this.chequeraCuotaService = chequeraCuotaService;
        this.lectivoService = lectivoService;
        this.carreraChequeraService = carreraChequeraService;
        this.cursoService = cursoService;
        this.spoterDataService = spoterDataService;
        this.mailSenderService = mailSenderService;
        this.chequeraService = chequeraService;
        this.chequeraClient = chequeraClient;
        this.spoterService = spoterService;
        this.chequeraQueueService = chequeraQueueService;
    }

    public String sendChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId,
                               Boolean copiaInformes, Boolean incluyeMatricula, Boolean codigoBarras) {
        log.debug("Processing MailChequeraService.sendChequera");
        return chequeraClient.sendChequera(facultadId, tipoChequeraId, chequeraSerieId, alternativaId, copiaInformes, codigoBarras);
    }

    public String sendChequeraByQueue(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Boolean copiaInformes, Boolean incluyeMatricula, Boolean codigoBarras) {
        log.debug("Processing MailChequeraService.sendChequeraByQueue");
        chequeraQueueService.sendChequeraQueue(new ChequeraMessageDto.Builder()
                .uuid(UUID.randomUUID())
                .facultadId(facultadId)
                .tipoChequeraId(tipoChequeraId)
                .chequeraSerieId(chequeraSerieId)
                .alternativaId(alternativaId)
                .copiaInformes(copiaInformes)
                .codigoBarras(codigoBarras)
                .incluyeMatricula(incluyeMatricula)
                .build()
        );
        return "Envío de Chequera Solicitado";
    }

    public String sendCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Integer productoId, Integer cuotaId,
                            Boolean copiaInformes, Boolean incluyeMatricula) {
        MailSender javaMailSender = mailSenderService.findSender();
        log.debug("mail_chequera_service.send_chequera.javaMailSender={}", javaMailSender);
        String url = MessageFormat.format("http://{0}:{1}", javaMailSender.getIpAddress(), javaMailSender.getPort().toString());
        log.debug("mail_chequera_service.send_chequera.url={}", url);
        WebClient webClient = WebClient.builder().baseUrl(url + "/chequera").build();
        String uri = MessageFormat.format("/sendCuota/{0}/{1}/{2}/{3}/{4}/{5}/{6}/{7}", facultadId.toString(),
                tipoChequeraId.toString(), chequeraSerieId.toString(), alternativaId.toString(), productoId.toString(), cuotaId.toString(),
                copiaInformes ? "true" : "false", incluyeMatricula ? "true" : "false");
        log.debug("mail_chequera_service.send_cuota.uri={}", uri);
        Mono<String> mono = webClient.get()
                .uri(uri)
                .retrieve().bodyToMono(String.class);
        return mono.block();
    }

    public SpoterDataResponse sendChequeraPreSpoter(SpoterData spoterData, Boolean updateMailPersonal, Boolean responseSinEnvio) {
        log.debug("Processing MailChequeraService.sendChequeraPreSpoter");
        logSpoterData(spoterData);
// Determina lectivoId
        Lectivo lectivo;
        OffsetDateTime ahora = Tool.dateAbsoluteArgentina();
        Integer lectivoId = (lectivo = lectivoService.findByFecha(ahora)).getLectivoId();
        logLectivo(lectivo);
// si corresponde al segundo semestre entonces la genera para el ciclo lectivo siguiente
        OffsetDateTime referenciaHasta = lectivo.getFechaFinal();

// Ajusta la referenciaDesde para que sea en septiembre del año anterior
        assert referenciaHasta != null;
        OffsetDateTime referenciaDesde = referenciaHasta.minusYears(1).withMonth(8).withDayOfMonth(1);

        if (ahora.isAfter(referenciaDesde) && ahora.isBefore(referenciaHasta)) {
            assert lectivoId != null;
            lectivo = lectivoService.findByLectivoId(++lectivoId);
            log.debug("Lectivo ajustado por fecha");
        }
        logLectivo(lectivo);

        if (updateMailPersonal) {
            try {
                Domicilio domicilio = domicilioService.findByUnique(spoterData.getPersonaId(), spoterData.getDocumentoId());
                domicilio.setEmailInstitucional(spoterData.getEmailPersonal());
                domicilio = domicilioService.update(domicilio, domicilio.getDomicilioId(), true);
                logDomicilio(domicilio);
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
            logSpoterData(data);
            ChequeraSerieDTO chequeraSerieDTO = chequeraService.constructChequeraDataDTO(chequeraSerieService.findByUnique(data.getFacultadId(), data.getTipoChequeraId(), data.getChequeraSerieId()));
            String messageSender = "Response Sin Envío";
            if (!responseSinEnvio) {
                messageSender = this.sendChequera(data.getFacultadId(), data.getTipoChequeraId(), data.getChequeraSerieId(), data.getAlternativaId(), false, false, false);
            }
            return new SpoterDataResponse(true, messageSender, data.getFacultadId(), data.getTipoChequeraId(), data.getChequeraSerieId(), chequeraSerieDTO);
        } catch (SpoterDataException e) {
            log.debug("SpoterData error -> {}", e.getMessage());
        }
        // Determina curso
        Curso curso = cursoService.findTopByClaseChequera(1);
        logCurso(curso);
        // Determina facultadId, lectivoId, tipoChequeraId, chequeraSerieId,
        // arancelTipoId, alternativaId
        CarreraChequera carreraChequera;
        try {
            carreraChequera = carreraChequeraService.findByUnique(spoterData.getFacultadId(), lectivoId,
                    spoterData.getPlanId(), spoterData.getCarreraId(), 1, curso.getCursoId(),
                    spoterData.getGeograficaId());
            logCarreraChequera(carreraChequera);
        } catch (CarreraChequeraException e) {
            log.debug(e.getMessage());
            return new SpoterDataResponse(false, "SIN Tipo Chequera ASIGNADA", null, null, null, null);
        }

        // Genera la chequera nueva con los datos encontrados
        ChequeraSerie chequeraSerie = spoterService.makeChequeraSpoter(spoterData, lectivoId, curso, carreraChequera);
        logChequeraSerie(chequeraSerie);

        // Enviar chequera
        String message = this.sendChequera(chequeraSerie.getFacultadId(), chequeraSerie.getTipoChequeraId(),
                chequeraSerie.getChequeraSerieId(), chequeraSerie.getAlternativaId(), false, false, false);
        log.debug("MailChequeraService.sendChequeraPreSpoter - message -> {}", message);
        // Registrar SpoterData
        spoterData.setLectivoId(lectivoId);
        spoterData.setStatus(message.equals("Envío de Chequera Ok!!!") ? (byte) 1 : (byte) 0);
        spoterData.setMessage(message);
        spoterData.setTipoChequeraId(chequeraSerie.getTipoChequeraId());
        spoterData.setChequeraSerieId(chequeraSerie.getChequeraSerieId());
        spoterData.setAlternativaId(chequeraSerie.getAlternativaId());
        spoterData = spoterDataService.add(spoterData);
        logSpoterData(spoterData);
        log.debug("Construyendo chequeraDTO");
        ChequeraSerieDTO chequeraSerieDTO = chequeraService.constructChequeraDataDTO(chequeraSerie);
        logChequeraSerieDTO(chequeraSerieDTO);
        return new SpoterDataResponse(spoterData.getStatus() == (byte) 1, spoterData.getMessage(),
                spoterData.getFacultadId(), spoterData.getTipoChequeraId(), spoterData.getChequeraSerieId(), chequeraSerieDTO);
    }

    public String notificaDeudor(BigDecimal personaId, Integer documentoId) throws MessagingException {
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
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        List<String> addresses = new ArrayList<>();

        if (!domicilio.getEmailPersonal().isEmpty())
            addresses.add(domicilio.getEmailPersonal());
        if (!domicilio.getEmailInstitucional().isEmpty())
            addresses.add(domicilio.getEmailInstitucional());

//		addresses.add("daniel.quinterospinto@gmail.com");
        try {
            helper.setTo(addresses.toArray(new String[0]));
            helper.setText(data);
            helper.setReplyTo("no-reply@um.edu.ar");
            helper.setSubject("Notificación por Deuda");
        } catch (MessagingException e) {
            log.error("ERROR: No pudo ENVIARSE -> {}", e.getMessage());
            return "ERROR: No pudo ENVIARSE";
        }
        javaMailSender.send(message);

        return "Envío de Correo Ok!!";
    }

    public String notificaDeudorChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId)
            throws MessagingException {
        String data;

        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        Domicilio domicilio;
        try {
            domicilio = domicilioService.findByUnique(serie.getPersonaId(), serie.getDocumentoId());
        } catch (DomicilioException e) {
            return "ERROR: Sin correos para ENVIAR";
        }

        data = notaDeudorChequera(facultadId, tipoChequeraId, chequeraSerieId);

        // Envia correo
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        List<String> addresses = new ArrayList<>();

        if (!domicilio.getEmailPersonal().isEmpty())
            addresses.add(domicilio.getEmailPersonal());
        if (!domicilio.getEmailInstitucional().isEmpty())
            addresses.add(domicilio.getEmailInstitucional());

//		addresses.add("daniel.quinterospinto@gmail.com");
        try {
            helper.setTo(addresses.toArray(new String[0]));
            helper.setText(data);
            helper.setReplyTo("no-reply@um.edu.ar");
            helper.setSubject("Notificación por Deuda");
        } catch (MessagingException e) {
            log.error("ERROR: No pudo ENVIARSE -> {}", e.getMessage());
            return "ERROR: No pudo ENVIARSE";
        }
        javaMailSender.send(message);

        return "Envio de Correo Ok!";
    }

    public String notaDeudorChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        Facultad facultad = facultadService.findByFacultadId(facultadId);
        Persona persona = personaService.findByUnique(serie.getPersonaId(), serie.getDocumentoId());

        String data = "" + (char) 10;
        data += "UNIVERSIDAD DE MENDOZA" + (char) 10;
        data += "Rectorado - Arístides Villanueva 794 (5500) Mendoza" + (char) 10;
        data += (char) 10;
        data += persona.getApellido() + ", " + persona.getNombre() + " (" + persona.getPersonaId() + ")" + (char) 10;
        data += facultad.getNombre() + (char) 10;
        data += (char) 10;
        data += "De nuestra mayor consideración:" + (char) 10;
        data += "         Tenemos el agrado de dirigirnos a Ud. a efectos de llevar a su conocimiento que" + (char) 10;
        data += "al " + DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(OffsetDateTime.now())
                + " figuran en nuestros registros pendientes de cancelación las" + (char) 10;
        data += "siguientes cuotas. Por tal motivo le rogamos tenga a bien regularizar su situación arancelaria,"
                + (char) 10;
        data += "para lo cual podrá dirigir un e-mail a tesoreria@um.edu.ar a efectos de generar la chequera"
                + (char) 10;
        data += "de pago correspondiente." + (char) 10;
        data += (char) 10;
        data += "                   Detalle DEUDA Chequera: " + serie.getFacultadId() + "/" + serie.getTipoChequeraId()
                + "/" + serie.getChequeraSerieId() + (char) 10;
        data += (char) 10;
        for (ChequeraCuota cuota : chequeraCuotaService.findAllDebidas(facultadId, tipoChequeraId, chequeraSerieId,
                serie.getAlternativaId())) {
            data += "         Período: " + String.format("%02d/%04d", cuota.getMes(), cuota.getAnho())
                    + "     Vencimiento: "
                    + DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    .format(Objects.requireNonNull(cuota.getVencimiento1()).withOffsetSameInstant(ZoneOffset.UTC))
                    + "     Importe: " + cuota.getImporte1().setScale(2, RoundingMode.HALF_UP) + (char) 10;

        }
        data += (char) 10;
        data += "         Del mismo modo deseamos recordarle que, a fin de poder acceder a las próximas" + (char) 10;
        data += "Mesas de Exámenes, deberá encontrarse al día con sus pagos." + (char) 10;
        data += (char) 10;
        data += "         Por último le recordamos que, si ha decidido no continuar sus estudios durante" + (char) 10;
        data += "el presente Ciclo Lectivo, debe comunicar esta decisión lo antes posible a la Secretaría" + (char) 10;
        data += "de su Facultad, de modo de no continuar generando deuda." + (char) 10;
        data += (char) 10;
        data += "         Si al recibir la presente, su deuda estuviese cancelada, le pedimos disculpas y" + (char) 10;
        data += "le solicitamos remitir un e-mail a tesoreria@um.edu.ar a fin de tomar nota de los pagos" + (char) 10;
        data += "realizados." + (char) 10;
        data += (char) 10;
        data += "         Le saludamos cordialmente." + (char) 10;
        data += (char) 10;
        data += (char) 10;
        data += "Por favor no responda este mail, fue generado automáticamente. Su respuesta no será leída."
                + (char) 10;

        return data;
    }

    private void logLectivo(Lectivo lectivo) {
        try {
            log.debug("Lectivo -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(lectivo));
        } catch (JsonProcessingException e) {
            log.debug("Lectivo jsonify error -> {}", e.getMessage());
        }
    }

    private void logDomicilio(Domicilio domicilio) {
        try {
            log.debug("Domicilio -> {}", JsonMapper.
                    builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(domicilio));
        } catch (JsonProcessingException e) {
            log.debug("Domicilio jsonify error -> {}", e.getMessage());
        }
    }

    private void logCurso(Curso curso) {
        try {
            log.debug("Curso -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(curso));
        } catch (JsonProcessingException e) {
            log.debug("Curso jsonify error -> {}", e.getMessage());
        }
    }

    private void logSpoterData(SpoterData data) {
        try {
            log.debug("SpoterData -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(data));
        } catch (JsonProcessingException e) {
            log.debug("SpoterData jsonify error -> {}", e.getMessage());
        }
    }

    private void logCarreraChequera(CarreraChequera carreraChequera) {
        try {
            log.debug("CarreraChequera -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(carreraChequera));
        } catch (JsonProcessingException e) {
            log.debug("CarreraChequera jsonify error -> {}", e.getMessage());
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

    private void logChequeraSerieDTO(ChequeraSerieDTO chequeraSerieDTO) {
        try {
            log.debug("ChequeraSerieDTO -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraSerieDTO));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraSerieDTO jsonify error -> {}", e.getMessage());
        }
    }

}

package ar.edu.um.tesoreria.rest.service.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.*;
import ar.edu.um.tesoreria.rest.kotlin.model.dto.ChequeraSerieDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.springframework.transaction.annotation.Transactional;

import ar.edu.um.tesoreria.rest.exception.CarreraChequeraException;
import ar.edu.um.tesoreria.rest.exception.ChequeraSerieControlException;
import ar.edu.um.tesoreria.rest.exception.DomicilioException;
import ar.edu.um.tesoreria.rest.exception.LegajoException;
import ar.edu.um.tesoreria.rest.exception.PersonaException;
import ar.edu.um.tesoreria.rest.exception.SpoterDataException;
import ar.edu.um.tesoreria.rest.model.CarreraChequera;
import ar.edu.um.tesoreria.rest.model.ChequeraAlternativa;
import ar.edu.um.tesoreria.rest.model.ChequeraSerieControl;
import ar.edu.um.tesoreria.rest.model.ChequeraTotal;
import ar.edu.um.tesoreria.rest.model.Curso;
import ar.edu.um.tesoreria.rest.model.LectivoAlternativa;
import ar.edu.um.tesoreria.rest.model.LectivoCuota;
import ar.edu.um.tesoreria.rest.model.LectivoTotal;
import ar.edu.um.tesoreria.rest.model.Legajo;
import ar.edu.um.tesoreria.rest.model.MailSender;
import ar.edu.um.tesoreria.rest.model.SpoterData;
import ar.edu.um.tesoreria.rest.kotlin.model.dto.SpoterDataResponse;
import ar.edu.um.tesoreria.rest.service.CarreraChequeraService;
import ar.edu.um.tesoreria.rest.service.ChequeraAlternativaService;
import ar.edu.um.tesoreria.rest.service.ChequeraCuotaService;
import ar.edu.um.tesoreria.rest.service.ChequeraSerieControlService;
import ar.edu.um.tesoreria.rest.service.ChequeraSerieService;
import ar.edu.um.tesoreria.rest.service.ChequeraTotalService;
import ar.edu.um.tesoreria.rest.service.CursoService;
import ar.edu.um.tesoreria.rest.service.DomicilioService;
import ar.edu.um.tesoreria.rest.service.FacultadService;
import ar.edu.um.tesoreria.rest.service.LectivoAlternativaService;
import ar.edu.um.tesoreria.rest.service.LectivoCuotaService;
import ar.edu.um.tesoreria.rest.service.LectivoService;
import ar.edu.um.tesoreria.rest.service.LectivoTotalService;
import ar.edu.um.tesoreria.rest.service.LegajoService;
import ar.edu.um.tesoreria.rest.service.MailSenderService;
import ar.edu.um.tesoreria.rest.service.PersonaService;
import ar.edu.um.tesoreria.rest.service.SpoterDataService;
import ar.edu.um.tesoreria.rest.util.Tool;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MailChequeraService {

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private ChequeraSerieService chequeraSerieService;

    @Autowired
    private DomicilioService domicilioService;

    @Autowired
    private FacultadService facultadService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private ChequeraCuotaService chequeraCuotaService;

    @Autowired
    private LectivoService lectivoService;

    @Autowired
    private CarreraChequeraService carreraChequeraService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private ChequeraSerieControlService chequeraSerieControlService;

    @Autowired
    private LegajoService legajoService;

    @Autowired
    private SpoterDataService spoterDataService;

    @Autowired
    private LectivoTotalService lectivoTotalService;

    @Autowired
    private ChequeraTotalService chequeraTotalService;

    @Autowired
    private LectivoAlternativaService lectivoAlternativaService;

    @Autowired
    private ChequeraAlternativaService chequeraAlternativaService;

    @Autowired
    private LectivoCuotaService lectivoCuotaService;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private ChequeraService chequeraService;

    public String sendChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId,
                               Boolean copiaInformes, Boolean incluyeMatricula) throws MessagingException {
        MailSender sender = mailSenderService.findSender();
        String url = MessageFormat.format("http://{0}:{1}", sender.getIpAddress(), sender.getPort().toString());
        WebClient webClient = WebClient.builder().baseUrl(url + "/chequera").build();
        Mono<String> mono = webClient.get()
                .uri(MessageFormat.format("/sendChequera/{0}/{1}/{2}/{3}/{4}/{5}", facultadId.toString(),
                        tipoChequeraId.toString(), chequeraSerieId.toString(), alternativaId.toString(),
                        copiaInformes ? "true" : "false", incluyeMatricula ? "true" : "false"))
                .retrieve().bodyToMono(String.class);
        return mono.block();
    }

    public SpoterDataResponse sendChequeraPreSpoter(SpoterData spoterData, Boolean updateMailPersonal, Boolean responseSinEnvio) throws MessagingException {
        // Determina lectivoId
        Lectivo lectivo = null;
        Integer lectivoId = (lectivo = lectivoService.findByFecha(Tool.dateAbsoluteArgentina())).getLectivoId();

        try {
            log.debug("Lectivo -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(lectivo));
        } catch (JsonProcessingException e1) {
            log.debug("Lectivo -> error");
        }
        if (updateMailPersonal) {
            try {
                Domicilio domicilio = domicilioService.findByUnique(spoterData.getPersonaId(), spoterData.getDocumentoId());
                domicilio.setEmailInstitucional(spoterData.getEmailPersonal());
                domicilio = domicilioService.update(domicilio, domicilio.getDomicilioId(), true);
                try {
                    log.debug("Domicilio -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
                            .writeValueAsString(domicilio));
                } catch (JsonProcessingException e1) {
                    log.debug("Domicilio -> error");
                }
            } catch (DomicilioException e) {
                log.debug("Persona SIN Domicilio para Actualizar");
            }
        }
        // Verifica si ya existe chequera asociada
        try {
            SpoterData data = spoterDataService.findExistentRequest(spoterData.getPersonaId(),
                    spoterData.getDocumentoId(), spoterData.getFacultadId(), spoterData.getGeograficaId(), lectivoId);
            try {
                log.debug("SpoterData previo -> {}", JsonMapper.builder().findAndAddModules().build()
                        .writerWithDefaultPrettyPrinter().writeValueAsString(data));
            } catch (JsonProcessingException e) {
                log.debug("SpoterData previo -> error");
            }
            ChequeraSerieDTO chequeraSerieDTO = chequeraService.constructChequeraDataDTO(chequeraSerieService.findByUnique(data.getFacultadId(), data.getTipoChequeraId(), data.getChequeraSerieId()));
            String messageSender = "Response Sin Envío";
            if (!responseSinEnvio) {
                messageSender = this.sendChequera(data.getFacultadId(), data.getTipoChequeraId(), data.getChequeraSerieId(), data.getAlternativaId(), false, false);
            }
            return new SpoterDataResponse(true, messageSender, data.getFacultadId(), data.getTipoChequeraId(), data.getChequeraSerieId(), chequeraSerieDTO);
        } catch (SpoterDataException e) {

        }
        // Determina curso
        Curso curso = cursoService.findTopByClaseChequera(1);
        try {
            log.debug("Curso -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(curso));
        } catch (JsonProcessingException e1) {
            log.debug("Curso -> error");
        }
        // Determina facultadId, lectivoId, tipoChequeraId, chequeraSerieId,
        // arancelTipoId, alternativaId
        CarreraChequera carreraChequera = null;
        try {
            carreraChequera = carreraChequeraService.findByUnique(spoterData.getFacultadId(), lectivoId,
                    spoterData.getPlanId(), spoterData.getCarreraId(), 1, curso.getCursoId(),
                    spoterData.getGeograficaId());
            try {
                log.debug("CarreraChequera -> {}", JsonMapper.builder().findAndAddModules().build()
                        .writerWithDefaultPrettyPrinter().writeValueAsString(carreraChequera));
            } catch (JsonProcessingException e) {
                log.debug("CarreraChequera -> error");
            }
        } catch (CarreraChequeraException e) {
            log.debug(e.getMessage());
            return new SpoterDataResponse(false, "SIN Tipo Chequera ASIGNADA", null, null, null, null);
        }

        // Genera la chequera nueva con los datos encontrados
        ChequeraSerie chequeraSerie = this.makeChequeraSpoter(spoterData, lectivoId, curso, carreraChequera);
        try {
            log.debug("ChequeraSerie -> {}", JsonMapper.builder().findAndAddModules().build()
                    .writerWithDefaultPrettyPrinter().writeValueAsString(chequeraSerie));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraSerie -> error");
        }

        // Enviar chequera
        String message = "";
        message = this.sendChequera(chequeraSerie.getFacultadId(), chequeraSerie.getTipoChequeraId(),
                chequeraSerie.getChequeraSerieId(), chequeraSerie.getAlternativaId(), false, false);
        // Registrar SpoterData
        spoterData.setLectivoId(lectivoId);
        spoterData.setStatus(message.equals("Envío de Correo Ok!!") ? (byte) 1 : (byte) 0);
        spoterData.setMessage(message);
        spoterData.setTipoChequeraId(chequeraSerie.getTipoChequeraId());
        spoterData.setChequeraSerieId(chequeraSerie.getChequeraSerieId());
        spoterData.setAlternativaId(chequeraSerie.getAlternativaId());
        spoterData = spoterDataService.add(spoterData);
        try {
            log.debug("SpoterData -> {}", JsonMapper.builder().findAndAddModules().build()
                    .writerWithDefaultPrettyPrinter().writeValueAsString(spoterData));
        } catch (JsonProcessingException e) {
            log.debug("SpoterData -> error");
        }
        log.debug("Construyendo chequeraDTO");
        ChequeraSerieDTO chequeraSerieDTO = chequeraService.constructChequeraDataDTO(chequeraSerie);
        try {
            log.debug("ChequeraSerieDTO -> {}", JsonMapper.builder().findAndAddModules().build()
                    .writerWithDefaultPrettyPrinter().writeValueAsString(chequeraSerieDTO));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraSerieDTO -> error");
        }
        return new SpoterDataResponse(spoterData.getStatus() == (byte) 1, spoterData.getMessage(),
                spoterData.getFacultadId(), spoterData.getTipoChequeraId(), spoterData.getChequeraSerieId(), chequeraSerieDTO);
    }

    @Transactional
    public ChequeraSerie makeChequeraSpoter(SpoterData spoterData, Integer lectivoId, Curso curso,
                                            CarreraChequera carreraChequera) {
        try {
            personaService.findByUnique(spoterData.getPersonaId(), spoterData.getDocumentoId());
        } catch (PersonaException e) {
            personaService.add(new Persona(null, spoterData.getPersonaId(), spoterData.getDocumentoId(),
                    spoterData.getApellido(), spoterData.getNombre(), "", (byte) 0, "", "", ""));
        }
        try {
            domicilioService.findByUnique(spoterData.getPersonaId(), spoterData.getDocumentoId());
        } catch (DomicilioException e) {
            domicilioService.add(new Domicilio(null, spoterData.getPersonaId(), spoterData.getDocumentoId(),
                    Tool.hourAbsoluteArgentina(), "", "", "", "", "", spoterData.getCelular(), "", "",
                    spoterData.getFacultadId(), null, null, spoterData.getEmailPersonal(), "", ""), false);
        }

        // Determinar el número de chequera usando ChequeraSerieControl
        ChequeraSerieControl chequeraSerieControl = null;
        Long chequeraSerieId = 1L;
        try {
            chequeraSerieControl = chequeraSerieControlService.findLastByTipoChequera(carreraChequera.getFacultadId(),
                    carreraChequera.getTipoChequeraId());
            chequeraSerieId = 1L + chequeraSerieControl.getChequeraSerieId();
        } catch (ChequeraSerieControlException e) {

        }
        // Llenar Legajo tesorería
        try {
            legajoService.findByFacultadIdAndPersonaIdAndDocumentoId(spoterData.getFacultadId(),
                    spoterData.getPersonaId(), spoterData.getDocumentoId());
        } catch (LegajoException e) {
            legajoService.add(new Legajo(null, spoterData.getPersonaId(), spoterData.getDocumentoId(),
                    spoterData.getFacultadId(), 0L, Tool.dateAbsoluteArgentina(), lectivoId, spoterData.getPlanId(),
                    spoterData.getCarreraId(), (byte) 1, spoterData.getGeograficaId(), "", (byte) 0));
        }
        // Generar ChequeraSerieControl
        chequeraSerieControl = chequeraSerieControlService
                .add(new ChequeraSerieControl(null, carreraChequera.getFacultadId(),
                        carreraChequera.getTipoChequeraId(), chequeraSerieId, (byte) 0, 0, 0, 0L, (byte) 0));
        try {
            log.debug("ChequeraSerieControl -> {}", JsonMapper.builder().findAndAddModules().build()
                    .writerWithDefaultPrettyPrinter().writeValueAsString(chequeraSerieControl));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraSerieControl -> error");
        }
        // Generar ChequeraSerie
        ChequeraSerie chequeraSerie = chequeraSerieService.add(new ChequeraSerie(null,
                chequeraSerieControl.getFacultadId(), chequeraSerieControl.getTipoChequeraId(),
                chequeraSerieControl.getChequeraSerieId(), spoterData.getPersonaId(), spoterData.getDocumentoId(),
                lectivoId, 1, curso.getCursoId(), (byte) 0, spoterData.getGeograficaId(), Tool.dateAbsoluteArgentina(),
                0, "Generated By Spoter", 1, (byte) 0, 2, (byte) 0, "fsarabia", (byte) 0, (byte) 0, 0, BigDecimal.ZERO,
                null, null, null, null, null, null, null));
        try {
            log.debug("ChequeraSerie -> {}", JsonMapper.builder().findAndAddModules().build()
                    .writerWithDefaultPrettyPrinter().writeValueAsString(chequeraSerie));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraSerie -> error");
        }
        // Generar ChequeraTotal
        List<ChequeraTotal> chequeraTotals = new ArrayList<ChequeraTotal>();
        for (LectivoTotal lectivoTotal : lectivoTotalService.findAllByTipo(chequeraSerie.getFacultadId(),
                chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId())) {
            chequeraTotals.add(new ChequeraTotal(null, chequeraSerie.getFacultadId(), chequeraSerie.getTipoChequeraId(),
                    chequeraSerie.getChequeraSerieId(), lectivoTotal.getProductoId(), lectivoTotal.getTotal(),
                    BigDecimal.ZERO));
        }
        chequeraTotals = chequeraTotalService.saveAll(chequeraTotals);
        try {
            log.debug("ChequeraTotal -> {}", JsonMapper.builder().findAndAddModules().build()
                    .writerWithDefaultPrettyPrinter().writeValueAsString(chequeraTotals));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraTotal -> error");
        }
        // Generar ChequeraAlternativa
        List<ChequeraAlternativa> chequeraAlternativas = new ArrayList<ChequeraAlternativa>();
        for (LectivoAlternativa lectivoAlternativa : lectivoAlternativaService.findAllByTipo(
                chequeraSerie.getFacultadId(), chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId(),
                chequeraSerie.getAlternativaId())) {
            chequeraAlternativas.add(new ChequeraAlternativa(null, chequeraSerie.getFacultadId(),
                    chequeraSerie.getTipoChequeraId(), chequeraSerie.getChequeraSerieId(),
                    lectivoAlternativa.getProductoId(), lectivoAlternativa.getAlternativaId(),
                    lectivoAlternativa.getTitulo(), lectivoAlternativa.getCuotas()));
        }
        chequeraAlternativas = chequeraAlternativaService.saveAll(chequeraAlternativas);
        try {
            log.debug("ChequeraAlternativa -> {}", JsonMapper.builder().findAndAddModules().build()
                    .writerWithDefaultPrettyPrinter().writeValueAsString(chequeraAlternativas));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraAlternativa -> error");
        }
        // Generar Cuotas
        List<ChequeraCuota> chequeraCuotas = new ArrayList<ChequeraCuota>();
        Integer offset = 0;
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
            ChequeraCuota chequeraCuota = new ChequeraCuota(null, chequeraSerie.getFacultadId(),
                    chequeraSerie.getTipoChequeraId(), chequeraSerie.getChequeraSerieId(), lectivoCuota.getProductoId(),
                    lectivoCuota.getAlternativaId(), lectivoCuota.getCuotaId(), lectivoCuota.getMes(),
                    lectivoCuota.getAnho(), chequeraSerie.getArancelTipoId(), vencimiento1, lectivoCuota.getImporte1(),
                    lectivoCuota.getImporte1(), vencimiento2, lectivoCuota.getImporte2(), lectivoCuota.getImporte2(),
                    vencimiento3, lectivoCuota.getImporte3(), lectivoCuota.getImporte3(), "", "", (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, 0, null, null, null, null);
            chequeraCuota.setCodigoBarras(chequeraCuotaService.calculateCodigoBarras(chequeraCuota));
            chequeraCuotas.add(chequeraCuota);
        }
        chequeraCuotas = chequeraCuotaService.saveAll(chequeraCuotas);
        try {
            log.debug("ChequeraCuota -> {}", JsonMapper.builder().findAndAddModules().build()
                    .writerWithDefaultPrettyPrinter().writeValueAsString(chequeraCuotas));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraCuota -> error");
        }
        return chequeraSerie;
    }

    public String notificaDeudor(BigDecimal personaId, Integer documentoId) throws MessagingException {
        String data = "";

        Domicilio domicilio = null;
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
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        List<String> addresses = new ArrayList<String>();

        if (!domicilio.getEmailPersonal().equals(""))
            addresses.add(domicilio.getEmailPersonal());
        if (!domicilio.getEmailInstitucional().equals(""))
            addresses.add(domicilio.getEmailInstitucional());

//		addresses.add("daniel.quinterospinto@gmail.com");
        try {
            helper.setTo(addresses.toArray(new String[addresses.size()]));
            helper.setText(data);
            helper.setReplyTo("no-reply@um.edu.ar");
            helper.setSubject("Notificación por Deuda");
        } catch (MessagingException e) {
            e.printStackTrace();
            return "ERROR: No pudo ENVIARSE";
        }
        sender.send(message);

        return "Envío de Correo Ok!!";
    }

    public String notificaDeudorChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId)
            throws MessagingException {
        String data = "";

        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        Domicilio domicilio = null;
        try {
            domicilio = domicilioService.findByUnique(serie.getPersonaId(), serie.getDocumentoId());
        } catch (DomicilioException e) {
            return "ERROR: Sin correos para ENVIAR";
        }

        data = notaDeudorChequera(facultadId, tipoChequeraId, chequeraSerieId);

        // Envia correo
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        List<String> addresses = new ArrayList<String>();

        if (!domicilio.getEmailPersonal().equals(""))
            addresses.add(domicilio.getEmailPersonal());
        if (!domicilio.getEmailInstitucional().equals(""))
            addresses.add(domicilio.getEmailInstitucional());

//		addresses.add("daniel.quinterospinto@gmail.com");
        try {
            helper.setTo(addresses.toArray(new String[addresses.size()]));
            helper.setText(data);
            helper.setReplyTo("no-reply@um.edu.ar");
            helper.setSubject("Notificación por Deuda");
        } catch (MessagingException e) {
            e.printStackTrace();
            return "ERROR: No pudo ENVIARSE";
        }
        sender.send(message);

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
                    .format(cuota.getVencimiento1().withOffsetSameInstant(ZoneOffset.UTC))
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

}

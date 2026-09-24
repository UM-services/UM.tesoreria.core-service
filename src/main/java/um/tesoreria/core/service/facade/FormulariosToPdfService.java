package um.tesoreria.core.service.facade;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;
import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.Image;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.BarcodeInter25;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;
import um.tesoreria.core.hexagonal.personas.legajo.infrastructure.persistence.entity.LegajoEntity;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.model.TipoImpresion;
import um.tesoreria.core.model.dto.ChequeraCuotaPagosDto;
import um.tesoreria.core.model.dto.ChequeraPagoDto;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.exception.ArancelTipoException;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.service.ArancelTipoService;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.service.CarreraService;
import um.tesoreria.core.service.ChequeraAlternativaService;
import um.tesoreria.core.service.DebitoService;
import um.tesoreria.core.service.TipoImpresionService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.service.LectivoAlternativaService;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.personas.legajo.application.service.LegajoService;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import lombok.extern.slf4j.Slf4j;
import um.tesoreria.core.exception.CarreraException;
import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.hexagonal.lectivo.application.exception.LectivoException;
import um.tesoreria.core.hexagonal.personas.legajo.application.exception.LegajoException;
import um.tesoreria.core.hexagonal.personas.persona.application.exception.PersonaException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
@Slf4j
public class FormulariosToPdfService {

    private final Environment environment;
    private final ChequeraSerieService chequeraSerieService;
    private final FacultadService facultadService;
    private final TipoChequeraService tipoChequeraService;
    private final PersonaService personaService;
    private final LectivoService lectivoService;
    private final LegajoService legajoService;
    private final CarreraService carreraService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final LectivoAlternativaService lectivoAlternativaService;
    private final SincronizeService sincronizeService;
    private final WebClient.Builder webClientBuilder;
    private final ChequeraService chequeraService;
    private final DebitoService debitoService;
    private final ChequeraTotalService chequeraTotalService;
    private final ChequeraAlternativaService chequeraAlternativaService;
    private final ArancelTipoService arancelTipoService;
    private final TipoImpresionService tipoImpresionService;

    public FormulariosToPdfService(Environment environment, ChequeraSerieService chequeraSerieService, FacultadService facultadService, TipoChequeraService tipoChequeraService,
                                   PersonaService personaService, LectivoService lectivoService, LegajoService legajoService, CarreraService carreraService,
                                   ChequeraCuotaService chequeraCuotaService, LectivoAlternativaService lectivoAlternativaService, SincronizeService sincronizeService,
                                   WebClient.Builder webClientBuilder, ChequeraService chequeraService, DebitoService debitoService,
                                   ChequeraTotalService chequeraTotalService, ChequeraAlternativaService chequeraAlternativaService,
                                   ArancelTipoService arancelTipoService, TipoImpresionService tipoImpresionService) {
        this.environment = environment;
        this.chequeraSerieService = chequeraSerieService;
        this.facultadService = facultadService;
        this.tipoChequeraService = tipoChequeraService;
        this.personaService = personaService;
        this.lectivoService = lectivoService;
        this.legajoService = legajoService;
        this.carreraService = carreraService;
        this.chequeraCuotaService = chequeraCuotaService;
        this.lectivoAlternativaService = lectivoAlternativaService;
        this.sincronizeService = sincronizeService;
        this.webClientBuilder = webClientBuilder;
        this.chequeraService = chequeraService;
        this.debitoService = debitoService;
        this.chequeraTotalService = chequeraTotalService;
        this.chequeraAlternativaService = chequeraAlternativaService;
        this.arancelTipoService = arancelTipoService;
        this.tipoImpresionService = tipoImpresionService;
    }

    public String generateChequeraPdf(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                      Integer alternativaId) {
        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        List<ChequeraCuota> cuotas = chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(serie.getFacultadId(),
                        serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId());
        boolean hayAlgoParaImprimir = false;
        for (ChequeraCuota cuota : cuotas) {
            if (cuota.getPagado() == 0 && cuota.getBaja() == 0 && cuota.getImporte1().compareTo(BigDecimal.ZERO) != 0) {
                hayAlgoParaImprimir = true;
            }
        }

        if (!hayAlgoParaImprimir) {
            return "";
        }

        Facultad facultad = facultadService.findByFacultadId(serie.getFacultadId());
        TipoChequera tipoChequera = tipoChequeraService.findByTipoChequeraId(serie.getTipoChequeraId());
        Persona persona;
        try {
            persona = personaService.findByUnique(serie.getPersonaId(), serie.getDocumentoId());
        } catch (PersonaException e) {
            persona = new Persona();
        }
        Lectivo lectivo;
        try {
            lectivo = lectivoService.findByLectivoId(serie.getLectivoId());
        } catch (LectivoException e) {
            lectivo = new Lectivo();
        }
        // Sincroniza carrera
        try {
            sincronizeService.sincronizeCarreraAlumno(facultadId, persona.getPersonaId(), persona.getDocumentoId());
        } catch (Exception e) {
            log.debug("Sin sincronizar");
        }
        log.debug("Antes");

        Legajo legajo = null;
        try {
            legajo = legajoService.findByFacultadIdAndPersonaIdAndDocumentoId(serie.getFacultadId(),
                    serie.getPersonaId(), serie.getDocumentoId());
        } catch (LegajoException e) {
            legajo = new Legajo();
        }
        Carrera carrera = null;
        try {
            carrera = carreraService.findByFacultadIdAndPlanIdAndCarreraId(legajo.getFacultadId(), legajo.getPlanId(),
                    legajo.getCarreraId());
        } catch (CarreraException e) {
            carrera = new Carrera();
        }

        String path = environment.getProperty("path.files");

        String filename = path + "chequera-" + serie.getPersonaId() + "-" + serie.getFacultadId() + "-"
                + serie.getTipoChequeraId() + "-" + serie.getChequeraSerieId() + ".pdf";

        try {
            Document document = new Document(new Rectangle(PageSize.A4));
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.setMargins(40, 25, 40, 30);
            document.open();

            float[] columnHeader = {1, 1};
            PdfPTable table = new PdfPTable(columnHeader);
            table.setWidthPercentage(100);

            Image image = null;
            if (facultadId == 15)
                image = Image.getInstance("marca_etec.png");
            else
                image = Image.getInstance("marca_um.png");
            image.scalePercent(80);
            PdfPCell cell = new PdfPCell(image);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            Paragraph paragraph = new Paragraph("UNIVERSIDAD DE MENDOZA", new Font(Font.HELVETICA, 16, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.addElement(paragraph);
            paragraph = new Paragraph(facultad.getNombre(), new Font(Font.HELVETICA, 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.addElement(paragraph);
            table.addCell(cell);
            document.add(table);

            paragraph = new Paragraph(tipoChequera.getNombre(), new Font(Font.HELVETICA, 16, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph(lectivo.getNombre(), new Font(Font.HELVETICA, 12));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph("RapiPago", new Font(Font.HELVETICA, 12, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(paragraph);
            paragraph = new Paragraph(
                    new Phrase("         Alumno: (" + persona.getPersonaId() + ") ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(persona.getApellido() + ", " + persona.getNombre(),
                    new Font(Font.HELVETICA, 11, Font.BOLD)));
            if (facultadId != 6)
                paragraph.add(new Phrase(" - (" + carrera.getNombre() + ")", new Font(Font.HELVETICA, 11)));
            document.add(paragraph);
            paragraph = new Paragraph(new Phrase("Chequera: ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(serie.getChequeraSerieId().toString(),
                    new Font(Font.HELVETICA, 11, Font.BOLD)));
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(paragraph);
            paragraph = new Paragraph(new Phrase("Código de Pago Electrónico: ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(
                    String.format("%02d", serie.getFacultadId()) + String.format("%03d", serie.getTipoChequeraId())
                            + String.format("%05d", serie.getChequeraSerieId()),
                    new Font(Font.HELVETICA, 11, Font.BOLD)));
            document.add(paragraph);
            paragraph = new Paragraph("Alternativa " + alternativaId,
                    new Font(Font.HELVETICA, 12, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add(new Paragraph(" ", new Font(Font.HELVETICA, 8)));

            for (ChequeraCuota cuota : chequeraCuotaService
                    .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(serie.getFacultadId(),
                            serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId())) {
                if (cuota.getPagado() == 0 && cuota.getBaja() == 0
                        && cuota.getImporte1().compareTo(BigDecimal.ZERO) != 0) {
                    LectivoAlternativa lectivoAlternativa = lectivoAlternativaService
                            .findByFacultadIdAndLectivoIdAndTipochequeraIdAndProductoIdAndAlternativaId(
                                    serie.getFacultadId(), serie.getLectivoId(), serie.getTipoChequeraId(),
                                    cuota.getProductoId(), serie.getAlternativaId());

                    float[] columnCuota = {1, 1, 1, 1};
                    table = new PdfPTable(columnCuota);
                    table.setWidthPercentage(100);
                    paragraph = new Paragraph(
                            new Phrase(lectivoAlternativa.getTitulo() + ": " + cuota.getCuotaId()
                                    + " de " + lectivoAlternativa.getCuotas(), new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell = new PdfPCell();
                    cell.addElement(paragraph);
                    cell.setBorder(Rectangle.TOP);
                    table.addCell(cell);
                    paragraph = new Paragraph(new Phrase("Período: ", new Font(Font.HELVETICA, 8)));
                    paragraph.add(
                            new Phrase(cuota.getMes() + "/" + cuota.getAnho(), new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell = new PdfPCell();
                    cell.addElement(paragraph);
                    cell.setBorder(Rectangle.TOP);
                    table.addCell(cell);
                    cell = new PdfPCell();
                    paragraph = new Paragraph(new Phrase("Primer Vencimiento: ", new Font(Font.HELVETICA, 8)));
                    paragraph.add(new Phrase(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                    .format(cuota.getVencimiento1().withOffsetSameInstant(ZoneOffset.UTC)),
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    cell.addElement(paragraph);
                    paragraph = new Paragraph(new Phrase("Segundo Vencimiento: ", new Font(Font.HELVETICA, 8)));
                    paragraph.add(new Phrase(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                    .format(cuota.getVencimiento2().withOffsetSameInstant(ZoneOffset.UTC)),
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    cell.addElement(paragraph);
                    paragraph = new Paragraph(new Phrase("Tercer Vencimiento: ", new Font(Font.HELVETICA, 8)));
                    paragraph.add(new Phrase(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                    .format(cuota.getVencimiento3().withOffsetSameInstant(ZoneOffset.UTC)),
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    cell.addElement(paragraph);
                    cell.setBorder(Rectangle.TOP);
                    table.addCell(cell);

                    cell = new PdfPCell();
                    paragraph = new Paragraph(new Phrase("Importe: ", new Font(Font.HELVETICA, 8)));
                    paragraph.add(new Phrase(new DecimalFormat("#.00").format(cuota.getImporte1()),
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    cell.addElement(paragraph);
                    paragraph = new Paragraph(new Phrase("Importe: ", new Font(Font.HELVETICA, 8)));
                    paragraph.add(new Phrase(new DecimalFormat("#.00").format(cuota.getImporte2()),
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    cell.addElement(paragraph);
                    paragraph = new Paragraph(new Phrase("Importe: ", new Font(Font.HELVETICA, 8)));
                    paragraph.add(new Phrase(new DecimalFormat("#.00").format(cuota.getImporte3()),
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    cell.addElement(paragraph);
                    cell.setBorder(Rectangle.TOP);
                    table.addCell(cell);

                    BarcodeInter25 code25 = new BarcodeInter25();
                    code25.setGenerateChecksum(false);
                    code25.setCode(cuota.getCodigoBarras());
                    code25.setX(1.3f);

                    image = code25.createImageWithBarcode(writer.getDirectContent(), null, null);
                    cell = new PdfPCell(image);
                    cell.setColspan(4);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.BOTTOM);
                    table.addCell(cell);

                    document.add(table);
                }
            }

            document.add(new Paragraph(" "));

            // Finalizamos el documento
            document.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return filename;
    }

    public String generateCuotaPdf(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Integer productoId, Integer cuotaId) {
        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        ChequeraCuota cuota = chequeraCuotaService
                .findByUnique(serie.getFacultadId(),
                        serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId(), productoId, cuotaId);
        boolean hayAlgoParaImprimir = cuota.getPagado() == 0 && cuota.getBaja() == 0 && cuota.getImporte1().compareTo(BigDecimal.ZERO) != 0;

        if (!hayAlgoParaImprimir) {
            return "";
        }

        Facultad facultad = facultadService.findByFacultadId(serie.getFacultadId());
        TipoChequera tipoChequera = tipoChequeraService.findByTipoChequeraId(serie.getTipoChequeraId());
        Persona persona;
        try {
            persona = personaService.findByUnique(serie.getPersonaId(), serie.getDocumentoId());
        } catch (PersonaException e) {
            persona = new Persona();
        }
        Lectivo lectivo;
        try {
            lectivo = lectivoService.findByLectivoId(serie.getLectivoId());
        } catch (LectivoException e) {
            lectivo = new Lectivo();
        }
        // Sincroniza carrera
        try {
            sincronizeService.sincronizeCarreraAlumno(facultadId, persona.getPersonaId(), persona.getDocumentoId());
        } catch (Exception e) {
            log.debug("Sin sincronizar");
        }
        log.debug("Antes");

        Legajo legajo = null;
        try {
            legajo = legajoService.findByFacultadIdAndPersonaIdAndDocumentoId(serie.getFacultadId(),
                    serie.getPersonaId(), serie.getDocumentoId());
        } catch (LegajoException e) {
            legajo = new Legajo();
        }
        Carrera carrera = null;
        try {
            carrera = carreraService.findByFacultadIdAndPlanIdAndCarreraId(legajo.getFacultadId(), legajo.getPlanId(),
                    legajo.getCarreraId());
        } catch (CarreraException e) {
            carrera = new Carrera();
        }

        String path = environment.getProperty("path.files");

        String filename = path + "cuota-" + serie.getPersonaId() + "-" + serie.getFacultadId() + "-"
                + serie.getTipoChequeraId() + "-" + serie.getChequeraSerieId() + "-" + cuota.getProductoId() + "-" + cuota.getCuotaId() + ".pdf";

        try {
            Document document = new Document(new Rectangle(PageSize.A4));
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.setMargins(40, 25, 40, 30);
            document.open();

            float[] columnHeader = {1, 1};
            PdfPTable table = new PdfPTable(columnHeader);
            table.setWidthPercentage(100);

            Image image = null;
            if (facultadId == 15)
                image = Image.getInstance("marca_etec.png");
            else
                image = Image.getInstance("marca_um.png");
            image.scalePercent(80);
            PdfPCell cell = new PdfPCell(image);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            Paragraph paragraph = new Paragraph("UNIVERSIDAD DE MENDOZA", new Font(Font.HELVETICA, 16, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.addElement(paragraph);
            paragraph = new Paragraph(facultad.getNombre(), new Font(Font.HELVETICA, 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.addElement(paragraph);
            table.addCell(cell);
            document.add(table);

            paragraph = new Paragraph(tipoChequera.getNombre(), new Font(Font.HELVETICA, 16, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph(lectivo.getNombre(), new Font(Font.HELVETICA, 12));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph("RapiPago", new Font(Font.HELVETICA, 12, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(paragraph);
            paragraph = new Paragraph(
                    new Phrase("         Alumno: (" + persona.getPersonaId() + ") ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(persona.getApellido() + ", " + persona.getNombre(),
                    new Font(Font.HELVETICA, 11, Font.BOLD)));
            if (facultadId != 6)
                paragraph.add(new Phrase(" - (" + carrera.getNombre() + ")", new Font(Font.HELVETICA, 11)));
            document.add(paragraph);
            paragraph = new Paragraph(new Phrase("Chequera: ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(serie.getChequeraSerieId().toString(),
                    new Font(Font.HELVETICA, 11, Font.BOLD)));
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(paragraph);
            paragraph = new Paragraph(new Phrase("Código de Pago Electrónico: ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(
                    String.format("%02d", serie.getFacultadId()) + String.format("%03d", serie.getTipoChequeraId())
                            + String.format("%05d", serie.getChequeraSerieId()),
                    new Font(Font.HELVETICA, 11, Font.BOLD)));
            document.add(paragraph);
            paragraph = new Paragraph("Alternativa " + alternativaId,
                    new Font(Font.HELVETICA, 12, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add(new Paragraph(" ", new Font(Font.HELVETICA, 8)));

            if (cuota.getPagado() == 0 && cuota.getBaja() == 0
                    && cuota.getImporte1().compareTo(BigDecimal.ZERO) != 0) {
                LectivoAlternativa lectivoAlternativa = lectivoAlternativaService
                        .findByFacultadIdAndLectivoIdAndTipochequeraIdAndProductoIdAndAlternativaId(
                                serie.getFacultadId(), serie.getLectivoId(), serie.getTipoChequeraId(),
                                cuota.getProductoId(), serie.getAlternativaId());

                float[] columnCuota = {1, 1, 1, 1};
                table = new PdfPTable(columnCuota);
                table.setWidthPercentage(100);
                paragraph = new Paragraph(
                        new Phrase(lectivoAlternativa.getTitulo() + ": " + cuota.getCuotaId()
                                + " de " + lectivoAlternativa.getCuotas(), new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell = new PdfPCell();
                cell.addElement(paragraph);
                cell.setBorder(Rectangle.TOP);
                table.addCell(cell);
                paragraph = new Paragraph(new Phrase("Período: ", new Font(Font.HELVETICA, 8)));
                paragraph.add(
                        new Phrase(cuota.getMes() + "/" + cuota.getAnho(), new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell = new PdfPCell();
                cell.addElement(paragraph);
                cell.setBorder(Rectangle.TOP);
                table.addCell(cell);
                cell = new PdfPCell();
                paragraph = new Paragraph(new Phrase("Primer Vencimiento: ", new Font(Font.HELVETICA, 8)));
                paragraph.add(new Phrase(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                .format(cuota.getVencimiento1().withOffsetSameInstant(ZoneOffset.UTC)),
                        new Font(Font.HELVETICA, 8, Font.BOLD)));
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                paragraph = new Paragraph(new Phrase("Segundo Vencimiento: ", new Font(Font.HELVETICA, 8)));
                paragraph.add(new Phrase(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                .format(cuota.getVencimiento2().withOffsetSameInstant(ZoneOffset.UTC)),
                        new Font(Font.HELVETICA, 8, Font.BOLD)));
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                paragraph = new Paragraph(new Phrase("Tercer Vencimiento: ", new Font(Font.HELVETICA, 8)));
                paragraph.add(new Phrase(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                .format(cuota.getVencimiento3().withOffsetSameInstant(ZoneOffset.UTC)),
                        new Font(Font.HELVETICA, 8, Font.BOLD)));
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setBorder(Rectangle.TOP);
                table.addCell(cell);

                cell = new PdfPCell();
                paragraph = new Paragraph(new Phrase("Importe: ", new Font(Font.HELVETICA, 8)));
                paragraph.add(new Phrase(new DecimalFormat("#.00").format(cuota.getImporte1()),
                        new Font(Font.HELVETICA, 8, Font.BOLD)));
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                paragraph = new Paragraph(new Phrase("Importe: ", new Font(Font.HELVETICA, 8)));
                paragraph.add(new Phrase(new DecimalFormat("#.00").format(cuota.getImporte2()),
                        new Font(Font.HELVETICA, 8, Font.BOLD)));
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                paragraph = new Paragraph(new Phrase("Importe: ", new Font(Font.HELVETICA, 8)));
                paragraph.add(new Phrase(new DecimalFormat("#.00").format(cuota.getImporte3()),
                        new Font(Font.HELVETICA, 8, Font.BOLD)));
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell.addElement(paragraph);
                cell.setBorder(Rectangle.TOP);
                table.addCell(cell);

                BarcodeInter25 code25 = new BarcodeInter25();
                code25.setGenerateChecksum(false);
                code25.setCode(cuota.getCodigoBarras());
                code25.setX(1.3f);

                image = code25.createImageWithBarcode(writer.getDirectContent(), null, null);
                cell = new PdfPCell(image);
                cell.setColspan(4);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.BOTTOM);
                table.addCell(cell);

                document.add(table);
            }

            document.add(new Paragraph(" "));

            // Finalizamos el documento
            document.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return filename;
    }

    public String generateMatriculaPdf(BigDecimal personaId, Integer documentoId, Integer facultadId,
                                       Integer lectivoId) {
        Facultad facultad = null;
        try {
            facultad = facultadService.findByFacultadId(facultadId);
        } catch (FacultadException e) {
            facultad = new Facultad();
        }
        log.debug(facultad.toString());
        if (facultad.getApiserver().isEmpty())
            return "";
        String url = "http://" + facultad.getApiserver() + ":" + facultad.getApiport() + "/formularios/matricula/"
                + personaId + "/" + documentoId + "/" + facultadId + "/" + lectivoId;
        String path = environment.getProperty("path.files");

        String filename = path + "matricula-" + personaId + "-" + documentoId + "-" + facultadId + "-" + lectivoId
                + ".pdf";

        try {
            byte[] response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (response != null) {
                Files.write(response, new File(filename));
            }
        } catch (WebClientResponseException e) {
            log.debug("No se pudo generar {}", filename);
            filename = null;
        } catch (IOException e) {
            log.debug("No se pudo generar {}", filename);
            filename = null;
        }
        return filename;
    }

    /**
     * Genera el PDF "Estado de Chequera": detalle de todos los productos/cuotas de una chequera
     * (pagas y pendientes), agrupados por producto (Matrícula antes que Arancel, orden ascendente
     * por productoId — igual que el ORDER BY ChT_Pro_ID ASC de la consulta de referencia), más una
     * segunda hoja con la adhesión al débito automático. El encabezado (logo, Universidad de
     * Mendoza, facultad, número de hoja, título, datos del titular/chequera y la leyenda "NO
     * VALIDO COMO COMPROBANTE DE PAGO") se repite igual en ambas hojas — ver
     * {@link #writeEncabezadoEstadoChequera}.
     * <p>
     * Fuente de datos de la hoja 1, según la consulta SQL de referencia (chequera_serie +
     * tipo_chequera + lectivo + arancel_tipo + chequera_total + persona + facultad +
     * tipoimpresion + producto + chequera_alternativa + chequera_cuota + chequera_pago):
     * - El número entre paréntesis junto a "Periodo: mes/año (n)" es {@code chp_orden}
     *   (ChequeraPagoDto.orden) del pago asociado a esa cuota — vacío si todavía no hay pago
     *   (el join con chequera_pago es LEFT OUTER).
     * - "Subtotal Pagado" sale de chequera_total (ChT_Pagado), no se recalcula sumando cuotas.
     *   "Subtotal Producto" (ChT_Total) va en la misma fila que el encabezado "Producto: X"
     *   (a la derecha), igual que en el PDF de referencia, para no gastar una línea aparte.
     * - El prefijo de cada fila de cuota (ej. "Arancel Mensual: 1/12") y el total de cuotas
     *   salen de chequera_alternativa (ChA_Titulo / ChA_Cuotas), no del nombre del producto.
     * - "Primer vencimiento" sale de ChequeraCuotaPagosDto.vencimiento1 y conserva su día
     *   calendario, sin convertir el huso horario.
     * - "Tipo Impresion" (ej. "Rapipago") sale de la nueva entidad {@link TipoImpresion}, agregada
     *   junto con este método porque no existía antes en el proyecto.
     * <p>
     * La hoja 2 (adhesión a débito automático) no está cubierta por la consulta de referencia;
     * sigue usando DebitoService tal como se armó originalmente.
     * PENDIENTE: la columna "Recibido" de esa tabla no tiene un campo de origen confirmado en
     * la entidad Debito — se deja vacía.
     */
    public String generateEstadoChequeraPdf(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                            Integer alternativaId, Integer debitoTipoId) {
        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        Facultad facultad;
        try {
            facultad = facultadService.findByFacultadId(serie.getFacultadId());
        } catch (FacultadException e) {
            facultad = new Facultad();
        }
        TipoChequera tipoChequera = tipoChequeraService.findByTipoChequeraId(serie.getTipoChequeraId());
        Persona persona;
        try {
            persona = personaService.findByUnique(serie.getPersonaId(), serie.getDocumentoId());
        } catch (PersonaException e) {
            persona = new Persona();
        }
        Lectivo lectivo;
        try {
            lectivo = lectivoService.findByLectivoId(serie.getLectivoId());
        } catch (LectivoException e) {
            lectivo = new Lectivo();
        }
        ArancelTipoEntity arancelTipo;
        try {
            arancelTipo = arancelTipoService.findByArancelTipoId(serie.getArancelTipoId());
        } catch (ArancelTipoException e) {
            arancelTipo = new ArancelTipoEntity();
        }
        TipoImpresion tipoImpresion;
        try {
            tipoImpresion = tipoImpresionService.findByTipoImpresionId(serie.getTipoImpresionId());
        } catch (Exception e) {
            tipoImpresion = new TipoImpresion();
            tipoImpresion.setNombre("");
        }

        List<ChequeraCuotaPagosDto> cuotaPagos = chequeraService.findAllCuotaPagosByChequera(facultadId,
                tipoChequeraId, chequeraSerieId, alternativaId);

        // Totales oficiales por producto (chequera_total: ChT_Total / ChT_Pagado) — no se recalculan
        Map<Integer, ChequeraTotal> totalesPorProducto = new LinkedHashMap<>();
        for (ChequeraTotal total : chequeraTotalService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId)) {
            totalesPorProducto.put(total.getProductoId(), total);
        }

        // TreeMap: orden ascendente de productoId, igual que el ORDER BY ChT_Pro_ID ASC de la
        // consulta de referencia — en la práctica deja Matrícula antes que Arancel.
        Map<Integer, List<ChequeraCuotaPagosDto>> porProducto = new TreeMap<>();
        for (ChequeraCuotaPagosDto cuota : cuotaPagos) {
            porProducto.computeIfAbsent(cuota.getProductoId(), k -> new ArrayList<>()).add(cuota);
        }

        String path = environment.getProperty("path.files");
        String filename = path + "estado-chequera-" + serie.getPersonaId() + "-" + serie.getFacultadId() + "-"
                + serie.getTipoChequeraId() + "-" + serie.getChequeraSerieId() + ".pdf";

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        // Paleta para un estilo más moderno: acento institucional, gris para etiquetas, gris claro
        // para líneas finas (en vez de negro) y un fondo muy suave para las filas alternadas.
        Color colorAcento = new Color(30, 58, 95);
        Color colorEtiqueta = new Color(110, 110, 110);
        Color colorLinea = new Color(210, 210, 215);
        Color colorFilaAlterna = new Color(247, 247, 250);

        try {
            Document document = new Document(new Rectangle(PageSize.A4));
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.setMargins(40, 25, 18, 14);
            document.open();

            // --- Hoja 1: encabezado + cuotas por producto ---
            writeEncabezadoEstadoChequera(document, facultad, facultadId, tipoChequera, arancelTipo, tipoImpresion,
                    lectivo, persona, serie, 1, colorAcento, colorEtiqueta);

            Paragraph paragraph = new Paragraph("Alternativa: " + alternativaId, new Font(Font.HELVETICA, 11, Font.BOLD, colorAcento));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add(new Paragraph(" ", new Font(Font.HELVETICA, 5)));

            PdfPCell cell;

            // --- Un bloque por producto (Matrícula primero, después Arancel) ---
            for (Map.Entry<Integer, List<ChequeraCuotaPagosDto>> entry : porProducto.entrySet()) {
                Integer productoId = entry.getKey();
                List<ChequeraCuotaPagosDto> cuotasProducto = entry.getValue();
                String nombreProducto = cuotasProducto.get(0).getProducto() != null
                        ? cuotasProducto.get(0).getProducto().getNombre()
                        : "Producto " + productoId;

                // chequera_alternativa: título de fila (ej. "Arancel Mensual") y cantidad total de cuotas
                ChequeraAlternativa chequeraAlternativa;
                try {
                    chequeraAlternativa = chequeraAlternativaService.findByUnique(facultadId, tipoChequeraId,
                            chequeraSerieId, productoId, alternativaId);
                } catch (Exception e) {
                    chequeraAlternativa = new ChequeraAlternativa();
                    chequeraAlternativa.setTitulo(nombreProducto);
                    chequeraAlternativa.setCuotas(cuotasProducto.size());
                }
                String tituloFila = chequeraAlternativa.getTitulo();
                Integer totalCuotas = chequeraAlternativa.getCuotas();

                // chequera_total: subtotales oficiales del producto (no se recalculan)
                ChequeraTotal chequeraTotal = totalesPorProducto.get(productoId);
                BigDecimal subtotalProducto = chequeraTotal != null && chequeraTotal.getTotal() != null
                        ? chequeraTotal.getTotal() : BigDecimal.ZERO;
                BigDecimal subtotalPagado = chequeraTotal != null && chequeraTotal.getPagado() != null
                        ? chequeraTotal.getPagado() : BigDecimal.ZERO;

                // "Producto: X" a la izquierda y "Subtotal Producto: $Y" a la derecha, en la misma
                // fila (como en el PDF de referencia) para no gastar una línea aparte.
                float[] columnProductoHeader = {1, 1};
                PdfPTable productoHeaderTable = new PdfPTable(columnProductoHeader);
                productoHeaderTable.setWidthPercentage(100);
                cell = new PdfPCell(new Phrase("Producto: " + nombreProducto, new Font(Font.HELVETICA, 11, Font.BOLD, colorAcento)));
                cell.setBorder(Rectangle.NO_BORDER);
                productoHeaderTable.addCell(cell);
                paragraph = new Paragraph(new Phrase("Subtotal Producto: ", new Font(Font.HELVETICA, 9, Font.NORMAL, colorEtiqueta)));
                paragraph.add(new Phrase(decimalFormat.format(subtotalProducto), new Font(Font.HELVETICA, 9, Font.BOLD)));
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.addElement(paragraph);
                productoHeaderTable.addCell(cell);
                document.add(productoHeaderTable);
                document.add(new Paragraph(" ", new Font(Font.HELVETICA, 3)));

                float[] columnCuota = {1.8f, 1.4f, 1.25f, 1.1f, 1.1f, 1.55f};
                PdfPTable table = new PdfPTable(columnCuota);
                table.setWidthPercentage(100);

                // Mantener el mismo orden de columnas que el reporte web.
                Font fontColHeader = new Font(Font.HELVETICA, 7, Font.BOLD, colorEtiqueta);
                String[] cuotaHeaders = {"Cuota", "Período", "Primer vencimiento", "A Pagar", "Fecha Pago", "Pagado"};
                int[] alineacionHeaders = {Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_LEFT,
                        Element.ALIGN_RIGHT, Element.ALIGN_CENTER, Element.ALIGN_RIGHT};
                for (int h = 0; h < cuotaHeaders.length; h++) {
                    cell = new PdfPCell(new Phrase(cuotaHeaders[h], fontColHeader));
                    cell.setHorizontalAlignment(alineacionHeaders[h]);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setBorder(Rectangle.BOTTOM);
                    cell.setBorderColor(colorLinea);
                    cell.setPaddingBottom(2f);
                    table.addCell(cell);
                }

                for (int i = 0; i < cuotasProducto.size(); i++) {
                    ChequeraCuotaPagosDto cuota = cuotasProducto.get(i);
                    // Última fila: además del borde superior, le agregamos borde inferior para
                    // cerrar visualmente el bloque del producto.
                    int bordeFila = (i == cuotasProducto.size() - 1)
                            ? (Rectangle.TOP | Rectangle.BOTTOM)
                            : Rectangle.TOP;
                    // Filas alternadas con un fondo muy suave, para un look más moderno/legible
                    Color fondoFila = (i % 2 == 0) ? colorFilaAlterna : Color.WHITE;

                    BigDecimal importe = cuota.getImporte1() != null ? cuota.getImporte1() : BigDecimal.ZERO;

                    // chequera_pago (LEFT JOIN): puede no existir todavía para esta cuota
                    ChequeraPagoDto pago = (cuota.getChequeraPagos() != null && !cuota.getChequeraPagos().isEmpty())
                            ? cuota.getChequeraPagos().get(0)
                            : null;

                    // Columna 1: "<ChA_Titulo>: <ChC_Cuo_ID>/<ChA_Cuotas>"
                    cell = new PdfPCell(new Phrase(
                            tituloFila + ": " + cuota.getCuotaId() + "/" + totalCuotas,
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setBorder(bordeFila);
                    cell.setBorderColor(colorLinea);
                    cell.setBackgroundColor(fondoFila);
                    cell.setPadding(3f);
                    table.addCell(cell);

                    // Columna 2: "Periodo: mes/año (chp_orden)"
                    String ordenTexto = pago != null && pago.getOrden() != null ? String.valueOf(pago.getOrden()) : "";
                    paragraph = new Paragraph(new Phrase("Periodo: ", new Font(Font.HELVETICA, 8, Font.NORMAL, colorEtiqueta)));
                    paragraph.add(new Phrase(cuota.getMes() + "/" + cuota.getAnho() + " (" + ordenTexto + ")",
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell = new PdfPCell();
                    cell.addElement(paragraph);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setBorder(bordeFila);
                    cell.setBorderColor(colorLinea);
                    cell.setBackgroundColor(fondoFila);
                    cell.setPadding(3f);
                    table.addCell(cell);

                    // Columna 3: fecha contractual del primer vencimiento (no es un instante de pago).
                    String primerVencimiento = cuota.getVencimiento1() != null
                            ? cuota.getVencimiento1().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "—";
                    cell = new PdfPCell(new Phrase(primerVencimiento, new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setBorder(bordeFila);
                    cell.setBorderColor(colorLinea);
                    cell.setBackgroundColor(fondoFila);
                    cell.setPadding(3f);
                    table.addCell(cell);

                    // Columna 4: A Pagar (importe de la cuota)
                    cell = new PdfPCell(new Phrase(decimalFormat.format(importe), new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setBorder(bordeFila);
                    cell.setBorderColor(colorLinea);
                    cell.setBackgroundColor(fondoFila);
                    cell.setPadding(3f);
                    table.addCell(cell);

                    // Columna 5: Fecha Pago — solo la fecha, centrada para alinear con su encabezado
                    // y con la misma altura que "A Pagar" (la referencia del pago ahora va bajo
                    // "Pagado", no acá, para no desalinear la fecha).
                    String fechaPago = "";
                    String referenciaPago = "";
                    if (pago != null) {
                        if (pago.getFecha() != null) {
                            fechaPago = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                    .format(pago.getFecha().withOffsetSameInstant(ZoneOffset.UTC));
                        }
                        // ChP_Archivo trae directamente el texto a mostrar (nombre de archivo del banco
                        // o el literal "MercadoPago", según cómo se haya registrado el pago)
                        referenciaPago = pago.getArchivo() != null ? pago.getArchivo() : "";
                    }
                    cell = new PdfPCell(new Phrase(fechaPago, new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setBorder(bordeFila);
                    cell.setBorderColor(colorLinea);
                    cell.setBackgroundColor(fondoFila);
                    cell.setPadding(3f);
                    table.addCell(cell);

                    // Columna 6: Pagado (importe realmente pagado — ChP_Importe; puede diferir del
                    // "A Pagar" si hubo un pago parcial), con la referencia del pago debajo
                    BigDecimal pagadoCuota = pago != null && pago.getImporte() != null ? pago.getImporte() : null;
                    paragraph = new Paragraph(new Phrase(pagadoCuota != null ? decimalFormat.format(pagadoCuota) : "—",
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    paragraph.setAlignment(Element.ALIGN_RIGHT);
                    if (!referenciaPago.isEmpty()) {
                        paragraph.add(Chunk.NEWLINE);
                        paragraph.add(new Phrase(referenciaPago, new Font(Font.HELVETICA, 6, Font.NORMAL, colorEtiqueta)));
                    }
                    cell = new PdfPCell();
                    cell.addElement(paragraph);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setBorder(bordeFila);
                    cell.setBorderColor(colorLinea);
                    cell.setBackgroundColor(fondoFila);
                    cell.setPadding(3f);
                    table.addCell(cell);
                }
                document.add(table);

                paragraph = new Paragraph(new Phrase("Subtotal Pagado: ", new Font(Font.HELVETICA, 9, Font.NORMAL, colorEtiqueta)));
                paragraph.add(new Phrase(decimalFormat.format(subtotalPagado), new Font(Font.HELVETICA, 9, Font.BOLD)));
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                document.add(paragraph);
                paragraph = new Paragraph(new Phrase("Subtotal Deuda: ", new Font(Font.HELVETICA, 9, Font.NORMAL, colorEtiqueta)));
                paragraph.add(new Phrase(decimalFormat.format(subtotalProducto.subtract(subtotalPagado)),
                        new Font(Font.HELVETICA, 9, Font.BOLD)));
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                document.add(paragraph);

                // Espacio entre el cierre de este producto y el siguiente (o la hoja 2)
                document.add(new Paragraph(" ", new Font(Font.HELVETICA, 6)));
            }

            // --- Hoja 2: mismo encabezado que la hoja 1 + adhesión al débito automático ---
            document.newPage();
            writeEncabezadoEstadoChequera(document, facultad, facultadId, tipoChequera, arancelTipo, tipoImpresion,
                    lectivo, persona, serie, 2, colorAcento, colorEtiqueta);

            List<Debito> debitos = debitoService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId,
                    debitoTipoId);

            paragraph = new Paragraph("Adhesión de chequera al Débito Automático",
                    new Font(Font.HELVETICA, 12, Font.BOLD, colorAcento));
            document.add(paragraph);
            document.add(new Paragraph(" "));

            float[] columnDebito = {0.6f, 1.2f, 1.3f, 2.2f, 1.3f, 0.7f, 1.7f};
            PdfPTable debitoTable = new PdfPTable(columnDebito);
            debitoTable.setWidthPercentage(100);
            String[] headers = {"Cuo", "Importe", "Fecha Vto", "CBU", "Envío al Banco", "Rech", "Motivo de Rechazo"};
            for (String h : headers) {
                cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 7, Font.BOLD, colorEtiqueta)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setBorderColor(colorLinea);
                cell.setPaddingBottom(3f);
                debitoTable.addCell(cell);
            }

            DateTimeFormatter fechaHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (int i = 0; i < debitos.size(); i++) {
                Debito debito = debitos.get(i);
                Color fondoFila = (i % 2 == 0) ? colorFilaAlterna : Color.WHITE;

                // Importe: Debito no tiene un campo propio, se busca en las cuotas ya cargadas
                BigDecimal importeDebito = cuotaPagos.stream()
                        .filter(c -> c.getProductoId().equals(debito.getProductoId())
                                && c.getAlternativaId().equals(debito.getAlternativaId())
                                && c.getCuotaId().equals(debito.getCuotaId()))
                        .map(c -> c.getImporte1() != null ? c.getImporte1() : BigDecimal.ZERO)
                        .findFirst()
                        .orElse(BigDecimal.ZERO);

                String[] valoresFila = {
                        String.valueOf(debito.getCuotaId()),
                        decimalFormat.format(importeDebito),
                        debito.getFechaVencimiento() != null
                                ? DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                .format(debito.getFechaVencimiento().withOffsetSameInstant(ZoneOffset.UTC))
                                : "",
                        debito.getCbu() != null ? debito.getCbu() : "",
                        debito.getFechaEnvio() != null
                                ? fechaHora.format(debito.getFechaEnvio().withOffsetSameInstant(ZoneOffset.UTC))
                                : "",
                        debito.getRechazado() != null && debito.getRechazado() != 0 ? "*" : "",
                        debito.getMotivoRechazo() != null ? debito.getMotivoRechazo() : ""
                };
                for (String valor : valoresFila) {
                    cell = new PdfPCell(new Phrase(valor, new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.TOP);
                    cell.setBorderColor(colorLinea);
                    cell.setBackgroundColor(fondoFila);
                    cell.setPadding(4f);
                    debitoTable.addCell(cell);
                }
            }
            document.add(debitoTable);

            document.close();
        } catch (Exception ex) {
            log.debug("No se pudo generar el estado de chequera: {}", ex.getMessage());
            filename = null;
        }

        return filename;
    }

    /**
     * Encabezado común a ambas hojas del "Estado de Chequera": logo, Universidad de Mendoza,
     * facultad, número de hoja, título, datos del titular/tipo de chequera/arancel/lectivo/
     * impresión, el código de chequera y la leyenda "NO VALIDO COMO COMPROBANTE DE PAGO".
     * Se llama una vez por hoja para que ambas queden idénticas. Estilo: nombre de la universidad
     * y línea de acento en {@code colorAcento}, etiquetas ("Titular:", "Tipo Chequera:", etc.) en
     * {@code colorEtiqueta} y valores en negro — misma tipografía base (Helvetica) que el resto
     * del proyecto, con la jerarquía dada por color/peso en vez de tipografías distintas.
     */
    private void writeEncabezadoEstadoChequera(Document document, Facultad facultad, Integer facultadId,
                                               TipoChequera tipoChequera, ArancelTipoEntity arancelTipo,
                                               TipoImpresion tipoImpresion, Lectivo lectivo, Persona persona,
                                               ChequeraSerie serie, int hoja, Color colorAcento, Color colorEtiqueta)
            throws Exception {
        float[] columnHeader = {1, 1};
        PdfPTable headerTable = new PdfPTable(columnHeader);
        headerTable.setWidthPercentage(100);

        Image image;
        if (facultadId == 15)
            image = Image.getInstance("marca_etec.png");
        else
            image = Image.getInstance("marca_um.png");
        image.scalePercent(80);
        PdfPCell cell = new PdfPCell(image);
        cell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(cell);

        Paragraph paragraph = new Paragraph("UNIVERSIDAD DE MENDOZA",
                new Font(Font.HELVETICA, 16, Font.BOLD, colorAcento));
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.addElement(paragraph);
        paragraph = new Paragraph(facultad.getNombre(), new Font(Font.HELVETICA, 14, Font.BOLD, colorAcento));
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        cell.addElement(paragraph);
        paragraph = new Paragraph("Hoja: " + hoja, new Font(Font.HELVETICA, 9, Font.NORMAL, colorEtiqueta));
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        cell.addElement(paragraph);
        headerTable.addCell(cell);
        document.add(headerTable);

        // Línea de acento fina, separando el logo/título de los datos del titular
        PdfPTable lineaAcento = new PdfPTable(1);
        lineaAcento.setWidthPercentage(100);
        cell = new PdfPCell();
        cell.setFixedHeight(2f);
        cell.setBackgroundColor(colorAcento);
        cell.setBorder(Rectangle.NO_BORDER);
        lineaAcento.addCell(cell);
        document.add(lineaAcento);
        document.add(new Paragraph(" ", new Font(Font.HELVETICA, 4)));

        paragraph = new Paragraph("Estado de Chequera", new Font(Font.HELVETICA, 16, Font.BOLD, colorAcento));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        document.add(new Paragraph(" ", new Font(Font.HELVETICA, 6)));

        paragraph = new Paragraph(
                new Phrase("Titular: (" + persona.getPersonaId() + ") ", new Font(Font.HELVETICA, 11, Font.NORMAL, colorEtiqueta)));
        paragraph.add(new Phrase(persona.getApellido() + ", " + persona.getNombre(), new Font(Font.HELVETICA, 11, Font.BOLD)));
        document.add(paragraph);

        paragraph = new Paragraph(new Phrase("Tipo Chequera: ", new Font(Font.HELVETICA, 11, Font.NORMAL, colorEtiqueta)));
        paragraph.add(new Phrase(tipoChequera.getNombre(), new Font(Font.HELVETICA, 11, Font.BOLD)));
        document.add(paragraph);

        paragraph = new Paragraph(new Phrase("Tipo Arancel: ", new Font(Font.HELVETICA, 11, Font.NORMAL, colorEtiqueta)));
        paragraph.add(new Phrase(arancelTipo.getDescripcion(), new Font(Font.HELVETICA, 11, Font.BOLD)));
        document.add(paragraph);

        paragraph = new Paragraph(new Phrase("Ciclo Lectivo: ", new Font(Font.HELVETICA, 11, Font.NORMAL, colorEtiqueta)));
        paragraph.add(new Phrase(lectivo.getNombre(), new Font(Font.HELVETICA, 11, Font.BOLD)));
        document.add(paragraph);

        paragraph = new Paragraph(new Phrase("Porcentaje de beca: ", new Font(Font.HELVETICA, 11, Font.NORMAL, colorEtiqueta)));
        paragraph.add(new Phrase((serie.getBecaPorcentaje() == null ? BigDecimal.ZERO : serie.getBecaPorcentaje())
                .movePointRight(2).stripTrailingZeros().toPlainString() + "%", new Font(Font.HELVETICA, 11, Font.BOLD)));
        document.add(paragraph);

        paragraph = new Paragraph(new Phrase("Tipo Impresion: ", new Font(Font.HELVETICA, 11, Font.NORMAL, colorEtiqueta)));
        paragraph.add(new Phrase(tipoImpresion.getNombre(), new Font(Font.HELVETICA, 11, Font.BOLD)));
        document.add(paragraph);

        paragraph = new Paragraph(new Phrase("Chequera: ", new Font(Font.HELVETICA, 11, Font.NORMAL, colorEtiqueta)));
        paragraph.add(new Phrase(serie.getFacultadId() + "/" + serie.getTipoChequeraId() + "/"
                + serie.getChequeraSerieId(), new Font(Font.HELVETICA, 11, Font.BOLD)));
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(paragraph);

        document.add(new Paragraph(" ", new Font(Font.HELVETICA, 5)));
        paragraph = new Paragraph("NO VALIDO COMO COMPROBANTE DE PAGO",
                new Font(Font.HELVETICA, 9, Font.BOLDITALIC, colorEtiqueta));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        document.add(new Paragraph(" ", new Font(Font.HELVETICA, 5)));
    }

}

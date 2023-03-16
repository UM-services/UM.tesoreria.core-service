package ar.edu.um.tesoreria.rest.service.facade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import com.google.common.io.Files;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BarcodeInter25;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import ar.edu.um.tesoreria.rest.model.Legajo;
import ar.edu.um.tesoreria.rest.model.kotlin.Persona;
import ar.edu.um.tesoreria.rest.model.TipoChequera;
import ar.edu.um.tesoreria.rest.service.CarreraService;
import ar.edu.um.tesoreria.rest.service.ChequeraCuotaService;
import ar.edu.um.tesoreria.rest.service.ChequeraSerieService;
import ar.edu.um.tesoreria.rest.service.FacultadService;
import ar.edu.um.tesoreria.rest.service.LectivoAlternativaService;
import ar.edu.um.tesoreria.rest.service.LectivoService;
import ar.edu.um.tesoreria.rest.service.LegajoService;
import ar.edu.um.tesoreria.rest.service.PersonaService;
import ar.edu.um.tesoreria.rest.service.TipoChequeraService;
import lombok.extern.slf4j.Slf4j;
import ar.edu.um.tesoreria.rest.exception.CarreraException;
import ar.edu.um.tesoreria.rest.exception.FacultadException;
import ar.edu.um.tesoreria.rest.exception.LectivoException;
import ar.edu.um.tesoreria.rest.exception.LegajoException;
import ar.edu.um.tesoreria.rest.exception.PersonaException;
import ar.edu.um.tesoreria.rest.model.Carrera;
import ar.edu.um.tesoreria.rest.model.ChequeraCuota;
import ar.edu.um.tesoreria.rest.model.ChequeraSerie;
import ar.edu.um.tesoreria.rest.model.Facultad;
import ar.edu.um.tesoreria.rest.model.Lectivo;
import ar.edu.um.tesoreria.rest.model.LectivoAlternativa;

@Service
@Slf4j
public class FormulariosToPdfService {

	@Autowired
	private Environment env;

	@Autowired
	private ChequeraSerieService chequeraSerieService;

	@Autowired
	private FacultadService facultadService;

	@Autowired
	private TipoChequeraService tipoChequeraService;

	@Autowired
	private PersonaService personaService;

	@Autowired
	private LectivoService lectivoService;

	@Autowired
	private LegajoService legajoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private ChequeraCuotaService chequeraCuotaService;

	@Autowired
	private LectivoAlternativaService lectivoAlternativaService;

	@Autowired
	private SincronizeService sincronizeService;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	public String generateChequeraPdf(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
			Integer alternativaId) {
		ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
		List<ChequeraCuota> cuotas = chequeraCuotaService
				.findAllByFacultadIdAndTipochequeraIdAndChequeraserieIdAndAlternativaId(serie.getFacultadId(),
						serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId());
		Boolean hayAlgoParaImprimir = false;
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
		Persona persona = null;
		try {
			persona = personaService.findByUnique(serie.getPersonaId(), serie.getDocumentoId());
		} catch (PersonaException e) {
			persona = new Persona();
		}
		Lectivo lectivo = null;
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

		String path = env.getProperty("path.files");

		String filename = path + "chequera-" + serie.getPersonaId() + "-" + serie.getFacultadId() + "-"
				+ serie.getTipoChequeraId() + "-" + serie.getChequeraSerieId() + ".pdf";

		try {
			Document document = new Document(new Rectangle(PageSize.A4));
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
			document.setMargins(40, 25, 40, 30);
			document.open();

			float[] columnHeader = { 1, 1 };
			PdfPTable table = new PdfPTable(columnHeader);
			table.setWidthPercentage(100);

			Image medios = Image.getInstance("medios.png");
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
			paragraph.add(new Phrase(Long.valueOf(serie.getChequeraSerieId()).toString(),
					new Font(Font.HELVETICA, 11, Font.BOLD)));
			paragraph.setAlignment(Element.ALIGN_RIGHT);
			document.add(paragraph);
			paragraph = new Paragraph(new Phrase("Código de Pago Electrónico: ", new Font(Font.HELVETICA, 11)));
			paragraph.add(new Phrase(
					String.format("%02d", serie.getFacultadId()) + String.format("%03d", serie.getTipoChequeraId())
							+ String.format("%05d", serie.getChequeraSerieId()),
					new Font(Font.HELVETICA, 11, Font.BOLD)));
			document.add(paragraph);
			paragraph = new Paragraph("Alternativa " + Integer.valueOf(alternativaId),
					new Font(Font.HELVETICA, 12, Font.BOLD));
			paragraph.setAlignment(Element.ALIGN_CENTER);
			document.add(paragraph);
			document.add(new Paragraph(" ", new Font(Font.HELVETICA, 8)));

			for (ChequeraCuota cuota : chequeraCuotaService
					.findAllByFacultadIdAndTipochequeraIdAndChequeraserieIdAndAlternativaId(serie.getFacultadId(),
							serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId())) {
				if (cuota.getPagado() == 0 && cuota.getBaja() == 0
						&& cuota.getImporte1().compareTo(BigDecimal.ZERO) != 0) {
					LectivoAlternativa lectivoAlternativa = lectivoAlternativaService
							.findByFacultadIdAndLectivoIdAndTipochequeraIdAndProductoIdAndAlternativaId(
									serie.getFacultadId(), serie.getLectivoId(), serie.getTipoChequeraId(),
									cuota.getProductoId(), serie.getAlternativaId());

					float[] columnCuota = { 1, 1, 1, 1 };
					table = new PdfPTable(columnCuota);
					table.setWidthPercentage(100);
					paragraph = new Paragraph(
							new Phrase(lectivoAlternativa.getTitulo() + ": " + Integer.valueOf(cuota.getCuotaId())
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
			document.add(medios);

			document.add(new Paragraph(" "));

			// Finalizamos el documento
			document.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage().toString());
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
		if (facultad.getApiserver().equals(""))
			return "";
		String url = "http://" + facultad.getApiserver() + ":" + facultad.getApiport() + "/formularios/matricula/"
				+ personaId + "/" + documentoId + "/" + facultadId + "/" + lectivoId;
		String path = env.getProperty("path.files");

		String filename = path + "matricula-" + personaId + "-" + documentoId + "-" + facultadId + "-" + lectivoId
				+ ".pdf";

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<byte[]> response = restTemplateBuilder.build().exchange(url, HttpMethod.GET, entity,
					byte[].class);
			Files.write(response.getBody(), new File(filename));
		} catch (HttpServerErrorException e) {
			log.debug("No se pudo generar {}", filename);
			filename = null;
		} catch (IOException e) {
			log.debug("No se pudo generar {}", filename);
			filename = null;
		}
		return filename;
	}
}

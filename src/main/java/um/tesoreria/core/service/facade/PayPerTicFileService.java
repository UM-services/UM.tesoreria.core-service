/**
 * 
 */
package um.tesoreria.core.service.facade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraAlternativaException;
import um.tesoreria.core.exception.ChequeraSerieException;
import um.tesoreria.core.exception.DomicilioException;
import um.tesoreria.core.exception.PayPerTicException;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.kotlin.model.ChequeraAlternativa;
import um.tesoreria.core.kotlin.model.ChequeraSerie;
import um.tesoreria.core.kotlin.model.Domicilio;
import um.tesoreria.core.model.PayPerTic;
import um.tesoreria.core.model.view.CuotaDeudaPayPerTic;
import um.tesoreria.core.service.ChequeraAlternativaService;
import um.tesoreria.core.service.ChequeraSerieService;
import um.tesoreria.core.service.DomicilioService;
import um.tesoreria.core.service.PayPerTicService;
import um.tesoreria.core.hexagonal.persona.application.service.PersonaService;
import um.tesoreria.core.service.view.CuotaDeudaPayPerTicService;
import um.tesoreria.core.util.Tool;
import um.tesoreria.core.util.transfer.FileInfo;
import lombok.extern.slf4j.Slf4j;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class PayPerTicFileService {

	@Autowired
	private CuotaDeudaPayPerTicService cuotaDeudaPayPerTicService;

	@Autowired
	private ChequeraSerieService chequeraSerieService;

	@Autowired
	private PersonaService personaService;

	@Autowired
	private ChequeraAlternativaService chequeraAlternativaService;

	@Autowired
	private DomicilioService domicilioService;

	@Autowired
	private PayPerTicService payPerTicService;

	@Autowired
	private Environment env;

	public String generate(OffsetDateTime desde, OffsetDateTime hasta) throws IOException {
		String path = env.getProperty("path.files");

		String filename = path + "pay_per_tic.xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle style_normal = book.createCellStyle();
		Font font_normal = book.createFont();
		font_normal.setBold(false);
		style_normal.setFont(font_normal);

		Sheet sheet = book.createSheet("Lista");
		Row row = null;
		Integer fila = 0;
		row = sheet.createRow(fila);
		this.setCellString(row, 0, "external_transaction_id", style_normal);
		this.setCellString(row, 1, "external_reference", style_normal);
		this.setCellString(row, 2, "concept_id", style_normal);
		this.setCellString(row, 3, "concept_description", style_normal);
		this.setCellString(row, 4, "currency_id", style_normal);
		this.setCellString(row, 5, "amount", style_normal);
		this.setCellString(row, 6, "due_date", style_normal);
		this.setCellString(row, 7, "last_due_date", style_normal);
		this.setCellString(row, 8, "return_url", style_normal);
		this.setCellString(row, 9, "back_url", style_normal);
		this.setCellString(row, 10, "notification_url", style_normal);
		this.setCellString(row, 11, "rate", style_normal);
		this.setCellString(row, 12, "charge_delay", style_normal);
		this.setCellString(row, 13, "payment_number", style_normal);
		this.setCellString(row, 14, "promotion_code", style_normal);
		this.setCellString(row, 15, "meta_data", style_normal);
		this.setCellString(row, 16, "payer_reference", style_normal);
		this.setCellString(row, 17, "payer_name", style_normal);
		this.setCellString(row, 18, "payer_email", style_normal);
		this.setCellString(row, 19, "payer_phone", style_normal);
		this.setCellString(row, 20, "payer_id_country", style_normal);
		this.setCellString(row, 21, "payer_id_type", style_normal);
		this.setCellString(row, 22, "payer_id_number", style_normal);

		for (CuotaDeudaPayPerTic cuota : cuotaDeudaPayPerTicService.findAllByVencimiento1Between(desde, hasta)) {
			ChequeraSerie chequeraSerie = null;
			try {
				chequeraSerie = chequeraSerieService.findByUnique(cuota.getFacultadId(), cuota.getTipoChequeraId(),
						cuota.getChequeraSerieId());
			} catch (ChequeraSerieException e) {
				chequeraSerie = new ChequeraSerie();
			}

			PersonaEntity personaEntity = null;
			try {
				personaEntity = personaService.findByUnique(chequeraSerie.getPersonaId(), chequeraSerie.getDocumentoId());
			} catch (PersonaException e) {
				personaEntity = new PersonaEntity();
			}

			if (personaEntity.getUniqueId() != null) {
				row = sheet.createRow(++fila);
				this.setCellString(row, 0, cuota.getKey(), style_normal); // external_transaction_id
				this.setCellString(row, 1, cuota.getKey(), style_normal); // external_reference
				String concept_id = cuota.getProductoId() + "/" + cuota.getCuotaId() + "/" + cuota.getMes() + "/"
						+ cuota.getAnho();
				this.setCellString(row, 2, concept_id, style_normal);

				ChequeraAlternativa chequeraAlternativa = null;
				try {
					chequeraAlternativa = chequeraAlternativaService.findByUnique(cuota.getFacultadId(),
							cuota.getTipoChequeraId(), cuota.getChequeraSerieId(), cuota.getProductoId(),
							cuota.getAlternativaId());
				} catch (ChequeraAlternativaException e) {
					chequeraAlternativa = new ChequeraAlternativa();
				}
				String concept_description = chequeraAlternativa.getTitulo() + ": " + cuota.getCuotaId() + " Periodo: "
						+ cuota.getMes() + "/" + cuota.getAnho();
				this.setCellString(row, 3, concept_description, style_normal);
				String currency_id = "ARS";
				this.setCellString(row, 4, currency_id, style_normal);
				Double amount = cuota.getImporte1().doubleValue();
				this.setCellDouble(row, 5, amount, style_normal);
				OffsetDateTime due_date = Tool.lastTime(cuota.getVencimiento1());
				this.setCellString(row, 6, due_date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
						style_normal);
				OffsetDateTime last_due_date = cuota.getVencimiento2();
				last_due_date = Tool.lastTime(last_due_date.plusDays(10));
				this.setCellString(row, 7, last_due_date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
						style_normal);

				BigDecimal payer_reference = chequeraSerie.getPersonaId();
				this.setCellBigDecimal(row, 16, payer_reference, style_normal);

				String payer_name = personaEntity.getApellido() + ", " + personaEntity.getNombre();
				this.setCellString(row, 17, payer_name, style_normal);

				Domicilio domicilio = null;
				try {
					domicilio = domicilioService.findByUnique(personaEntity.getPersonaId(), personaEntity.getDocumentoId());
				} catch (DomicilioException e) {
					domicilio = new Domicilio();
				}
				String payer_email = domicilio.getEmailPersonal();
				this.setCellString(row, 18, payer_email, style_normal);
				String payer_id_country = "ARG";
				this.setCellString(row, 20, payer_id_country, style_normal);
				String payer_id_type = "DNI_ARG";
				this.setCellString(row, 21, payer_id_type, style_normal);
				String payer_id_number = payer_reference.toString();
				this.setCellString(row, 22, payer_id_number, style_normal);
			}
		}

		for (Integer column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		try {
			File file = new File(filename);
			FileOutputStream output = new FileOutputStream(file);
			book.write(output);
			output.flush();
			output.close();
			log.debug(file.getAbsolutePath());
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
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

	private void setCellDouble(Row row, int column, Double value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	public void upload(FileInfo fileinfo) {
		List<PayPerTic> pagos = new ArrayList<PayPerTic>();
		File file = Tool.writeFile(fileinfo);

		// Procesa Excel
		try {
			InputStream input = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(input);
			Integer sheet_count = workbook.getNumberOfSheets();
			log.debug("Sheets -> " + sheet_count.toString());
			for (Integer counter = 0; counter < sheet_count; counter++) {
				Sheet sheet = workbook.getSheetAt(counter);
				log.debug("Sheet -> " + sheet.getSheetName());
				Integer rows = sheet.getLastRowNum();
				for (Integer row_number = 1; row_number < rows; row_number++) {
					Row row = sheet.getRow(row_number);
					Integer column = 0;
					if (row.getCell(column) != null) {
						String payperticId = row.getCell(column).getStringCellValue();
						PayPerTic rendicion = null;
						try {
							rendicion = payPerTicService.findByPayperticId(payperticId);
						} catch (PayPerTicException e) {
							rendicion = new PayPerTic();
						}
						rendicion.setPayperticId(payperticId);
						if (row.getCell(++column) != null)
							rendicion.setExternaltransactionId(row.getCell(column).getStringCellValue());
						if (row.getCell(++column) != null)
							rendicion.setConceptId(row.getCell(column).getStringCellValue());
						if (row.getCell(++column) != null)
							rendicion.setConceptdescription(row.getCell(column).getStringCellValue());
						if (row.getCell(++column) != null)
							rendicion.setPayername(row.getCell(column).getStringCellValue());
						if (row.getCell(++column) != null)
							rendicion.setPayeremail(row.getCell(column).getStringCellValue());
						if (row.getCell(++column) != null)
							rendicion.setPayernumberId(row.getCell(column).getStringCellValue());
						if (row.getCell(++column) != null)
							rendicion.setUploaddate(OffsetDateTime.parse(row.getCell(column).getStringCellValue(),
									DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneOffset.UTC)));
						if (row.getCell(++column) != null)
							rendicion.setDuedate(OffsetDateTime.parse(row.getCell(column).getStringCellValue(),
									DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneOffset.UTC)));
						if (row.getCell(++column) != null)
							rendicion.setPaymentdate(OffsetDateTime.parse(row.getCell(column).getStringCellValue(),
									DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneOffset.UTC)));
						if (row.getCell(++column) != null)
							rendicion.setAccreditationdate(OffsetDateTime.parse(
									row.getCell(column).getStringCellValue(),
									DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneOffset.UTC)));
						if (row.getCell(++column) != null)
							rendicion.setAmount(new BigDecimal(row.getCell(column).getNumericCellValue()).setScale(2,
									RoundingMode.HALF_DOWN));
						if (row.getCell(++column) != null)
							rendicion.setArancelrecaudacion(new BigDecimal(row.getCell(column).getNumericCellValue())
									.setScale(2, RoundingMode.HALF_DOWN));
						if (row.getCell(++column) != null)
							rendicion.setIvaservicio(new BigDecimal(row.getCell(column).getNumericCellValue())
									.setScale(2, RoundingMode.HALF_DOWN));
						if (row.getCell(++column) != null)
							rendicion.setCostomediopago(new BigDecimal(row.getCell(column).getNumericCellValue())
									.setScale(2, RoundingMode.HALF_DOWN));
						if (row.getCell(++column) != null)
							rendicion.setCostofinanciacion(new BigDecimal(row.getCell(column).getNumericCellValue())
									.setScale(2, RoundingMode.HALF_DOWN));
						if (row.getCell(++column) != null)
							rendicion.setOtroscostos(new BigDecimal(row.getCell(column).getNumericCellValue())
									.setScale(2, RoundingMode.HALF_DOWN));
						if (row.getCell(++column) != null)
							rendicion
									.setImpuestosrefacturacion(new BigDecimal(row.getCell(column).getNumericCellValue())
											.setScale(2, RoundingMode.HALF_DOWN));
						if (row.getCell(++column) != null)
							rendicion.setTotalfactura(new BigDecimal(row.getCell(column).getNumericCellValue())
									.setScale(2, RoundingMode.HALF_DOWN));
						rendicion.setSheet(sheet.getSheetName());
						pagos.add(rendicion);
					}
				}
			}
			workbook.close();
			input.close();
		} catch (FileNotFoundException e) {
			log.debug(e.getMessage());
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
		payPerTicService.saveAll(pagos);
	}

}

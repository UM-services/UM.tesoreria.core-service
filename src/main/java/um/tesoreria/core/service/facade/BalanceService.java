/**
 * 
 */
package um.tesoreria.core.service.facade;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
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

import um.tesoreria.core.exception.ProveedorMovimientoException;
import um.tesoreria.core.exception.ProveedorValorException;
import um.tesoreria.core.exception.ValorMovimientoException;
import um.tesoreria.core.exception.ValorException;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.service.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class BalanceService {

	@Autowired
	private Environment env;

	@Autowired
	private CuentaService cuentaService;

	@Autowired
	private ContabilidadService contabilidadService;

	@Autowired
	private CuentaMovimientoService cuentaMovimientoService;

	@Autowired
	private ComprobanteService comprobanteService;

	@Autowired
	private ValorMovimientoService valorMovimientoService;

	@Autowired
	private ProveedorValorService proveedorValorService;

	@Autowired
	private ProveedorMovimientoService proveedorMovimientoService;

	@Autowired
	private ValorService valorService;
    @Autowired
    private ProveedorPagoService proveedorPagoService;

	public String makeSumasSaldos(OffsetDateTime desde, OffsetDateTime hasta) {
		Ejercicio ejercicio = contabilidadService.verifyEjercicio(desde, hasta);
		if (ejercicio == null)
			return "Error: SIN Ejercicio";

		contabilidadService.recalculateGrados();

		String path = env.getProperty("path.files");

		String filename = path + "sumas_y_saldos.xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle style_normal = book.createCellStyle();
		Font font_normal = book.createFont();
		font_normal.setBold(false);
		style_normal.setFont(font_normal);

		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		Sheet sheet = book.createSheet("Balance");
		Row row = null;
		Integer fila = -1;
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Desde " + format.format(desde), style_normal);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Hasta " + format.format(hasta), style_normal);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "#Cuenta", style_normal);
		this.setCellString(row, 1, "Cuenta", style_normal);
		this.setCellString(row, 2, "S.Inicial Débitos", style_normal);
		this.setCellString(row, 3, "S.Inicial Créditos", style_normal);
		this.setCellString(row, 4, "Débitos", style_normal);
		this.setCellString(row, 5, "Créditos", style_normal);
		this.setCellString(row, 6, "Saldo Deudor", style_normal);
		this.setCellString(row, 7, "Saldo Acreedor", style_normal);

		List<Cuenta> cuentas = cuentaService.findAllGrado5();
//		List<Cuenta> cuentas = cuentaService
//				.findAllByCuentaIn(List.of(new BigDecimal(10102030001.0), new BigDecimal(10102040001.0)));
		for (Cuenta cuenta : cuentas) {
			Boolean show = false;
			show = true;
			List<BigDecimal> iniciales = contabilidadService.saldoInicial(cuenta.getNumeroCuenta(), ejercicio, desde);
			if (iniciales.get(0).compareTo(BigDecimal.ZERO) != 0)
				show = true;
			if (iniciales.get(1).compareTo(BigDecimal.ZERO) != 0)
				show = true;
			List<BigDecimal> movimientos = contabilidadService.saldoPeriodo(cuenta.getNumeroCuenta(), desde, hasta,
					(byte) 0);
			if (movimientos.get(0).compareTo(BigDecimal.ZERO) != 0)
				show = true;
			if (movimientos.get(1).compareTo(BigDecimal.ZERO) != 0)
				show = true;
			BigDecimal saldoDeudor = BigDecimal.ZERO;
			BigDecimal saldoAcreedor = BigDecimal.ZERO;
			BigDecimal saldo = iniciales.get(0).subtract(iniciales.get(1)).setScale(2, RoundingMode.HALF_UP);
			saldo = saldo.add(movimientos.get(0).subtract(movimientos.get(1)).setScale(2, RoundingMode.HALF_UP));
			if (saldo.compareTo(BigDecimal.ZERO) != 0)
				show = true;
			if (saldo.compareTo(BigDecimal.ZERO) > 0) {
				saldoDeudor = saldo;
			} else {
				saldoAcreedor = saldo.multiply(new BigDecimal(-1)).setScale(2, RoundingMode.HALF_UP);
			}
			if (show) {
				row = sheet.createRow(++fila);
				this.setCellBigDecimal(row, 0, cuenta.getNumeroCuenta(), style_normal);
				this.setCellString(row, 1, cuenta.getNombre(), style_normal);
				// Calculo saldo inicial de apertura
				this.setCellBigDecimal(row, 2, iniciales.get(0), style_normal);
				this.setCellBigDecimal(row, 3, iniciales.get(1), style_normal);
				this.setCellBigDecimal(row, 4, movimientos.get(0), style_normal);
				this.setCellBigDecimal(row, 5, movimientos.get(1), style_normal);
				this.setCellBigDecimal(row, 6, saldoDeudor, style_normal);
				this.setCellBigDecimal(row, 7, saldoAcreedor, style_normal);
			}
		}

		for (Integer column = 0; column < sheet.getRow(2).getPhysicalNumberOfCells(); column++)
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

	public String makeMayor(BigDecimal numeroCuenta, OffsetDateTime desde, OffsetDateTime hasta) {
		Ejercicio ejercicio = contabilidadService.verifyEjercicio(desde, hasta);
		if (ejercicio == null)
			return "Error: SIN Ejercicio";

		Cuenta cuenta = cuentaService.findByNumeroCuenta(numeroCuenta);
		Map<Integer, Comprobante> comprobantes = comprobanteService.findAll().stream()
				.collect(Collectors.toMap(Comprobante::getComprobanteId, Function.identity(), (comprobante, replacement) -> comprobante));

		String path = env.getProperty("path.files");

		String filename = path + "mayor.xlsx";

		Workbook book = new XSSFWorkbook();
		CellStyle style_normal = book.createCellStyle();
		Font font_normal = book.createFont();
		font_normal.setBold(false);
		style_normal.setFont(font_normal);
		CellStyle style_bold = book.createCellStyle();
		Font font_bold = book.createFont();
		font_bold.setBold(true);
		style_bold.setFont(font_bold);

		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		Sheet sheet = book.createSheet("Mayor");
		Row row = null;
		Integer fila = -1;
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Cuenta", style_bold);
		this.setCellBigDecimal(row, 1, numeroCuenta, style_normal);
		this.setCellString(row, 2, cuenta.getNombre(), style_normal);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Desde " + format.format(desde), style_normal);
		row = sheet.createRow(++fila);
		this.setCellString(row, 0, "Hasta " + format.format(hasta), style_normal);

		BigDecimal saldo = BigDecimal.ZERO;

		int columnaFecha = 0;
		int columnaAsiento = 1;
		int columnaComprobante = 2;
		int columnaProveedor = 3;
		int columnaConcepto = 4;
		int columnaOrdenPago = 5;
		int columnaBeneficiario = 6;
		int columnaDebe = 7;
		int columnaHaber = 8;
		int columnaSaldo = 9;
		int columnaOrdenPagoValor = 10;
		int columnaValor = 11;
		int columnaNumero = 12;
		int columnaImporte = 13;

		row = sheet.createRow(++fila);
		this.setCellString(row, columnaOrdenPago, "Inicial", style_bold);
		List<BigDecimal> iniciales = contabilidadService.saldoInicial(numeroCuenta, ejercicio, desde);
		this.setCellBigDecimal(row, columnaDebe, iniciales.get(0), style_normal);
		this.setCellBigDecimal(row, columnaHaber, iniciales.get(1), style_normal);
		saldo = saldo.add(iniciales.get(0)).setScale(2, RoundingMode.HALF_UP);
		saldo = saldo.subtract(iniciales.get(1)).setScale(2, RoundingMode.HALF_UP);
		this.setCellBigDecimal(row, columnaSaldo, saldo, style_normal);

		row = sheet.createRow(++fila);
		this.setCellString(row, columnaFecha, "Fecha", style_bold);
		this.setCellString(row, columnaAsiento, "Asiento", style_bold);
		this.setCellString(row, columnaComprobante, "Comprobante", style_bold);
		this.setCellString(row, columnaProveedor, "Proveedor", style_bold);
		this.setCellString(row, columnaConcepto, "Concepto", style_bold);
		this.setCellString(row, columnaOrdenPago, "Orden Pago / Pago", style_bold);
		this.setCellString(row, columnaBeneficiario, "Beneficiario", style_bold);
		this.setCellString(row, columnaDebe, "Debe", style_bold);
		this.setCellString(row, columnaHaber, "Haber", style_bold);
		this.setCellString(row, columnaSaldo, "Saldo", style_bold);
		this.setCellString(row, columnaOrdenPagoValor, "Orden Pago", style_bold);
		this.setCellString(row, columnaValor, "Valor", style_bold);
		this.setCellString(row, columnaNumero, "Numero", style_bold);
		this.setCellString(row, columnaImporte, "Importe", style_bold);

		for (CuentaMovimiento cuentaMovimiento : cuentaMovimientoService
				.findAllByNumeroCuentaAndFechaContableBetweenAndApertura(numeroCuenta, desde, hasta, (byte) 0)) {
            try {
                log.debug("CuentaMovimiento -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimiento));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
            row = sheet.createRow(++fila);
			this.setCellString(row, columnaFecha,
					format.format(cuentaMovimiento.getFechaContable().withOffsetSameInstant(ZoneOffset.UTC)), style_normal);
			this.setCellInteger(row, columnaAsiento, cuentaMovimiento.getOrdenContable(), style_normal);
			Comprobante comprobante = comprobantes.get(cuentaMovimiento.getComprobanteId());
			this.setCellString(row, columnaComprobante, comprobante.getDescripcion(), style_normal);

			// proveedor de la operacion
			if (cuentaMovimiento.getProveedorId() != null && cuentaMovimiento.getProveedorId() != 0) {
				this.setCellString(row, columnaProveedor, cuentaMovimiento.getProveedor().getRazonSocial(), style_normal);
			}

			this.setCellString(row, columnaConcepto, cuentaMovimiento.getConcepto(), style_normal);

			// busqueda de la OP
			ProveedorPago proveedorPago = proveedorPagoService.findAllByFactura(cuentaMovimiento.getProveedorMovimientoId()).stream().findFirst().orElse(null);
            try {
                log.debug("proveedorPago -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorPago));
            } catch (JsonProcessingException e) {
                log.debug("Sin proveedorPago");
            }
			if (proveedorPago != null) {
				ProveedorMovimiento proveedorMovimientoOP = proveedorPago.getOrdenPago();
                try {
                    log.debug("proveedorMovimientoOP -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorMovimientoOP));
                } catch (JsonProcessingException e) {
                    log.debug("Sin proveedorMovimientoOP");
                }
				String ordenPago = proveedorMovimientoOP.getComprobante().getDescripcion() + " / " + proveedorMovimientoOP.getPrefijo() + " / " + proveedorMovimientoOP.getNumeroComprobante();
				this.setCellString(row, columnaOrdenPago, ordenPago, style_normal);
				this.setCellString(row, columnaBeneficiario, proveedorMovimientoOP.getNombreBeneficiario(), style_normal);
            }
            //

			if (cuentaMovimiento.getDebita() == 1) {
				this.setCellBigDecimal(row, columnaDebe, cuentaMovimiento.getImporte(), style_normal);
				saldo = saldo.add(cuentaMovimiento.getImporte()).setScale(2, RoundingMode.HALF_UP);
			} else {
				this.setCellBigDecimal(row, columnaHaber, cuentaMovimiento.getImporte(), style_normal);
				saldo = saldo.subtract(cuentaMovimiento.getImporte()).setScale(2, RoundingMode.HALF_UP);
			}
			this.setCellBigDecimal(row, columnaSaldo, saldo, style_normal);
			try {
				ValorMovimiento valorMovimiento = valorMovimientoService
						.findFirstByContable(cuentaMovimiento.getFechaContable(), cuentaMovimiento.getOrdenContable());
				ProveedorValor proveedorValor = proveedorValorService
						.findByValorMovimientoId(valorMovimiento.getValorMovimientoId());
				ProveedorMovimiento proveedorMovimiento = proveedorMovimientoService
						.findByProveedorMovimientoId(proveedorValor.getProveedorMovimientoId());
				Valor valor = valorService.findByValorId(valorMovimiento.getValorId());
				this.setCellString(row, columnaOrdenPagoValor,
						proveedorMovimiento.getPrefijo() + "/" + proveedorMovimiento.getNumeroComprobante(),
						style_normal);
				this.setCellString(row, columnaValor, valor.getConcepto(), style_normal);
				this.setCellLong(row, columnaNumero, valorMovimiento.getNumero(), style_normal);
				this.setCellBigDecimal(row, columnaImporte, valorMovimiento.getImporte(), style_normal);
			} catch (ValorMovimientoException e) {
				log.debug("Sin Valor");
			} catch (ProveedorValorException e) {
				log.debug("Sin Pago");
			} catch (ProveedorMovimientoException e) {
				log.debug("Sin Orden Pago");
			} catch (ValorException e) {
				log.debug("Sin Tipo");
			}
		}

		for (Integer column = 0; column < sheet.getRow(4).getPhysicalNumberOfCells(); column++)
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

	private void setCellLong(Row row, int column, Long value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private void setCellInteger(Row row, int column, Integer value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private void setCellString(Row row, int column, String value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private void setCellBigDecimal(Row row, int column, BigDecimal value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value.doubleValue());
		cell.setCellStyle(style);
	}

}

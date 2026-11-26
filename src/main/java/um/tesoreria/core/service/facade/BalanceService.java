/**
 *
 */
package um.tesoreria.core.service.facade;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.exception.ProveedorValorException;
import um.tesoreria.core.exception.ValorMovimientoException;
import um.tesoreria.core.exception.ValorException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.ProveedorMovimientoEntity;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.service.*;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException.Codigo;
import um.tesoreria.core.service.facade.sumasysaldos.SumasSaldosFila;
import um.tesoreria.core.service.facade.sumasysaldos.SumasSaldosService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BalanceService {

	private final CuentaService cuentaService;
	private final ContabilidadService contabilidadService;
	private final CuentaMovimientoService cuentaMovimientoService;
	private final ComprobanteService comprobanteService;
	private final ValorMovimientoService valorMovimientoService;
	private final ProveedorValorService proveedorValorService;
	private final ProveedorMovimientoService proveedorMovimientoService;
	private final ValorService valorService;
	private final ProveedorPagoService proveedorPagoService;
	private final SumasSaldosService sumasSaldosService;

	public byte[] makeSumasSaldos(OffsetDateTime desde, OffsetDateTime hasta) {
		Ejercicio ejercicio = contabilidadService.verifyEjercicio(desde, hasta);
		if (ejercicio == null)
			throw new MayorConsultaException(Codigo.FUERA_DE_EJERCICIO, "Desde y hasta deben pertenecer al mismo ejercicio");

		Workbook book = new XSSFWorkbook();
		CellStyle style_normal = book.createCellStyle();
		Font font_normal = book.createFont();
		font_normal.setBold(false);
		style_normal.setFont(font_normal);

		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		Sheet sheet = book.createSheet("Balance");
		Row row = null;
		int filaExcel = -1;
		row = sheet.createRow(++filaExcel);
		this.setCellString(row, 0, "Desde " + format.format(desde), style_normal);
		row = sheet.createRow(++filaExcel);
		this.setCellString(row, 0, "Hasta " + format.format(hasta), style_normal);
		row = sheet.createRow(++filaExcel);
		this.setCellString(row, 0, "#Cuenta", style_normal);
		this.setCellString(row, 1, "Cuenta", style_normal);
		this.setCellString(row, 2, "S.Inicial Débitos", style_normal);
		this.setCellString(row, 3, "S.Inicial Créditos", style_normal);
		this.setCellString(row, 4, "Débitos", style_normal);
		this.setCellString(row, 5, "Créditos", style_normal);
		this.setCellString(row, 6, "Saldo Deudor", style_normal);
		this.setCellString(row, 7, "Saldo Acreedor", style_normal);

		for (SumasSaldosFila fila : sumasSaldosService.filas(ejercicio, desde, hasta)) {
			row = sheet.createRow(++filaExcel);
			this.setCellBigDecimal(row, 0, fila.numeroCuenta(), style_normal);
			this.setCellString(row, 1, fila.nombre(), style_normal);
			this.setCellBigDecimal(row, 2, fila.inicialDebitos(), style_normal);
			this.setCellBigDecimal(row, 3, fila.inicialCreditos(), style_normal);
			this.setCellBigDecimal(row, 4, fila.debitos(), style_normal);
			this.setCellBigDecimal(row, 5, fila.creditos(), style_normal);
			this.setCellBigDecimal(row, 6, fila.saldoDeudor(), style_normal);
			this.setCellBigDecimal(row, 7, fila.saldoAcreedor(), style_normal);
		}

		for (int column = 0; column < sheet.getRow(2).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		return toBytes(book);
	}

	public byte[] makeMayor(BigDecimal numeroCuenta, OffsetDateTime desde, OffsetDateTime hasta) {
		Ejercicio ejercicio = contabilidadService.verifyEjercicio(desde, hasta);
		if (ejercicio == null)
			throw new MayorConsultaException(Codigo.FUERA_DE_EJERCICIO, "Desde y hasta deben pertenecer al mismo ejercicio");

		Cuenta cuenta = cuentaService.findByNumeroCuenta(numeroCuenta)
				.orElse(null);
		Map<Integer, Comprobante> comprobantes = comprobanteService.findAll().stream()
				.collect(Collectors.toMap(Comprobante::getComprobanteId, Function.identity(), (comprobante, replacement) -> comprobante));

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
		int fila = -1;
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
			row = sheet.createRow(++fila);
			this.setCellString(row, columnaFecha,
					format.format(Objects.requireNonNull(cuentaMovimiento.getFechaContable()).withOffsetSameInstant(ZoneOffset.UTC)), style_normal);
			this.setCellInteger(row, columnaAsiento, cuentaMovimiento.getOrdenContable(), style_normal);
			Comprobante comprobante = comprobantes.get(cuentaMovimiento.getComprobanteId());
			this.setCellString(row, columnaComprobante, comprobante.getDescripcion(), style_normal);

			// proveedor de la operacion
			if (cuentaMovimiento.getProveedorId() != null && cuentaMovimiento.getProveedorId() != 0) {
				this.setCellString(row, columnaProveedor, Objects.requireNonNull(cuentaMovimiento.getProveedor()).getRazonSocial(), style_normal);
			}

			this.setCellString(row, columnaConcepto, cuentaMovimiento.getConcepto(), style_normal);

			// busqueda de la OP
			ProveedorPago proveedorPago = proveedorPagoService.findAllByFactura(cuentaMovimiento.getProveedorMovimientoId()).stream().findFirst().orElse(null);
			if (proveedorPago != null) {
				ProveedorMovimientoEntity proveedorMovimientoOP = proveedorPago.getOrdenPago();
				assert proveedorMovimientoOP != null;
				String ordenPago = Objects.requireNonNull(proveedorMovimientoOP.getComprobante()).getDescripcion() + " / " + proveedorMovimientoOP.getPrefijo() + " / " + proveedorMovimientoOP.getNumeroComprobante();
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

		for (int column = 0; column < sheet.getRow(4).getPhysicalNumberOfCells(); column++)
			sheet.autoSizeColumn(column);

		return toBytes(book);
	}

	private byte[] toBytes(Workbook book) {
		try (book; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			book.write(output);
			return output.toByteArray();
		} catch (IOException e) {
			throw new UncheckedIOException("No se pudo generar el Excel", e);
		}
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
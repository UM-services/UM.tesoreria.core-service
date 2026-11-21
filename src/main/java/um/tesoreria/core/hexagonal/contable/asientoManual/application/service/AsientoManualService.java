package um.tesoreria.core.hexagonal.contable.asientoManual.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.asiento.application.exception.AsientoException;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableResultado;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.asientoManual.application.exception.AsientoManualException;
import um.tesoreria.core.hexagonal.contable.asientoManual.application.exception.AsientoManualException.Codigo;
import um.tesoreria.core.hexagonal.contable.asientoManual.domain.ports.in.AsientoManualUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Asiento Contable manual (legacy {@code frmAsiento} / {@code clsCtlContable.saveAsiento}).
 *
 * <p>Paridad: vínculo "Registro de Asiento", líneas con {@code comprobanteId = 1}, concepto y
 * número manual opcional. Diferencias deliberadas: el número se reserva con el asignador y no se
 * permite pisar un asiento existente (el legacy hacía upsert).</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AsientoManualService implements AsientoManualUseCase {

    private static final int COMPROBANTE_ASIENTO = 1;
    private static final String VINCULO = "Registro de Asiento";

    private final AsientoService asientoService;
    private final CuentaMovimientoService cuentaMovimientoService;
    private final CuentaService cuentaService;
    private final ReservarOrdenContableUseCase reservarOrdenContable;
    private final EjercicioRepository ejercicioRepository;

    @Override
    @Transactional
    public AsientoContableResultado registrar(OffsetDateTime fecha, Integer numero, String concepto,
                                              List<AsientoContableLinea> lineas) {
        OffsetDateTime f = normalizar(fecha);
        validarEjercicio(f);
        List<AsientoContableLinea> validas = validarLineas(lineas);

        Integer orden;
        if (numero != null) {
            if (!ordenLibre(f, numero)) {
                throw new AsientoManualException(Codigo.ORDEN_OCUPADO,
                        "El asiento " + numero + " de " + f.toLocalDate() + " ya existe");
            }
            orden = numero;
        } else {
            orden = reservarOrdenContable.reservar(f);
        }

        asientoService.add(new AsientoEntity(null, f, orden, VINCULO, null, null, null, null));
        List<CuentaMovimiento> movimientos = crearLineas(f, orden, concepto, validas);
        if (numero != null) {
            reservarOrdenContable.registrarUso(f, numero);
        }
        return resultado(f, orden, VINCULO, movimientos);
    }

    @Override
    public AsientoContableResultado consultar(OffsetDateTime fecha, Integer orden) {
        OffsetDateTime f = normalizar(fecha);
        String vinculo = vinculo(f, orden);
        if (vinculo != null && !VINCULO.equals(vinculo)) {
            throw new AsientoManualException(Codigo.ASIENTO_INEXISTENTE,
                    "El asiento " + orden + " de " + f.toLocalDate() + " no es un asiento manual");
        }
        List<CuentaMovimiento> movimientos = lineasManuales(f, orden);
        if (movimientos.isEmpty()) {
            throw new AsientoManualException(Codigo.ASIENTO_INEXISTENTE,
                    "No existe el asiento manual " + orden + " de " + f.toLocalDate());
        }
        return resultado(f, orden, vinculo, movimientos);
    }

    @Override
    @Transactional
    public void eliminar(OffsetDateTime fecha, Integer orden) {
        OffsetDateTime f = normalizar(fecha);
        validarEjercicio(f);
        String vinculo = vinculo(f, orden);
        if (vinculo != null && !VINCULO.equals(vinculo)) {
            throw new AsientoManualException(Codigo.ASIENTO_INEXISTENTE,
                    "El asiento " + orden + " de " + f.toLocalDate() + " no es un asiento manual");
        }
        if (vinculo == null && lineasManuales(f, orden).isEmpty()) {
            throw new AsientoManualException(Codigo.ASIENTO_INEXISTENTE,
                    "No existe el asiento manual " + orden + " de " + f.toLocalDate());
        }

        cuentaMovimientoService.deleteAsiento(f, orden, COMPROBANTE_ASIENTO);
        try {
            AsientoEntity asiento = asientoService.findByAsiento(f, orden);
            asientoService.deleteByAsientoId(asiento.getAsientoId());
        } catch (AsientoException ignored) {
            // Sin cabecera: nada más que borrar.
        }
    }

    @Override
    public List<AsientoContableLinea> importar(InputStream archivo) {
        List<AsientoContableLinea> lineas = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(archivo)) {
            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet sheet = workbook.getSheetAt(s);
                Row header = sheet.getRow(0);
                if (header == null) {
                    continue;
                }
                int colCuenta = -1;
                int colDebe = -1;
                int colHaber = -1;
                for (int c = 0; c < header.getLastCellNum(); c++) {
                    String nombre = texto(header.getCell(c));
                    if (nombre == null) {
                        continue;
                    }
                    switch (nombre.toLowerCase(Locale.ROOT).trim()) {
                        case "cuenta" -> colCuenta = c;
                        case "debe" -> colDebe = c;
                        case "haber" -> colHaber = c;
                        default -> {
                        }
                    }
                }
                if (colCuenta < 0) {
                    continue;
                }
                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) {
                        continue;
                    }
                    BigDecimal cuenta = numero(row.getCell(colCuenta));
                    if (cuenta == null || cuenta.signum() <= 0) {
                        continue;
                    }
                    BigDecimal debe = colDebe >= 0 ? numero(row.getCell(colDebe)) : null;
                    BigDecimal haber = colHaber >= 0 ? numero(row.getCell(colHaber)) : null;
                    if (debe != null && debe.signum() > 0) {
                        lineas.add(new AsientoContableLinea(cuenta, null, (byte) 1, debe));
                    } else if (haber != null && haber.signum() > 0) {
                        lineas.add(new AsientoContableLinea(cuenta, null, (byte) 0, haber));
                    }
                }
            }
            return lineas;
        } catch (IOException | RuntimeException e) {
            throw new AsientoManualException(Codigo.IMPORTACION_INVALIDA,
                    "No se pudo leer la planilla de asientos", e);
        }
    }

    private List<CuentaMovimiento> crearLineas(OffsetDateTime fecha, Integer orden, String concepto,
                                               List<AsientoContableLinea> lineas) {
        List<CuentaMovimiento> movimientos = new ArrayList<>();
        int item = 0;
        for (AsientoContableLinea linea : lineas) {
            CuentaMovimiento movimiento = CuentaMovimiento.builder()
                    .fechaContable(fecha)
                    .ordenContable(orden)
                    .item(++item)
                    .numeroCuenta(linea.numeroCuenta())
                    .debita(linea.debita())
                    .comprobanteId(COMPROBANTE_ASIENTO)
                    .concepto(concepto == null ? "" : concepto)
                    .importe(linea.importe())
                    .build();
            movimientos.add(cuentaMovimientoService.createCuentaMovimiento(movimiento));
        }
        return movimientos;
    }

    private List<CuentaMovimiento> lineasManuales(OffsetDateTime fecha, Integer orden) {
        return cuentaMovimientoService.findAllByAsiento(fecha, orden, 0, 2).stream()
                .filter(movimiento -> Objects.equals(movimiento.getComprobanteId(), COMPROBANTE_ASIENTO))
                .toList();
    }

    private void validarEjercicio(OffsetDateTime fecha) {
        Ejercicio ejercicio = ejercicioRepository.findByFecha(fecha.toLocalDate())
                .orElseThrow(() -> new AsientoManualException(Codigo.EJERCICIO_INEXISTENTE,
                        "No hay ejercicio para la fecha " + fecha.toLocalDate()));
        if (Byte.valueOf((byte) 1).equals(ejercicio.getBloqueado())) {
            throw new AsientoManualException(Codigo.EJERCICIO_BLOQUEADO,
                    "El ejercicio de la fecha " + fecha.toLocalDate() + " está bloqueado");
        }
    }

    private List<AsientoContableLinea> validarLineas(List<AsientoContableLinea> lineas) {
        if (lineas == null || lineas.isEmpty()) {
            throw new AsientoManualException(Codigo.SIN_LINEAS, "El asiento no tiene líneas");
        }
        for (AsientoContableLinea linea : lineas) {
            if (linea.numeroCuenta() == null || linea.debita() == null
                    || (linea.debita() != 0 && linea.debita() != 1)
                    || linea.importe() == null || linea.importe().signum() == 0) {
                throw new AsientoManualException(Codigo.LINEA_INVALIDA,
                        "Línea inválida: cuenta, debe/haber e importe son obligatorios (importe distinto de 0)");
            }
        }
        return lineas;
    }

    private boolean ordenLibre(OffsetDateTime fecha, Integer orden) {
        if (vinculo(fecha, orden) != null) {
            return false;
        }
        return cuentaMovimientoService.findAllByAsiento(fecha, orden, 0, 2).isEmpty();
    }

    private String vinculo(OffsetDateTime fecha, Integer orden) {
        try {
            return asientoService.findByAsiento(fecha, orden).getVinculo();
        } catch (AsientoException e) {
            return null;
        }
    }

    private AsientoContableResultado resultado(OffsetDateTime fecha, Integer orden, String vinculo,
                                                List<CuentaMovimiento> movimientos) {
        Map<BigDecimal, String> nombres = nombres(movimientos);
        List<AsientoContableLinea> lineas = movimientos.stream()
                .map(movimiento -> new AsientoContableLinea(
                        movimiento.getNumeroCuenta(),
                        nombre(movimiento, nombres),
                        movimiento.getDebita(),
                        movimiento.getImporte()))
                .toList();
        BigDecimal debe = total(lineas, (byte) 1);
        BigDecimal haber = total(lineas, (byte) 0);
        return new AsientoContableResultado(fecha, orden, vinculo, debe, haber,
                debe.compareTo(haber) == 0, lineas);
    }

    private Map<BigDecimal, String> nombres(List<CuentaMovimiento> movimientos) {
        List<BigDecimal> cuentas = movimientos.stream()
                .map(CuentaMovimiento::getNumeroCuenta)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (cuentas.isEmpty()) {
            return Map.of();
        }
        return cuentaService.findAllByNumeroCuentaIn(cuentas).stream()
                .filter(cuenta -> cuenta.getNumeroCuenta() != null)
                .collect(Collectors.toMap(Cuenta::getNumeroCuenta,
                        cuenta -> cuenta.getNombre() == null ? "" : cuenta.getNombre(),
                        (primero, segundo) -> primero));
    }

    private static String nombre(CuentaMovimiento movimiento, Map<BigDecimal, String> nombres) {
        if (movimiento.getCuenta() != null && movimiento.getCuenta().getNombre() != null) {
            return movimiento.getCuenta().getNombre();
        }
        return nombres.get(movimiento.getNumeroCuenta());
    }

    private static BigDecimal total(List<AsientoContableLinea> lineas, byte debita) {
        return lineas.stream()
                .filter(linea -> linea.debita() != null && linea.debita() == debita)
                .map(AsientoContableLinea::importe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static OffsetDateTime normalizar(OffsetDateTime fecha) {
        if (fecha == null) {
            throw new AsientoManualException(Codigo.LINEA_INVALIDA, "La fecha contable es obligatoria");
        }
        return fecha.withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDate()
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
    }

    private static String texto(Cell cell) {
        return cell == null ? null : new DataFormatter().formatCellValue(cell);
    }

    private static BigDecimal numero(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType tipo = cell.getCellType();
        if (tipo == CellType.FORMULA) {
            tipo = cell.getCachedFormulaResultType();
        }
        if (tipo == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        if (tipo == CellType.STRING) {
            String valor = cell.getStringCellValue().trim();
            if (valor.isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(valor.replace(",", "."));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

}

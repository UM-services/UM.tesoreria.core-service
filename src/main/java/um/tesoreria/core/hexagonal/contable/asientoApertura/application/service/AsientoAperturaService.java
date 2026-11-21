package um.tesoreria.core.hexagonal.contable.asientoApertura.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableResultado;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.asientoApertura.application.exception.AsientoAperturaException;
import um.tesoreria.core.hexagonal.contable.asientoApertura.application.exception.AsientoAperturaException.Codigo;
import um.tesoreria.core.hexagonal.contable.asientoApertura.domain.ports.in.AsientoAperturaUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Asiento de apertura (legacy {@code frmAsientoApertura}): clona el asiento de cierre del ejercicio
 * anterior, invierte Debe/Haber y lo guarda al inicio del ejercicio actual.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AsientoAperturaService implements AsientoAperturaUseCase {

    private static final int COMPROBANTE_ASIENTO = 1;
    private static final String VINCULO = "Asiento Apertura";

    private final AsientoService asientoService;
    private final CuentaMovimientoService cuentaMovimientoService;
    private final CuentaService cuentaService;
    private final ReservarOrdenContableUseCase reservarOrdenContable;
    private final EjercicioRepository ejercicioRepository;

    @Override
    public AsientoContableResultado preview(Integer ejercicioId, Integer referencia) {
        Ejercicio ejercicio = ejercicio(ejercicioId);
        if (referencia == null) {
            throw new AsientoAperturaException(Codigo.REFERENCIA_INVALIDA,
                    "Falta el asiento de referencia (cierre del ejercicio anterior)");
        }
        Ejercicio anterior = ejercicioRepository.findByEjercicioId(ejercicioId - 1)
                .orElseThrow(() -> new AsientoAperturaException(Codigo.EJERCICIO_ANTERIOR_INEXISTENTE,
                        "No existe el ejercicio anterior " + (ejercicioId - 1)));

        List<CuentaMovimiento> referenciaLineas = cuentaMovimientoService.findAllByAsiento(
                fecha(anterior.getFechaFinal()), referencia, 0, 2);
        if (referenciaLineas.isEmpty()) {
            throw new AsientoAperturaException(Codigo.REFERENCIA_INVALIDA,
                    "El asiento de referencia " + referencia + " de " + anterior.getFechaFinal()
                            + " no existe o no tiene líneas");
        }

        Map<BigDecimal, String> nombres = nombres(referenciaLineas);
        List<AsientoContableLinea> lineas = referenciaLineas.stream()
                .map(movimiento -> new AsientoContableLinea(
                        movimiento.getNumeroCuenta(),
                        nombre(movimiento, nombres),
                        (byte) (1 - movimiento.getDebita()),
                        movimiento.getImporte()))
                .toList();

        return resultado(fecha(ejercicio.getFechaInicio()), null, lineas);
    }

    @Override
    @Transactional
    public AsientoContableResultado registrar(Integer ejercicioId, String concepto,
                                              List<AsientoContableLinea> lineas) {
        Ejercicio ejercicio = ejercicio(ejercicioId);
        if (Byte.valueOf((byte) 1).equals(ejercicio.getBloqueado())) {
            throw new AsientoAperturaException(Codigo.EJERCICIO_BLOQUEADO,
                    "El ejercicio " + ejercicioId + " está bloqueado");
        }
        List<AsientoContableLinea> validas = validarLineas(lineas);
        OffsetDateTime fecha = fecha(ejercicio.getFechaInicio());
        Integer orden = reservarOrdenContable.reservar(fecha);

        asientoService.add(new AsientoEntity(null, fecha, orden, VINCULO, null, null, null, null));
        String conceptoLinea = (concepto == null || concepto.isBlank())
                ? "Asiento Apertura " + ejercicio.getNombre()
                : concepto;
        crearLineas(fecha, orden, conceptoLinea, validas);
        return resultado(fecha, orden, validas);
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
                    .concepto(concepto)
                    .importe(linea.importe())
                    .build();
            movimientos.add(cuentaMovimientoService.createCuentaMovimiento(movimiento));
        }
        return movimientos;
    }

    private Ejercicio ejercicio(Integer ejercicioId) {
        return ejercicioRepository.findByEjercicioId(ejercicioId)
                .orElseThrow(() -> new AsientoAperturaException(Codigo.EJERCICIO_INEXISTENTE,
                        "No existe el ejercicio " + ejercicioId));
    }

    private List<AsientoContableLinea> validarLineas(List<AsientoContableLinea> lineas) {
        if (lineas == null || lineas.isEmpty()) {
            throw new AsientoAperturaException(Codigo.SIN_LINEAS, "El asiento de apertura no tiene líneas");
        }
        for (AsientoContableLinea linea : lineas) {
            if (linea.numeroCuenta() == null || linea.debita() == null
                    || (linea.debita() != 0 && linea.debita() != 1)
                    || linea.importe() == null || linea.importe().signum() == 0) {
                throw new AsientoAperturaException(Codigo.LINEA_INVALIDA, "Línea inválida en el asiento de apertura");
            }
        }
        return lineas;
    }

    private AsientoContableResultado resultado(OffsetDateTime fecha, Integer orden,
                                                List<AsientoContableLinea> lineas) {
        BigDecimal debe = total(lineas, (byte) 1);
        BigDecimal haber = total(lineas, (byte) 0);
        return new AsientoContableResultado(fecha, orden, VINCULO, debe, haber,
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

    private static OffsetDateTime fecha(LocalDate fecha) {
        return fecha.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

}

package um.tesoreria.core.hexagonal.contable.asientoCierre.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableResultado;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.asientoCierre.application.exception.AsientoCierreException;
import um.tesoreria.core.hexagonal.contable.asientoCierre.application.exception.AsientoCierreException.Codigo;
import um.tesoreria.core.hexagonal.contable.asientoCierre.domain.model.VarianteCierre;
import um.tesoreria.core.hexagonal.contable.asientoCierre.domain.ports.in.AsientoCierreUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.SumaCuenta;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Asientos de cierre (legacy {@code frmAsientoCierreRtdo} y {@code frmAsientoCierreActPas}):
 * recorre las cuentas de cierre, toma el saldo (debe - haber) del período y genera una línea por
 * cuenta con saldo distinto de cero, invirtiendo el sentido.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AsientoCierreService implements AsientoCierreUseCase {

    private static final int COMPROBANTE_ASIENTO = 1;

    private final AsientoService asientoService;
    private final CuentaMovimientoService cuentaMovimientoService;
    private final CuentaService cuentaService;
    private final ReservarOrdenContableUseCase reservarOrdenContable;
    private final EjercicioRepository ejercicioRepository;

    @Override
    public AsientoContableResultado preview(Integer ejercicioId, VarianteCierre variante) {
        Ejercicio ejercicio = ejercicio(ejercicioId);
        List<Cuenta> cuentas = variante == VarianteCierre.RESULTADO
                ? cuentaService.findAllByCierreResultado()
                : cuentaService.findAllByCierreActivoPasivo();

        OffsetDateTime desde = fecha(ejercicio.getFechaInicio());
        OffsetDateTime hasta = fecha(ejercicio.getFechaFinal());
        Map<BigDecimal, SumaCuenta> saldos = cuentaMovimientoService.sumarPorCuenta(desde, hasta, (byte) 0)
                .stream()
                .collect(Collectors.toMap(SumaCuenta::numeroCuenta, suma -> suma,
                        (primera, segunda) -> new SumaCuenta(primera.numeroCuenta(),
                                primera.debitos().add(segunda.debitos()),
                                primera.creditos().add(segunda.creditos()))));

        List<AsientoContableLinea> lineas = new ArrayList<>();
        for (Cuenta cuenta : cuentas) {
            if (cuenta.getNumeroCuenta() == null) {
                continue;
            }
            SumaCuenta suma = saldos.get(cuenta.getNumeroCuenta());
            if (suma == null) {
                continue;
            }
            BigDecimal saldo = suma.debitos().subtract(suma.creditos());
            if (saldo.signum() == 0) {
                continue;
            }
            lineas.add(new AsientoContableLinea(cuenta.getNumeroCuenta(), cuenta.getNombre(),
                    (byte) (saldo.signum() > 0 ? 0 : 1), saldo.abs()));
        }

        return resultado(fecha(ejercicio.getFechaFinal()), null, variante, lineas);
    }

    @Override
    @Transactional
    public AsientoContableResultado registrar(Integer ejercicioId, VarianteCierre variante, String concepto,
                                              List<AsientoContableLinea> lineas) {
        Ejercicio ejercicio = ejercicio(ejercicioId);
        if (Byte.valueOf((byte) 1).equals(ejercicio.getBloqueado())) {
            throw new AsientoCierreException(Codigo.EJERCICIO_BLOQUEADO,
                    "El ejercicio " + ejercicioId + " está bloqueado");
        }
        List<AsientoContableLinea> validas = validarLineas(lineas);
        OffsetDateTime fecha = fecha(ejercicio.getFechaFinal());
        Integer orden = reservarOrdenContable.reservar(fecha);

        asientoService.add(new AsientoEntity(null, fecha, orden, variante.vinculo(), null, null, null, null));
        crearLineas(fecha, orden, concepto, validas);
        return resultado(fecha, orden, variante, validas);
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

    private Ejercicio ejercicio(Integer ejercicioId) {
        return ejercicioRepository.findByEjercicioId(ejercicioId)
                .orElseThrow(() -> new AsientoCierreException(Codigo.EJERCICIO_INEXISTENTE,
                        "No existe el ejercicio " + ejercicioId));
    }

    private List<AsientoContableLinea> validarLineas(List<AsientoContableLinea> lineas) {
        if (lineas == null || lineas.isEmpty()) {
            throw new AsientoCierreException(Codigo.SIN_LINEAS, "El asiento de cierre no tiene líneas");
        }
        for (AsientoContableLinea linea : lineas) {
            if (linea.numeroCuenta() == null || linea.debita() == null
                    || (linea.debita() != 0 && linea.debita() != 1)
                    || linea.importe() == null || linea.importe().signum() == 0) {
                throw new AsientoCierreException(Codigo.LINEA_INVALIDA, "Línea inválida en el asiento de cierre");
            }
        }
        return lineas;
    }

    private AsientoContableResultado resultado(OffsetDateTime fecha, Integer orden, VarianteCierre variante,
                                                List<AsientoContableLinea> lineas) {
        BigDecimal debe = total(lineas, (byte) 1);
        BigDecimal haber = total(lineas, (byte) 0);
        return new AsientoContableResultado(fecha, orden, variante.vinculo(), debe, haber,
                debe.compareTo(haber) == 0, lineas);
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

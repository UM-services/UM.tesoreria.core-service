package um.tesoreria.core.service.facade.mayor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import um.tesoreria.core.exception.ProveedorValorException;
import um.tesoreria.core.exception.ValorException;
import um.tesoreria.core.exception.ValorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.ProveedorMovimientoEntity;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.kotlin.model.ProveedorPago;
import um.tesoreria.core.kotlin.model.ProveedorValor;
import um.tesoreria.core.kotlin.model.Valor;
import um.tesoreria.core.kotlin.model.ValorMovimiento;
import um.tesoreria.core.service.EjercicioService;
import um.tesoreria.core.service.ProveedorPagoService;
import um.tesoreria.core.service.ProveedorValorService;
import um.tesoreria.core.service.ValorMovimientoService;
import um.tesoreria.core.service.ValorService;
import um.tesoreria.core.service.facade.ContabilidadService;

import um.tesoreria.core.service.facade.mayor.MayorConsultaException.Codigo;

/**
 * Consulta del mayor en JSON para una o varias cuentas y un rango de fechas que puede cruzar ejercicios.
 *
 * <p>Capa legacy a propósito (decisión de la issue): depende de servicios legacy (saldo inicial, orden de pago,
 * valor) que un slice no puede importar. Las reglas de cálculo y validación viven acá y no en el front.
 *
 * <p>Regla de saldo (se mantiene como en {@code BalanceService.makeMayor}): el saldo inicial es el del ejercicio
 * de {@code desde}; el período se consulta sin los asientos de apertura ({@code apertura = 0}), salvo que {@code desde}
 * sea el primer día del ejercicio: ahí el saldo inicial es cero y el asiento de apertura se lista como primer
 * movimiento (como en el listado anterior); el saldo acumulado
 * continúa corrido a lo largo de todo el rango, sin cortes por ejercicio.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MayorConsultaService {

    private static final Byte SIN_APERTURA = (byte) 0;
    private static final Byte CON_APERTURA = (byte) 1;

    private final EjercicioService ejercicioService;
    private final CuentaService cuentaService;
    private final ContabilidadService contabilidadService;
    private final CuentaMovimientoService cuentaMovimientoService;
    private final ComprobanteService comprobanteService;
    private final ProveedorPagoService proveedorPagoService;
    private final ValorMovimientoService valorMovimientoService;
    private final ProveedorValorService proveedorValorService;
    private final ProveedorMovimientoService proveedorMovimientoService;
    private final ValorService valorService;

    public MayorConsultaResponse consultar(MayorConsultaRequest request) {
        if (request == null || request.desde() == null || request.hasta() == null) {
            throw new MayorConsultaException(Codigo.FECHAS_REQUERIDAS, "Desde y hasta son obligatorios");
        }
        OffsetDateTime desde = request.desde();
        OffsetDateTime hasta = request.hasta();
        if (desde.isAfter(hasta)) {
            throw new MayorConsultaException(Codigo.DESDE_POSTERIOR, "La fecha desde no puede ser posterior a hasta");
        }

        List<Ejercicio> ejercicios = ejerciciosDelRango(desde, hasta);
        List<Cuenta> cuentas = resolverCuentas(request);
        Map<Integer, Comprobante> comprobantes = comprobanteService.findAll().stream()
                .collect(Collectors.toMap(Comprobante::getComprobanteId, Function.identity(), (actual, repetido) -> actual));

        List<MayorCuentaResponse> bloques = new ArrayList<>();
        for (Cuenta cuenta : cuentas) {
            bloques.add(armarCuenta(cuenta, desde, hasta, ejercicios, comprobantes));
        }
        return new MayorConsultaResponse(desde, hasta, bloques);
    }

    /**
     * Ejercicios que cubren el rango, en orden. {@code desde} debe caer en un ejercicio, y cada ejercicio siguiente
     * debe empezar el día posterior al cierre del anterior hasta cubrir {@code hasta}.
     */
    private List<Ejercicio> ejerciciosDelRango(OffsetDateTime desde, OffsetDateTime hasta) {
        List<Ejercicio> todos = ejercicioService.findAll().stream()
                .filter(e -> e.getFechaInicio() != null && e.getFechaFinal() != null)
                .sorted(Comparator.comparing(Ejercicio::getFechaInicio))
                .toList();

        Ejercicio actual = todos.stream()
                .filter(e -> contiene(e, desde))
                .findFirst()
                .orElseThrow(() -> new MayorConsultaException(Codigo.FUERA_DE_EJERCICIO,
                        "La fecha desde no pertenece a ningún ejercicio"));

        List<Ejercicio> rango = new ArrayList<>();
        rango.add(actual);
        while (hasta.isAfter(actual.getFechaFinal())) {
            LocalDate esperado = dia(actual.getFechaFinal()).plusDays(1);
            Ejercicio siguiente = todos.stream()
                    .filter(e -> dia(e.getFechaInicio()).equals(esperado))
                    .findFirst()
                    .orElse(null);
            if (siguiente == null) {
                boolean hayPosteriores = todos.stream().anyMatch(e -> dia(e.getFechaInicio()).isAfter(esperado));
                throw hayPosteriores
                        ? new MayorConsultaException(Codigo.EJERCICIOS_NO_CONSECUTIVOS,
                        "Los ejercicios del rango no son consecutivos: falta uno desde el " + esperado)
                        : new MayorConsultaException(Codigo.FUERA_DE_EJERCICIO,
                        "La fecha hasta no pertenece a ningún ejercicio");
            }
            rango.add(siguiente);
            actual = siguiente;
        }
        return rango;
    }

    private List<Cuenta> resolverCuentas(MayorConsultaRequest request) {
        boolean porLista = request.cuentas() != null && !request.cuentas().isEmpty();
        // Swagger y otros clientes completan los números sin usar con 0: se lo toma como «no enviado».
        boolean porRango = informada(request.cuentaDesde()) || informada(request.cuentaHasta());
        if (porLista == porRango) {
            throw new MayorConsultaException(Codigo.SELECCION_INVALIDA,
                    "Indicar las cuentas por lista o por rango (desde y hasta), pero no ambas ni ninguna");
        }

        if (porLista) {
            // TreeSet usa compareTo: elimina repetidos aunque cambie la escala y ordena por número.
            TreeSet<BigDecimal> numeros = request.cuentas().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(TreeSet::new));
            if (numeros.isEmpty()) {
                throw new MayorConsultaException(Codigo.SELECCION_INVALIDA, "La lista de cuentas está vacía");
            }
            List<Cuenta> cuentas = new ArrayList<>();
            for (BigDecimal numero : numeros) {
                cuentas.add(cuentaService.findByNumeroCuenta(numero)
                        .orElseThrow(() -> new MayorConsultaException(Codigo.CUENTA_INEXISTENTE,
                                "No existe la cuenta " + numero.toPlainString())));
            }
            return cuentas;
        }

        BigDecimal cuentaDesde = request.cuentaDesde();
        BigDecimal cuentaHasta = request.cuentaHasta();
        if (!informada(cuentaDesde) || !informada(cuentaHasta) || cuentaDesde.compareTo(cuentaHasta) > 0) {
            throw new MayorConsultaException(Codigo.SELECCION_INVALIDA,
                    "El rango de cuentas requiere desde y hasta, con desde menor o igual a hasta");
        }
        // Pendiente de confirmar en la issue: el rango incluye solo cuentas imputables (no integradoras).
        List<Cuenta> cuentas = cuentaService.findAll().stream()
                .filter(c -> c.getNumeroCuenta() != null
                        && c.getNumeroCuenta().compareTo(cuentaDesde) >= 0
                        && c.getNumeroCuenta().compareTo(cuentaHasta) <= 0)
                .filter(c -> c.getIntegradora() == null || c.getIntegradora() == 0)
                .sorted(Comparator.comparing(Cuenta::getNumeroCuenta))
                .toList();
        if (cuentas.isEmpty()) {
            throw new MayorConsultaException(Codigo.RANGO_SIN_CUENTAS,
                    "No hay cuentas imputables entre " + cuentaDesde.toPlainString() + " y " + cuentaHasta.toPlainString());
        }
        return cuentas;
    }

    private MayorCuentaResponse armarCuenta(Cuenta cuenta, OffsetDateTime desde, OffsetDateTime hasta,
                                            List<Ejercicio> ejercicios, Map<Integer, Comprobante> comprobantes) {
        BigDecimal numeroCuenta = cuenta.getNumeroCuenta();

        // Si el período arranca el primer día del ejercicio, el asiento de apertura se muestra como un movimiento más
        // (como en el listado anterior) y el saldo inicial es cero. Si arranca después, el saldo inicial lo incluye.
        OffsetDateTime inicioEjercicio = Objects.requireNonNull(ejercicios.getFirst().getFechaInicio())
                .withOffsetSameInstant(ZoneOffset.UTC);
        boolean arrancaConLaApertura = desde.withOffsetSameInstant(ZoneOffset.UTC).isEqual(inicioEjercicio);

        BigDecimal inicialDebe = BigDecimal.ZERO;
        BigDecimal inicialHaber = BigDecimal.ZERO;
        List<CuentaMovimiento> lineas = new ArrayList<>();
        if (arrancaConLaApertura) {
            lineas.addAll(cuentaMovimientoService.findAllByNumeroCuentaAndFechaContableBetweenAndApertura(
                    numeroCuenta, desde, desde, CON_APERTURA));
        } else {
            List<BigDecimal> iniciales = contabilidadService.saldoInicial(numeroCuenta, ejercicios.getFirst(), desde);
            inicialDebe = iniciales.get(0);
            inicialHaber = iniciales.get(1);
        }
        BigDecimal saldo = BigDecimal.ZERO.add(inicialDebe).setScale(2, RoundingMode.HALF_UP);
        saldo = saldo.subtract(inicialHaber).setScale(2, RoundingMode.HALF_UP);
        MayorSaldoInicialResponse saldoInicial = new MayorSaldoInicialResponse(inicialDebe, inicialHaber, saldo);

        lineas.addAll(cuentaMovimientoService
                .findAllByNumeroCuentaAndFechaContableBetweenAndApertura(numeroCuenta, desde, hasta, SIN_APERTURA));

        BigDecimal totalDebe = BigDecimal.ZERO;
        BigDecimal totalHaber = BigDecimal.ZERO;
        List<MayorMovimientoResponse> movimientos = new ArrayList<>();

        for (CuentaMovimiento movimiento : lineas) {
            BigDecimal debe = null;
            BigDecimal haber = null;
            if (movimiento.getDebita() == 1) {
                debe = movimiento.getImporte();
                totalDebe = totalDebe.add(debe);
                saldo = saldo.add(debe).setScale(2, RoundingMode.HALF_UP);
            } else {
                haber = movimiento.getImporte();
                totalHaber = totalHaber.add(haber);
                saldo = saldo.subtract(haber).setScale(2, RoundingMode.HALF_UP);
            }
            movimientos.add(armarMovimiento(movimiento, debe, haber, saldo, ejercicios, comprobantes));
        }

        return new MayorCuentaResponse(numeroCuenta, cuenta.getNombre(), saldoInicial, movimientos,
                totalDebe.setScale(2, RoundingMode.HALF_UP), totalHaber.setScale(2, RoundingMode.HALF_UP), saldo);
    }

    private MayorMovimientoResponse armarMovimiento(CuentaMovimiento movimiento, BigDecimal debe, BigDecimal haber,
                                                    BigDecimal saldo, List<Ejercicio> ejercicios,
                                                    Map<Integer, Comprobante> comprobantes) {
        Ejercicio ejercicio = ejercicios.stream()
                .filter(e -> movimiento.getFechaContable() != null && contiene(e, movimiento.getFechaContable()))
                .findFirst()
                .orElse(null);
        Comprobante comprobante = comprobantes.get(movimiento.getComprobanteId());
        String proveedor = movimiento.getProveedorId() != null && movimiento.getProveedorId() != 0
                && movimiento.getProveedor() != null ? movimiento.getProveedor().getRazonSocial() : null;

        String ordenPago = null;
        String beneficiario = null;
        if (movimiento.getProveedorMovimientoId() != null) {
            ProveedorPago proveedorPago = proveedorPagoService.findAllByFactura(movimiento.getProveedorMovimientoId())
                    .stream().findFirst().orElse(null);
            ProveedorMovimientoEntity ordenDePago = proveedorPago == null ? null : proveedorPago.getOrdenPago();
            if (ordenDePago != null) {
                String descripcion = ordenDePago.getComprobante() == null ? "" : ordenDePago.getComprobante().getDescripcion();
                ordenPago = descripcion + " / " + ordenDePago.getPrefijo() + " / " + ordenDePago.getNumeroComprobante();
                beneficiario = ordenDePago.getNombreBeneficiario();
            }
        }

        String ordenPagoValor = null;
        String valorConcepto = null;
        Long numeroValor = null;
        BigDecimal importeValor = null;
        try {
            ValorMovimiento valorMovimiento = valorMovimientoService
                    .findFirstByContable(movimiento.getFechaContable(), movimiento.getOrdenContable());
            ProveedorValor proveedorValor = proveedorValorService
                    .findByValorMovimientoId(valorMovimiento.getValorMovimientoId());
            ProveedorMovimiento proveedorMovimiento = proveedorMovimientoService
                    .findByProveedorMovimientoId(proveedorValor.getProveedorMovimientoId());
            Valor valor = valorService.findByValorId(valorMovimiento.getValorId());
            ordenPagoValor = proveedorMovimiento.getPrefijo() + "/" + proveedorMovimiento.getNumeroComprobante();
            valorConcepto = valor.getConcepto();
            numeroValor = valorMovimiento.getNumero();
            importeValor = valorMovimiento.getImporte();
        } catch (ValorMovimientoException e) {
            log.debug("Sin Valor");
        } catch (ProveedorValorException e) {
            log.debug("Sin Pago");
        } catch (ProveedorMovimientoException e) {
            log.debug("Sin Orden Pago");
        } catch (ValorException e) {
            log.debug("Sin Tipo");
        }

        return new MayorMovimientoResponse(
                movimiento.getFechaContable(),
                movimiento.getOrdenContable(),
                ejercicio == null ? null : ejercicio.getEjercicioId(),
                ejercicio == null ? null : ejercicio.getNombre(),
                comprobante == null ? null : comprobante.getDescripcion(),
                proveedor,
                movimiento.getConcepto(),
                ordenPago,
                beneficiario,
                debe,
                haber,
                saldo,
                ordenPagoValor,
                valorConcepto,
                numeroValor,
                importeValor);
    }

    private static boolean informada(BigDecimal cuenta) {
        return cuenta != null && cuenta.signum() != 0;
    }

    private static boolean contiene(Ejercicio ejercicio, OffsetDateTime fecha) {
        return !fecha.isBefore(ejercicio.getFechaInicio()) && !fecha.isAfter(ejercicio.getFechaFinal());
    }

    private static LocalDate dia(OffsetDateTime fecha) {
        return fecha.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate();
    }
}
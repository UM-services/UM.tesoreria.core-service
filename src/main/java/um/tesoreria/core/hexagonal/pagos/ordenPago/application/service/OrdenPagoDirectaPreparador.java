package um.tesoreria.core.hexagonal.pagos.ordenPago.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaByIdUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.service.EjercicioService;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoValidacionException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.model.OrdenPagoDirectaPreparado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ContextoGasto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ContextoValor;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaContexto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaGasto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoValor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorService;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.Valor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Arma el contexto de una orden de pago directa: valida el legacy de {@code frmPagoLibre},
 * resuelve la numeración y traduce gastos y valores a las piezas del motor de asientos.
 */
@Component
@RequiredArgsConstructor
public class OrdenPagoDirectaPreparador {

    /** Comprobante fijo del legacy para una orden de pago. */
    private static final Integer COMPROBANTE_ORDEN_PAGO = 6;

    /** Cuenta de la obligación que contrapesa los gastos de una orden de pago directa. */
    private static final BigDecimal CUENTA_OBLIGACION = new BigDecimal("20101090099");

    private final ComprobanteService comprobanteService;
    private final ProveedorMovimientoService proveedorMovimientoService;
    private final EjercicioService ejercicioService;
    private final ValorService valorService;
    private final GetBancariaByIdUseCase getBancariaByIdUseCase;

    public OrdenPagoDirectaPreparado preparar(OrdenPagoDirectaSolicitud solicitud) {
        if (solicitud == null) {
            throw new OrdenPagoValidacionException(List.of("ERROR: Solicitud NO Válida"));
        }
        if (solicitud.fechaComprobante() == null) {
            throw new OrdenPagoValidacionException(List.of("ERROR: Fecha NO Válida"));
        }
        OffsetDateTime fecha = normalizar(solicitud.fechaComprobante());
        Ejercicio ejercicio = ejercicioService.findByFecha(fecha.toLocalDate())
                .orElseThrow(() -> new OrdenPagoValidacionException(
                        List.of("ERROR: No hay Ejercicio para la Fecha " + fecha.toLocalDate())));
        if (ejercicio.getBloqueado() != null && ejercicio.getBloqueado() == 1) {
            throw new OrdenPagoValidacionException(List.of("ERROR: Ejercicio BLOQUEADO"));
        }
        Comprobante comprobante = comprobanteService.findByComprobanteId(COMPROBANTE_ORDEN_PAGO);

        List<String> errores = new ArrayList<>();
        List<ContextoGasto> gastos = contextoGastos(solicitud.gastos(), errores);
        List<ContextoValor> valores = contextoValores(solicitud.valores(), errores);
        BigDecimal totalGastos = totalGastos(solicitud.gastos());
        BigDecimal totalValores = totalValores(solicitud.valores());
        if (totalGastos.compareTo(totalValores) != 0) {
            errores.add("ERROR: Diferencia entre VALORES y TOTAL");
        }
        Long numeroComprobante = numeroComprobante(solicitud.numeroManual(), ejercicio.getEjercicioId(), errores);

        OrdenPagoDirectaContexto contexto = new OrdenPagoDirectaContexto(comprobante.getComprobanteId(),
                comprobante.getDebita(), ejercicio.getEjercicioId(), numeroComprobante, fecha, null, 0, null,
                CUENTA_OBLIGACION, totalGastos, gastos, valores);
        return new OrdenPagoDirectaPreparado(comprobante, ejercicio, contexto, totalGastos, totalValores,
                ejercicio.getEjercicioId(), numeroComprobante, errores);
    }

    private List<ContextoGasto> contextoGastos(List<OrdenPagoDirectaGasto> gastos, List<String> errores) {
        if (gastos == null) {
            return List.of();
        }
        List<ContextoGasto> contexto = new ArrayList<>();
        for (OrdenPagoDirectaGasto gasto : gastos) {
            if (gasto.numeroCuenta() == null || gasto.numeroCuenta().signum() == 0) {
                errores.add("ERROR: Gasto SIN Cuenta Contable");
                continue;
            }
            if (gasto.importe() == null || gasto.importe().signum() == 0) {
                errores.add("ERROR: Importe de Gasto NO Válido");
                continue;
            }
            contexto.add(new ContextoGasto(gasto.numeroCuenta(), gasto.importe()));
        }
        return contexto;
    }

    private List<ContextoValor> contextoValores(List<OrdenPagoValor> valores, List<String> errores) {
        if (valores == null) {
            return List.of();
        }
        List<ContextoValor> contexto = new ArrayList<>();
        int posicion = 0;
        for (OrdenPagoValor valorSolicitud : valores) {
            OffsetDateTime fechaEmision = normalizar(valorSolicitud.fechaEmision());
            if (fechaEmision == null) {
                errores.add("ERROR: Valor SIN Fecha de Emisión");
                continue;
            }
            if (valorSolicitud.importe() == null || valorSolicitud.importe().signum() == 0) {
                errores.add("ERROR: Importe de Valor NO Válido");
                continue;
            }
            Valor valor = valorService.getValorById(valorSolicitud.valorId());
            BigDecimal contrapartida = valor.numeroCuenta();
            if (valorSolicitud.bancariaIdOrigen() != null) {
                Bancaria bancaria = getBancariaByIdUseCase.getBancariaById(valorSolicitud.bancariaIdOrigen());
                if (bancaria.getNumeroCuenta() != null && bancaria.getNumeroCuenta().signum() > 0) {
                    contrapartida = bancaria.getNumeroCuenta();
                }
            }
            if (contrapartida == null || contrapartida.signum() == 0) {
                errores.add("ERROR: Valor SIN Cuenta Contable (" + valor.concepto() + ")");
                continue;
            }
            contexto.add(new ContextoValor(null, valorSolicitud.valorId(), valorSolicitud.importe(),
                    fechaEmision, null, null, contrapartida, posicion++));
        }
        return contexto;
    }

    private Long numeroComprobante(Long numeroManual, Integer prefijo, List<String> errores) {
        if (numeroManual != null && numeroManual > 0) {
            if (proveedorMovimientoService.existsByOrdenPago(prefijo, numeroManual)) {
                errores.add("ERROR: OP Existente");
            }
            return numeroManual;
        }
        if (numeroManual != null) {
            errores.add("ERROR: Número NO Válido");
        }
        return proveedorMovimientoService.findLastOrdenPagoOptional(prefijo)
                .map(ultima -> ultima.getNumeroComprobante() == null ? 1L : ultima.getNumeroComprobante() + 1)
                .orElse(1L);
    }

    private BigDecimal totalGastos(List<OrdenPagoDirectaGasto> gastos) {
        if (gastos == null) {
            return BigDecimal.ZERO;
        }
        return gastos.stream()
                .map(gasto -> gasto.importe() == null ? BigDecimal.ZERO : gasto.importe().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal totalValores(List<OrdenPagoValor> valores) {
        if (valores == null) {
            return BigDecimal.ZERO;
        }
        return valores.stream()
                .map(valor -> valor.importe() == null ? BigDecimal.ZERO : valor.importe().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OffsetDateTime normalizar(OffsetDateTime fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}

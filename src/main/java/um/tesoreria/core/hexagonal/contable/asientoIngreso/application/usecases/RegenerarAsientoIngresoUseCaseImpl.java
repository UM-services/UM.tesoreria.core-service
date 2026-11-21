package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.audit.AuditBulkScope;
import um.tesoreria.core.audit.AuditBulkService;
import um.tesoreria.core.audit.BulkContext;
import um.tesoreria.core.exception.EjercicioBloqueadoException;
import um.tesoreria.core.exception.EjercicioException;
import um.tesoreria.core.exception.FacultadPagoCuentaException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieByUniqueUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.application.exception.AsientoException;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException.Codigo;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.AsientoIngresoCatalogo;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.AsientoIngresoResumenBuilder;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.FechasContables;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoAccion;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoLinea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoRegistro;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResultado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.CuentaFaltante;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.PagoIngreso;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in.RegenerarAsientoIngresoUseCase;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.ChequeraPagoAsientoEscrituraRepository;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.IngresoAsientoMapaRepository;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.IngresoItemRepository;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.service.LectivoTotalImputacionService;
import um.tesoreria.core.model.dto.ItemAsientoDto;
import um.tesoreria.core.model.dto.PagoDto;
import um.tesoreria.core.service.ChequeraSerieReemplazoService;
import um.tesoreria.core.service.EjercicioService;
import um.tesoreria.core.service.FacultadPagoCuentaService;
import um.tesoreria.core.service.facade.PagoService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Regeneración del asiento de ingreso de una fecha y un tipo de pago. Reproduce
 * {@code clsCtlPagos.regenerarAsientoIngreso} en una sola transacción y con las reglas del legacy:
 * reutiliza el orden contable del mapa {@code ingreso_asiento}, borra el asiento anterior, regenera
 * {@code chequera_pago_asiento} por pago y consolida {@code movcon} sumando el debe/haber por pago
 * más las líneas de {@code ingreso_items}.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RegenerarAsientoIngresoUseCaseImpl implements RegenerarAsientoIngresoUseCase {

    private static final DateTimeFormatter FECHA_CONCEPTO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int COMPROBANTE_INGRESO = 3;
    private static final String VINCULO = "Asiento Ingreso";

    private final IngresoAsientoMapaRepository mapaRepository;
    private final IngresoItemRepository ingresoItemRepository;
    private final ChequeraPagoAsientoEscrituraRepository chequeraPagoAsientoRepository;
    private final PagoService pagoService;
    private final ReservarOrdenContableUseCase reservarOrdenContable;
    private final AsientoService asientoService;
    private final CuentaMovimientoService cuentaMovimientoService;
    private final FacultadPagoCuentaService facultadPagoCuentaService;
    private final LectivoTotalImputacionService lectivoTotalImputacionService;
    private final GetChequeraSerieByUniqueUseCase getChequeraSerieByUniqueUseCase;
    private final ChequeraSerieReemplazoService chequeraSerieReemplazoService;
    private final EjercicioService ejercicioService;
    private final AsientoIngresoCatalogo catalogo;
    private final AsientoIngresoResumenBuilder resumenBuilder;
    private final AuditBulkService auditBulkService;
    private final TransactionOperations transaction;

    @Override
    public AsientoIngresoResultado regenerar(OffsetDateTime fecha, Integer tipoPagoId) {
        OffsetDateTime normalizada = FechasContables.normalizar(fecha);
        validarEjercicio(normalizada);
        List<PagoIngreso> pagos = cargarPagos(normalizada, tipoPagoId);
        Map<Integer, BigDecimal> cuentasDebe = resolverCuentasDebe(pagos, tipoPagoId);

        try {
            return Objects.requireNonNull(transaction.execute(status -> {
                try (AuditBulkScope ignored = auditBulkService.begin(
                        BulkContext.of("asiento-ingreso", null, null, null))) {
                    return ejecutar(normalizada, tipoPagoId, pagos, cuentasDebe);
                }
            }));
        } catch (EjercicioBloqueadoException e) {
            throw new AsientoIngresoException(Codigo.EJERCICIO_BLOQUEADO,
                    "El ejercicio de la fecha " + normalizada.toLocalDate() + " está bloqueado");
        }
    }

    private void validarEjercicio(OffsetDateTime fecha) {
        try {
            if (ejercicioService.findByFecha(fecha).getBloqueado() == 1) {
                throw new AsientoIngresoException(Codigo.EJERCICIO_BLOQUEADO,
                        "El ejercicio de la fecha " + fecha.toLocalDate() + " está bloqueado");
            }
        } catch (EjercicioException e) {
            throw new AsientoIngresoException(Codigo.EJERCICIO_INEXISTENTE,
                    "No hay ejercicio para la fecha " + fecha.toLocalDate());
        }
    }

    private List<PagoIngreso> cargarPagos(OffsetDateTime fecha, Integer tipoPagoId) {
        return pagoService.getPagos(tipoPagoId, fecha).stream().map(this::toPago).toList();
    }

    /**
     * El legacy aborta en el primer pago sin cuenta de pago. Se validan todas antes de escribir para
     * informar el problema completo y no dejar nada a medio generar.
     */
    private Map<Integer, BigDecimal> resolverCuentasDebe(List<PagoIngreso> pagos, Integer tipoPagoId) {
        Map<Integer, BigDecimal> cuentas = new LinkedHashMap<>();
        List<Integer> faltantes = new ArrayList<>();
        for (PagoIngreso pago : pagos) {
            Integer facultadId = pago.getFacultadId();
            if (facultadId == null) {
                faltantes.add(null);
                continue;
            }
            if (cuentas.containsKey(facultadId)) {
                continue;
            }
            try {
                var facultadPagoCuenta = facultadPagoCuentaService.findByUnique(facultadId, tipoPagoId);
                if (facultadPagoCuenta.getCuenta() == null) {
                    faltantes.add(facultadId);
                } else {
                    cuentas.put(facultadId, facultadPagoCuenta.getCuenta());
                }
            } catch (FacultadPagoCuentaException e) {
                faltantes.add(facultadId);
            }
        }
        if (!faltantes.isEmpty()) {
            throw new AsientoIngresoException(Codigo.CUENTA_TIPO_PAGO_FALTANTE,
                    "Falta la cuenta de pago del tipo " + tipoPagoId + " para la(s) facultad(es) " + faltantes);
        }
        return cuentas;
    }

    private AsientoIngresoResultado ejecutar(OffsetDateTime fecha, Integer tipoPagoId,
                                             List<PagoIngreso> pagos, Map<Integer, BigDecimal> cuentasDebe) {
        AsientoIngresoRegistro registro = mapaRepository.findWithLockByFechaAndTipoPago(fecha, tipoPagoId)
                .orElse(null);
        if (registro == null) {
            registro = mapaRepository.create(fecha, tipoPagoId, reservarOrdenContable.reservar(fecha));
        } else if (!ordenDisponible(fecha, tipoPagoId, registro.getOrdenContable())) {
            // El orden reservado por el legacy quedó compartido con una OP/factura: se re-reserva
            // para no pisar ni borrar líneas ajenas.
            log.warn("Orden {} de {} tipo {} ocupado por otro comprobante: se re-reserva",
                    registro.getOrdenContable(), fecha.toLocalDate(), tipoPagoId);
            registro = mapaRepository.updateOrden(registro, reservarOrdenContable.reservar(fecha));
        }
        Integer orden = registro.getOrdenContable();

        borrarAsientoPropio(fecha, orden);
        // Las bajas de asiento/movcon quedan encoladas: hay que ejecutarlas antes de insertar las
        // líneas nuevas (Hibernate ordena INSERT antes que DELETE en el mismo flush).
        chequeraPagoAsientoRepository.flush();

        Map<String, CuentaFaltante> faltantes = new LinkedHashMap<>();
        for (PagoIngreso pago : pagos) {
            chequeraPagoAsientoRepository.deleteAllByPago(pago);
            chequeraPagoAsientoRepository.create(pago, 1, cuentasDebe.get(pago.getFacultadId()),
                    (byte) 1, pago.getImporte());

            Integer lectivoId = lectivoId(pago);
            var imputacion = lectivoTotalImputacionService.findByProducto(
                    pago.getFacultadId(), lectivoId, pago.getTipoChequeraId(), pago.getProductoId());
            if (imputacion.isPresent() && imputacion.get().getNumeroCuenta() != null) {
                chequeraPagoAsientoRepository.create(pago, 2, imputacion.get().getNumeroCuenta(),
                        (byte) 0, pago.getImporte());
            } else {
                CuentaFaltante faltante = faltante(pago.getFacultadId(), lectivoId,
                        pago.getTipoChequeraId(), pago.getProductoId());
                faltantes.putIfAbsent(faltante.clave(), faltante);
            }
        }

        String concepto = "Ingreso " + fecha.format(FECHA_CONCEPTO) + " " + tipoPagoId;
        List<CuentaMovimiento> movimientos = new ArrayList<>();
        for (ItemAsientoDto item : pagoService.getItems(tipoPagoId, fecha)) {
            movimientos.add(movimiento(item.getNumeroCuenta(), item.getDebita(), item.getImporte(), concepto));
        }
        for (AsientoIngresoLinea item : ingresoItemRepository.findAllByFechaAndTipoPago(fecha, tipoPagoId)) {
            movimientos.add(movimiento(item.getNumeroCuenta(), item.getDebita(), item.getImporte(), concepto));
        }

        List<CuentaFaltante> faltantesLista = List.copyOf(faltantes.values());

        if (movimientos.isEmpty()) {
            mapaRepository.delete(registro);
            log.debug("Asiento ingreso sin movimientos: {} tipo {}", fecha, tipoPagoId);
            return AsientoIngresoResultado.builder()
                    .resumen(resumenBuilder.build(fecha, tipoPagoId, null))
                    .accion(AsientoIngresoAccion.SIN_MOVIMIENTOS)
                    .faltantes(faltantesLista)
                    .build();
        }

        guardarAsiento(fecha, orden, movimientos);
        return AsientoIngresoResultado.builder()
                .resumen(resumenBuilder.build(fecha, tipoPagoId, registro))
                .accion(AsientoIngresoAccion.GENERADO)
                .faltantes(faltantesLista)
                .build();
    }

    /**
     * Guarda la cabecera y las líneas con la misma secuencia que el flujo legacy de ingreso.
     * No se usa {@code ContabilidadService.saveAsiento} porque su {@code deleteAsientoDesde}
     * final borra la cabecera recién creada (el legacy de ingreso no la borra).
     */
    private void guardarAsiento(OffsetDateTime fecha, Integer orden, List<CuentaMovimiento> movimientos) {
        asientoService.add(new AsientoEntity(null, fecha, orden, VINCULO, null, null, null, null));
        int item = 0;
        for (CuentaMovimiento movimiento : movimientos) {
            movimiento.setFechaContable(fecha);
            movimiento.setOrdenContable(orden);
            movimiento.setItem(++item);
            cuentaMovimientoService.createCuentaMovimiento(movimiento);
        }
    }

    /** El orden del mapa se puede reutilizar sólo si sigue siendo de este ingreso y de nadie más. */
    private boolean ordenDisponible(OffsetDateTime fecha, Integer tipoPagoId, Integer orden) {
        if (mapaRepository.existsOtherByFechaAndOrden(fecha, orden, tipoPagoId)) {
            return false;
        }
        try {
            AsientoEntity asiento = asientoService.findByAsiento(fecha, orden);
            if (!VINCULO.equals(asiento.getVinculo())) {
                return false;
            }
        } catch (AsientoException ignored) {
            // Sin cabecera: puede ser un mapa de un intento anterior sin movimientos.
        }
        return cuentaMovimientoService.findAllByAsiento(fecha, orden, 0, 2).stream()
                .allMatch(this::esLineaIngreso);
    }

    /**
     * Borra sólo las líneas del ingreso. Si el orden tuviera líneas de otro comprobante no se
     * borra nada: se aborta para no destruir un asiento ajeno.
     */
    private void borrarAsientoPropio(OffsetDateTime fecha, Integer orden) {
        List<CuentaMovimiento> movimientos = cuentaMovimientoService.findAllByAsiento(fecha, orden, 0, 2);
        boolean hayAjenos = movimientos.stream().anyMatch(movimiento -> !esLineaIngreso(movimiento));
        if (hayAjenos) {
            throw new AsientoIngresoException(Codigo.REGENERACION_FALLIDA,
                    "El orden " + orden + " de la fecha " + fecha.toLocalDate()
                            + " tiene líneas de otro comprobante; no se puede regenerar el ingreso");
        }
        if (!movimientos.isEmpty()) {
            cuentaMovimientoService.deleteAllByCuentaMovimientoIdIn(movimientos.stream()
                    .map(CuentaMovimiento::getCuentaMovimientoId)
                    .toList());
        }
        try {
            asientoService.deleteByAsientoId(asientoService.findByAsiento(fecha, orden).getAsientoId());
        } catch (AsientoException ignored) {
            // Sin cabecera: nada que borrar.
        }
    }

    private boolean esLineaIngreso(CuentaMovimiento movimiento) {
        return movimiento.getComprobanteId() != null && movimiento.getComprobanteId() == COMPROBANTE_INGRESO;
    }

    private PagoIngreso toPago(PagoDto pago) {
        return PagoIngreso.builder()
                .facultadId(pago.getFacultadId())
                .tipoChequeraId(pago.getTipoChequeraId())
                .chequeraSerieId(pago.getChequeraSerieId())
                .productoId(pago.getProductoId())
                .alternativaId(pago.getAlternativaId())
                .cuotaId(pago.getCuotaId())
                .orden(pago.getOrden())
                .fecha(diaContable(pago.getFecha()))
                .importe(pago.getImportePagado() == null ? BigDecimal.ZERO : pago.getImportePagado())
                .tipoPagoId(pago.getTipoPagoId())
                .chequeraPagoId(pago.getChequeraPagoId())
                .chequeraPagoReemplazoId(pago.getChequeraPagoReemplazoId())
                .build();
    }

    /**
     * El legacy guarda en {@code chequera_pago_asiento.fecha} el día contable
     * ({@code DateValue(pago.fecha)}), no el timestamp. Mercado Pago trae pagos con hora
     * (00:03:20) y, sin truncar, {@code getItems} —que filtra por medianoche exacta— no los ve.
     */
    private static OffsetDateTime diaContable(OffsetDateTime fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.toLocalDate().atStartOfDay().atOffset(fecha.getOffset());
    }

    private Integer lectivoId(PagoIngreso pago) {
        return getChequeraSerieByUniqueUseCase
                .getByUnique(pago.getFacultadId(), pago.getTipoChequeraId(), pago.getChequeraSerieId())
                .map(ChequeraSerie::getLectivoId)
                .map(lectivo -> lectivo == null ? 0 : lectivo)
                .orElseGet(() -> {
                    try {
                        Integer lectivo = chequeraSerieReemplazoService.findByUnique(
                                pago.getFacultadId(), pago.getTipoChequeraId(), pago.getChequeraSerieId()).getLectivoId();
                        return lectivo == null ? 0 : lectivo;
                    } catch (RuntimeException e) {
                        // Serie y reemplazo ausentes: el legacy sigue con lectivo 0 y lo informa como faltante.
                        return 0;
                    }
                });
    }

    private CuentaFaltante faltante(Integer facultadId, Integer lectivoId, Integer tipoChequeraId,
                                    Integer productoId) {
        var tipoChequera = catalogo.tipoChequera(tipoChequeraId);
        Integer geograficaId = tipoChequera == null ? null : tipoChequera.geograficaId();
        return CuentaFaltante.builder()
                .facultadId(facultadId)
                .facultadNombre(catalogo.facultadNombre(facultadId))
                .lectivoId(lectivoId)
                .lectivoNombre(catalogo.lectivoNombre(lectivoId))
                .tipoChequeraId(tipoChequeraId)
                .tipoChequeraNombre(tipoChequera == null ? null : tipoChequera.nombre())
                .productoId(productoId)
                .productoNombre(catalogo.productoNombre(productoId))
                .geograficaId(geograficaId)
                .geograficaNombre(catalogo.geograficaNombre(geograficaId))
                .build();
    }

    private static CuentaMovimiento movimiento(BigDecimal numeroCuenta, Byte debita, BigDecimal importe,
                                               String concepto) {
        return CuentaMovimiento.builder()
                .numeroCuenta(numeroCuenta)
                .debita(debita)
                .comprobanteId(COMPROBANTE_INGRESO)
                .concepto(concepto)
                .importe(importe == null ? BigDecimal.ZERO : importe)
                .build();
    }

}

package um.tesoreria.core.hexagonal.pagos.ordenPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.application.service.BancoMovimientoService;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model.BancoMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.service.EjercicioService;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.pagos.entrega.application.service.EntregaService;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.Entrega;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoValidacionException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.model.OrdenPagoPreparado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.service.OrdenPagoPreparador;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.*;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.CreateOrdenPagoUseCase;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.application.service.ProveedorPagoService;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.application.service.ProveedorValorService;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorMovimientoService;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorService;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.Valor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaByIdUseCase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateOrdenPagoUseCaseImpl implements CreateOrdenPagoUseCase {

    private final OrdenPagoPreparador preparador;
    private final ProveedorMovimientoService proveedorMovimientoService;
    private final AsientoService asientoService;
    private final CuentaMovimientoService cuentaMovimientoService;
    private final ReservarOrdenContableUseCase reservarOrdenContable;
    private final ValorMovimientoService valorMovimientoService;
    private final ProveedorValorService proveedorValorService;
    private final ProveedorPagoService proveedorPagoService;
    private final BancoMovimientoService bancoMovimientoService;
    private final ValorService valorService;
    private final GetBancariaByIdUseCase getBancariaByIdUseCase;
    private final EjercicioService ejercicioService;
    private final EntregaService entregaService;

    @Override
    @Transactional
    public OrdenPagoResultado create(OrdenPagoSolicitud solicitud) {
        OrdenPagoPreparado preparado = preparador.preparar(solicitud);
        if (!preparado.errores().isEmpty()) {
            throw new OrdenPagoValidacionException(preparado.errores());
        }

        OrdenPagoContexto contexto = preparado.contexto();
        OffsetDateTime fecha = normalizar(solicitud.fechaComprobante());
        OffsetDateTime fechaVencimiento = solicitud.fechaVencimiento() == null
                ? fecha
                : normalizar(solicitud.fechaVencimiento());
        BigDecimal importe = preparado.totalValores();
        String vinculo = contexto.conceptoPago()
                + " - " + preparado.proveedor().getRazonSocial()
                + " (" + preparado.proveedor().getCuit() + ")";

        ProveedorMovimiento ordenPago = proveedorMovimientoService.createProveedorMovimiento(ProveedorMovimiento.builder()
                .proveedorId(preparado.proveedor().getProveedorId())
                .nombreBeneficiario(preparado.proveedor().getRazonSocial())
                .comprobanteId(preparado.comprobante().getComprobanteId())
                .fechaComprobante(fecha)
                .fechaVencimiento(fechaVencimiento)
                .prefijo(preparado.prefijo())
                .numeroComprobante(preparado.numeroComprobante())
                .netoSinDescuento(importe)
                .descuento(BigDecimal.ZERO)
                .neto(importe)
                .importe(importe)
                .cancelado(BigDecimal.ZERO)
                .ordenContable(0)
                .concepto(solicitud.concepto())
                .conCargo(solicitud.conCargo())
                .solicitaFactura(solicitud.solicitaFactura())
                .geograficaId(solicitud.geograficaId())
                .build());
        Long proveedorMovimientoId = ordenPago.getProveedorMovimientoId();

        List<AsientoPropuesto> propuestos = AsientoPropuestoBuilder.buildOrdenPago(contexto, false);
        List<AsientoPropuesto> asientos = new ArrayList<>();
        List<Long> valorMovimientoIds = registrarValores(propuestos, solicitud, contexto, preparado,
                proveedorMovimientoId, vinculo, asientos);

        BigDecimal cancelado = registrarPagos(preparado, solicitud, fecha, proveedorMovimientoId);

        ordenPago.setCancelado(cancelado);
        proveedorMovimientoService.updateProveedorMovimiento(ordenPago, proveedorMovimientoId);

        completarAsientosPendientes(propuestos, preparado, proveedorMovimientoId, vinculo, asientos);

        return new OrdenPagoResultado(
                proveedorMovimientoId,
                preparado.prefijo(),
                preparado.numeroComprobante(),
                importe,
                cancelado,
                asientos,
                valorMovimientoIds);
    }

    private List<Long> registrarValores(List<AsientoPropuesto> propuestos, OrdenPagoSolicitud solicitud,
                                        OrdenPagoContexto contexto, OrdenPagoPreparado preparado,
                                        Long proveedorMovimientoId, String vinculo, List<AsientoPropuesto> asientos) {
        List<AsientoPropuesto> propuestosValor = propuestos.stream()
                .filter(asiento -> asiento.origenes().stream()
                        .anyMatch(origen -> origen.tipo() == OrigenAsiento.Tipo.VALOR))
                .toList();
        List<Long> valorMovimientoIds = new ArrayList<>();
        List<OrdenPagoValor> valores = solicitud.valores() == null ? List.of() : solicitud.valores();
        for (int i = 0; i < valores.size(); i++) {
            OrdenPagoValor valor = valores.get(i);
            AsientoPropuesto propuesto = propuestosValor.get(i);
            OffsetDateTime fechaValor = normalizar(propuesto.fechaContable());
            validarEjercicio(fechaValor);
            Integer orden = reservarOrdenContable.reservar(fechaValor);
            asientos.add(registrarAsiento(fechaValor, orden, vinculo, propuesto.lineas(),
                    propuesto.origenes(), proveedorMovimientoId));

            Valor tipoValor = valorService.getValorById(valor.valorId());
            ValorMovimiento valorMovimiento = valorMovimientoService.create(new ValorMovimiento(
                    null,
                    valor.valorId(),
                    fechaValor,
                    valor.numero() == null ? 0L : valor.numero(),
                    valor.bancariaIdOrigen(),
                    valor.fechaEfectivizacion(),
                    valor.importe().abs(),
                    "Emitido",
                    valor.nombreTitular() == null ? "" : valor.nombreTitular(),
                    valor.cuitTitular() == null ? "" : valor.cuitTitular(),
                    0L,
                    fechaValor,
                    orden,
                    valor.letras() == null ? "" : valor.letras()));
            valorMovimientoIds.add(valorMovimiento.valorMovimientoId());

            proveedorValorService.create(new ProveedorValor(
                    null,
                    proveedorMovimientoId,
                    i + 1,
                    valorMovimiento.valorMovimientoId()));

            if (valor.bancariaIdOrigen() != null) {
                Bancaria bancaria = getBancariaByIdUseCase.getBancariaById(valor.bancariaIdOrigen());
                bancoMovimientoService.create(new BancoMovimiento(
                        null,
                        bancaria.getBancariaId(),
                        fechaValor,
                        bancoMovimientoService.nextOrden(bancaria.getBancariaId(), fechaValor),
                        tipoBanco(tipoValor.categoria()),
                        String.valueOf(valor.numero() == null ? 0L : valor.numero()),
                        vinculo,
                        valor.importe().abs(),
                        bancaria.getNumeroCuenta(),
                        (byte) 0,
                        null,
                        (byte) 0,
                        fechaValor,
                        orden,
                        valorMovimiento.valorMovimientoId()));
            }
        }
        return valorMovimientoIds;
    }

    private BigDecimal registrarPagos(OrdenPagoPreparado preparado, OrdenPagoSolicitud solicitud,
                                      OffsetDateTime fecha, Long proveedorMovimientoId) {
        BigDecimal cancelado = BigDecimal.ZERO;
        List<OrdenPagoFactura> aplicados = solicitud.facturas() == null ? List.of() : solicitud.facturas();
        for (OrdenPagoFactura aplicado : aplicados) {
            if (aplicado.pago() == null || aplicado.pago().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            boolean tieneTraza = proveedorPagoService.existsTrazaContableByFactura(aplicado.proveedorMovimientoId());
            proveedorPagoService.create(new ProveedorPago(
                    null,
                    proveedorMovimientoId,
                    aplicado.proveedorMovimientoId(),
                    aplicado.pago(),
                    fecha,
                    tieneTraza ? (byte) 0 : (byte) 1));
            cancelado = cancelado.add(aplicado.pago());

            ProveedorMovimiento factura = preparado.facturas().stream()
                    .filter(candidata -> candidata.getProveedorMovimientoId().equals(aplicado.proveedorMovimientoId()))
                    .findFirst()
                    .orElse(null);
            if (factura != null) {
                factura.setCancelado(valorAbsoluto(factura.getCancelado()).add(aplicado.pago()));
                proveedorMovimientoService.updateProveedorMovimiento(factura, factura.getProveedorMovimientoId());
            }
        }
        return cancelado;
    }

    /**
     * Reconstrucción automática no destructiva: sólo crea el asiento de los hijos que no lo tienen.
     * Los hijos ya contabilizados (facturas y entregas con asiento propio) se dejan intactos, y la
     * orden de pago de proveedor no lleva asiento propio, igual que el alta legacy.
     */
    private void completarAsientosPendientes(List<AsientoPropuesto> propuestos, OrdenPagoPreparado preparado,
                                             Long proveedorMovimientoId, String vinculo,
                                             List<AsientoPropuesto> asientos) {
        for (AsientoPropuesto propuesto : propuestos) {
            if (propuesto.ordenContable() != null) {
                continue;
            }
            if (propuesto.origenes().stream().anyMatch(origen -> origen.tipo() == OrigenAsiento.Tipo.VALOR)) {
                continue;
            }
            OffsetDateTime fechaAsiento = normalizar(propuesto.fechaContable());
            validarEjercicio(fechaAsiento);
            Integer orden = reservarOrdenContable.reservar(fechaAsiento);
            Long proveedorMovimientoIdLinea = null;

            OrigenAsiento origen = propuesto.origenes().getFirst();
            if (origen.tipo() == OrigenAsiento.Tipo.FACTURA) {
                proveedorMovimientoIdLinea = origen.referenciaId();
                ProveedorMovimiento factura = preparado.facturas().stream()
                        .filter(candidata -> candidata.getProveedorMovimientoId().equals(origen.referenciaId()))
                        .findFirst()
                        .orElse(null);
                if (factura != null) {
                    factura.setFechaContable(fechaAsiento);
                    factura.setOrdenContable(orden);
                    proveedorMovimientoService.updateProveedorMovimiento(factura, factura.getProveedorMovimientoId());
                }
            } else if (origen.tipo() == OrigenAsiento.Tipo.ENTREGA) {
                Entrega entrega = entregaService.findById(origen.referenciaId());
                entregaService.updateAsiento(new Entrega(
                        entrega.entregaId(),
                        entrega.fecha(),
                        entrega.ubicacionId(),
                        entrega.recibe(),
                        entrega.observacion(),
                        fechaAsiento,
                        orden,
                        entrega.anulada(),
                        entrega.tipo(),
                        entrega.trackId()));
            }

            asientos.add(registrarAsiento(fechaAsiento, orden, vinculo, propuesto.lineas(),
                    propuesto.origenes(), proveedorMovimientoIdLinea));
        }
    }

    private AsientoPropuesto registrarAsiento(OffsetDateTime fecha, Integer orden, String vinculo,
                                              List<LineaPropuesta> lineas, List<OrigenAsiento> origenes,
                                              Long proveedorMovimientoId) {
        asientoService.add(new AsientoEntity(null, fecha, orden, vinculo, null, null, null, null));
        int item = 0;
        for (LineaPropuesta linea : lineas) {
            cuentaMovimientoService.createCuentaMovimiento(CuentaMovimiento.builder()
                    .fechaContable(fecha)
                    .ordenContable(orden)
                    .item(++item)
                    .numeroCuenta(linea.numeroCuenta())
                    .debita(linea.debita())
                    .importe(linea.importe())
                    .comprobanteId(linea.comprobanteId())
                    .proveedorId(linea.proveedorId())
                    .concepto(linea.concepto())
                    .proveedorMovimientoId(proveedorMovimientoId)
                    .numeroAnulado(0)
                    .version(0)
                    .apertura((byte) 0)
                    .build());
        }
        return new AsientoPropuesto(fecha, orden, origenes, lineas);
    }

    private void validarEjercicio(OffsetDateTime fecha) {
        LocalDate dia = fecha.toLocalDate();
        Ejercicio ejercicio = ejercicioService.findByFecha(dia).orElse(null);
        if (ejercicio == null) {
            throw new OrdenPagoValidacionException(List.of("ERROR: No hay Ejercicio para la Fecha " + dia));
        }
        if (ejercicio.getBloqueado() != null && ejercicio.getBloqueado() == 1) {
            throw new OrdenPagoValidacionException(List.of("ERROR: Ejercicio BLOQUEADO"));
        }
    }

    private String tipoBanco(String categoria) {
        if (categoria == null) {
            return null;
        }
        return "Transferencia".equalsIgnoreCase(categoria) ? "Transferencias" : categoria;
    }

    private BigDecimal valorAbsoluto(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor.abs();
    }

    private OffsetDateTime normalizar(OffsetDateTime fecha) {
        return fecha.withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDate()
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
    }
}

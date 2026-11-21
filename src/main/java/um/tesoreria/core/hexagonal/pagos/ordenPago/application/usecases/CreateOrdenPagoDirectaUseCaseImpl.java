package um.tesoreria.core.hexagonal.pagos.ordenPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaByIdUseCase;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.application.service.BancoMovimientoService;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model.BancoMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.service.EjercicioService;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoValidacionException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.model.OrdenPagoDirectaPreparado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.service.OrdenPagoDirectaPreparador;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.AsientoPropuesto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.AsientoPropuestoBuilder;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.LineaPropuesta;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaContexto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoResultado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoValor;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrigenAsiento;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.CreateOrdenPagoDirectaUseCase;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.application.service.ProveedorValorService;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorMovimientoService;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorService;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.Valor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Alta de una orden de pago directa. Replica {@code clsCtlCompras.saveDirecto}: reserva el orden
 * del encabezado en la fecha del comprobante (a diferencia de la orden de pago a proveedor, que no
 * tiene asiento propio), registra los gastos contra la cuenta de obligaciones y genera un asiento
 * por cada valor con su propia fecha de emisión.
 */
@Component
@RequiredArgsConstructor
public class CreateOrdenPagoDirectaUseCaseImpl implements CreateOrdenPagoDirectaUseCase {

    private static final String VINCULO_ENCABEZADO = "Asiento OP Directa";

    private final OrdenPagoDirectaPreparador preparador;
    private final ProveedorMovimientoService proveedorMovimientoService;
    private final AsientoService asientoService;
    private final CuentaMovimientoService cuentaMovimientoService;
    private final ReservarOrdenContableUseCase reservarOrdenContable;
    private final ValorMovimientoService valorMovimientoService;
    private final ProveedorValorService proveedorValorService;
    private final BancoMovimientoService bancoMovimientoService;
    private final ValorService valorService;
    private final GetBancariaByIdUseCase getBancariaByIdUseCase;
    private final EjercicioService ejercicioService;

    @Override
    @Transactional
    public OrdenPagoResultado createDirecta(OrdenPagoDirectaSolicitud solicitud) {
        OrdenPagoDirectaPreparado preparado = preparador.preparar(solicitud);
        if (!preparado.errores().isEmpty()) {
            throw new OrdenPagoValidacionException(preparado.errores());
        }

        OffsetDateTime fecha = normalizar(solicitud.fechaComprobante());
        OffsetDateTime fechaVencimiento = solicitud.fechaVencimiento() == null
                ? fecha
                : normalizar(solicitud.fechaVencimiento());
        BigDecimal importe = preparado.totalGastos();
        int ordenEncabezado = reservarOrdenContable.reservar(fecha);

        ProveedorMovimiento ordenPago = proveedorMovimientoService.createProveedorMovimiento(ProveedorMovimiento.builder()
                .nombreBeneficiario(solicitud.nombreBeneficiario())
                .comprobanteId(preparado.comprobante().getComprobanteId())
                .fechaComprobante(fecha)
                .fechaVencimiento(fechaVencimiento)
                .prefijo(preparado.prefijo())
                .numeroComprobante(preparado.numeroComprobante())
                .netoSinDescuento(importe)
                .descuento(BigDecimal.ZERO)
                .neto(importe)
                .importe(importe)
                .cancelado(importe)
                .fechaContable(fecha)
                .ordenContable(ordenEncabezado)
                .concepto(solicitud.concepto())
                .conCargo(solicitud.conCargo())
                .solicitaFactura(solicitud.solicitaFactura())
                .geograficaId(solicitud.geograficaId())
                .build());
        Long proveedorMovimientoId = ordenPago.getProveedorMovimientoId();

        OrdenPagoDirectaContexto contexto = preparado.contexto()
                .conEncabezado(fecha, ordenEncabezado, proveedorMovimientoId);
        List<AsientoPropuesto> propuestos = AsientoPropuestoBuilder.buildDirecta(contexto, false);

        List<AsientoPropuesto> asientos = new ArrayList<>();
        propuestos.stream()
                .filter(this::esEncabezado)
                .findFirst()
                .ifPresent(propuesto -> asientos.add(registrarAsiento(fecha, ordenEncabezado, VINCULO_ENCABEZADO,
                        propuesto.lineas(), propuesto.origenes(), proveedorMovimientoId)));

        List<Long> valorMovimientoIds = registrarValores(contexto, solicitud, propuestos, proveedorMovimientoId, asientos);

        return new OrdenPagoResultado(proveedorMovimientoId, preparado.prefijo(),
                preparado.numeroComprobante(), importe, importe, asientos, valorMovimientoIds);
    }

    private boolean esEncabezado(AsientoPropuesto propuesto) {
        return propuesto.origenes().stream().anyMatch(origen -> origen.tipo() == OrigenAsiento.Tipo.ENCABEZADO);
    }

    private List<Long> registrarValores(OrdenPagoDirectaContexto contexto, OrdenPagoDirectaSolicitud solicitud,
                                        List<AsientoPropuesto> propuestos, Long proveedorMovimientoId,
                                        List<AsientoPropuesto> asientos) {
        List<AsientoPropuesto> deValores = propuestos.stream().filter(propuesto -> !esEncabezado(propuesto)).toList();
        List<OrdenPagoValor> valores = solicitud.valores() == null ? List.of() : solicitud.valores();
        List<Long> valorMovimientoIds = new ArrayList<>();
        for (int i = 0; i < valores.size(); i++) {
            OrdenPagoValor valor = valores.get(i);
            Optional<AsientoPropuesto> propuesto = i < deValores.size() ? Optional.of(deValores.get(i)) : Optional.empty();
            OffsetDateTime fechaValor = propuesto
                    .map(asiento -> normalizar(asiento.fechaContable()))
                    .orElseGet(() -> normalizar(valor.fechaEmision()));
            validarEjercicio(fechaValor);
            int orden = reservarOrdenContable.reservar(fechaValor);
            List<LineaPropuesta> lineas = propuesto.map(AsientoPropuesto::lineas).orElseGet(List::of);
            asientos.add(registrarAsiento(fechaValor, orden, contexto.conceptoPago(), lineas,
                    List.of(new OrigenAsiento(OrigenAsiento.Tipo.VALOR, (long) i)), proveedorMovimientoId));

            Valor tipoValor = valorService.getValorById(valor.valorId());
            ValorMovimiento valorMovimiento = valorMovimientoService.create(new ValorMovimiento(
                    null, valor.valorId(), fechaValor,
                    valor.numero() == null ? 0L : valor.numero(),
                    valor.bancariaIdOrigen(), valor.fechaEfectivizacion(),
                    valorAbsoluto(valor.importe()), "Emitido",
                    texto(valor.nombreTitular()), texto(valor.cuitTitular()),
                    0L, fechaValor, orden, texto(valor.letras())));
            valorMovimientoIds.add(valorMovimiento.valorMovimientoId());

            proveedorValorService.create(new ProveedorValor(null, proveedorMovimientoId, i + 1,
                    valorMovimiento.valorMovimientoId()));

            if (valor.bancariaIdOrigen() != null) {
                Bancaria bancaria = getBancariaByIdUseCase.getBancariaById(valor.bancariaIdOrigen());
                bancoMovimientoService.create(new BancoMovimiento(null, valor.bancariaIdOrigen(), fechaValor,
                        bancoMovimientoService.nextOrden(valor.bancariaIdOrigen(), fechaValor),
                        tipoBanco(tipoValor.categoria()),
                        String.valueOf(valor.numero() == null ? 0L : valor.numero()),
                        contexto.conceptoPago(), valorAbsoluto(valor.importe()), bancaria.getNumeroCuenta(),
                        (byte) 0, null, (byte) 0, fechaValor, orden, valorMovimiento.valorMovimientoId()));
            }
        }
        return valorMovimientoIds;
    }

    private AsientoPropuesto registrarAsiento(OffsetDateTime fecha, Integer orden, String vinculo,
                                              List<LineaPropuesta> lineas, List<OrigenAsiento> origenes,
                                              Long proveedorMovimientoId) {
        asientoService.add(new AsientoEntity(null, fecha, orden, vinculo, null, null, null, null));
        int item = 0;
        for (LineaPropuesta linea : lineas) {
            item++;
            cuentaMovimientoService.createCuentaMovimiento(CuentaMovimiento.builder()
                    .fechaContable(fecha)
                    .ordenContable(orden)
                    .item(item)
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
        var dia = fecha.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate();
        Optional<Ejercicio> ejercicio = ejercicioService.findByFecha(dia);
        if (ejercicio.isEmpty()) {
            throw new OrdenPagoValidacionException(List.of("ERROR: No hay Ejercicio para la Fecha " + dia));
        }
        if (ejercicio.get().getBloqueado() != null && ejercicio.get().getBloqueado() == 1) {
            throw new OrdenPagoValidacionException(List.of("ERROR: Ejercicio BLOQUEADO"));
        }
    }

    private String tipoBanco(String categoria) {
        if (categoria == null) {
            return null;
        }
        return "Transferencia".equalsIgnoreCase(categoria) ? "Transferencias" : categoria;
    }

    private BigDecimal valorAbsoluto(BigDecimal importe) {
        return importe == null ? BigDecimal.ZERO : importe.abs();
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private OffsetDateTime normalizar(OffsetDateTime fecha) {
        return fecha.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}

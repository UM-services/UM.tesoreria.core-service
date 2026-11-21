package um.tesoreria.core.hexagonal.pagos.ordenPago.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.compras.articulo.application.service.ArticuloService;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.application.service.ProveedorArticuloService;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.model.ProveedorArticulo;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaByIdUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.service.EjercicioService;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.pagos.entrega.application.service.EntregaService;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.Entrega;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.EntregaAsignacion;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.EntregaDetalle;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoValidacionException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.model.OrdenPagoPreparado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.*;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorService;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.Valor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service.ValorMovimientoService;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.application.service.ProveedorPagoService;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.application.service.ProveedorValorService;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.service.UbicacionArticuloService;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Resuelve una solicitud de orden de pago contra la base y arma el contexto del motor de
 * asientos. No escribe nada: lo usan el preview, el alta y la reconstrucción de traza.
 */
@Component
@RequiredArgsConstructor
public class OrdenPagoPreparador {

    private static final BigDecimal CUENTA_OBLIGACION = new BigDecimal("20101090099");

    private final ProveedorMovimientoService proveedorMovimientoService;
    private final ProveedorService proveedorService;
    private final ComprobanteService comprobanteService;
    private final ProveedorArticuloService proveedorArticuloService;
    private final ArticuloService articuloService;
    private final EntregaService entregaService;
    private final UbicacionArticuloService ubicacionArticuloService;
    private final ValorService valorService;
    private final GetBancariaByIdUseCase getBancariaByIdUseCase;
    private final EjercicioService ejercicioService;
    private final ProveedorPagoService proveedorPagoService;
    private final ProveedorValorService proveedorValorService;
    private final ValorMovimientoService valorMovimientoService;

    public OrdenPagoPreparado preparar(OrdenPagoSolicitud solicitud) {
        Proveedor proveedor = cargarProveedor(solicitud);
        Comprobante comprobante = cargarComprobante(solicitud);
        OffsetDateTime fecha = normalizar(solicitud.fechaComprobante());
        if (fecha == null) {
            throw new OrdenPagoValidacionException(List.of("ERROR: Fecha NO Válida"));
        }
        Ejercicio ejercicio = ejercicioService.findByFecha(fecha.toLocalDate())
                .orElseThrow(() -> new OrdenPagoValidacionException(
                        List.of("ERROR: No hay Ejercicio para la Fecha " + fecha.toLocalDate())));
        if (ejercicio.getBloqueado() != null && ejercicio.getBloqueado() == 1) {
            throw new OrdenPagoValidacionException(List.of("ERROR: Ejercicio BLOQUEADO"));
        }

        List<String> errores = new ArrayList<>();
        if (proveedor.getNumeroCuenta() == null || proveedor.getNumeroCuenta().signum() == 0) {
            errores.add("ERROR: Proveedor SIN Cuenta Contable");
        }

        List<OrdenPagoFactura> facturasSolicitud = solicitud.facturas() == null ? List.of() : solicitud.facturas();
        List<ProveedorMovimiento> facturas = cargarFacturas(facturasSolicitud, proveedor, errores);
        BigDecimal totalFacturas = totalFacturas(facturasSolicitud);
        BigDecimal totalValores = totalValores(solicitud.valores());
        BigDecimal importe = solicitud.importe() == null ? totalValores : solicitud.importe();
        if (importe.compareTo(totalValores) != 0) {
            errores.add("ERROR: Diferencia entre VALORES y TOTAL");
        }

        Integer prefijo = ejercicio.getEjercicioId();
        Long numero = resolverNumero(solicitud, comprobante, prefijo, errores);
        if (numero == null) {
            errores.add("ERROR: Número NO Válido");
        }

        List<ContextoFactura> contextoFacturas = contextoFacturas(facturas, proveedor, errores);
        List<ContextoEntrega> contextoEntregas = contextoEntregas(facturas, errores);
        List<ContextoValor> contextoValores = contextoValores(solicitud.valores(), errores);

        OrdenPagoContexto contexto = new OrdenPagoContexto(proveedor.getProveedorId(), comprobante.getComprobanteId(),
                comprobante.getDebita(), proveedor.getNumeroCuenta(), prefijo, numero, fecha, null,
                contextoFacturas, contextoEntregas, contextoValores);

        return new OrdenPagoPreparado(proveedor, comprobante, ejercicio, facturas, facturasSolicitud, contexto,
                totalFacturas, totalValores, prefijo, numero, errores);
    }

    /**
     * Contexto de una orden de pago ya registrada, reconstruido desde la base. Lo usa la
     * reconstrucción de traza contable: a diferencia del alta, conserva la fecha y el orden
     * de asiento que cada hijo ya tiene.
     */
    public OrdenPagoContexto contextoDe(ProveedorMovimiento ordenPago) {
        List<String> errores = new ArrayList<>();
        Proveedor proveedor = proveedorService.getByProveedorId(ordenPago.getProveedorId())
                .orElseThrow(() -> new OrdenPagoValidacionException(List.of("ERROR: Proveedor NO Existe")));
        Comprobante comprobante = comprobanteService.findByComprobanteId(ordenPago.getComprobanteId());
        List<ProveedorPago> pagos = proveedorPagoService
                .findAllByProveedorMovimientoIdPago(ordenPago.getProveedorMovimientoId());
        List<Long> facturaIds = pagos.stream().map(ProveedorPago::proveedorMovimientoIdFactura).toList();
        List<ProveedorMovimiento> facturas = facturaIds.isEmpty()
                ? List.of()
                : proveedorMovimientoService.findAllByProveedorMovimientoIdIn(facturaIds);
        return new OrdenPagoContexto(ordenPago.getProveedorId(), comprobante.getComprobanteId(),
                comprobante.getDebita(), proveedor.getNumeroCuenta(), ordenPago.getPrefijo(),
                ordenPago.getNumeroComprobante(), ordenPago.getFechaComprobante(),
                ordenPago.getProveedorMovimientoId(), contextoFacturas(facturas, proveedor, errores),
                contextoEntregas(facturas, errores),
                contextoValoresDeBase(valoresDe(ordenPago.getProveedorMovimientoId()), errores));
    }

    /**
     * Contexto de una orden de pago directa (sin proveedor): lo usa la reconstrucción de traza
     * sobre datos ya guardados, tomando los gastos de las líneas al debe de su propio asiento.
     */
    public OrdenPagoDirectaContexto contextoDirectaDe(ProveedorMovimiento ordenPago, List<ContextoGasto> gastos) {
        Comprobante comprobante = comprobanteService.findByComprobanteId(ordenPago.getComprobanteId());
        List<String> errores = new ArrayList<>();
        List<ContextoValor> valores = contextoValoresDeBase(
                valoresDe(ordenPago.getProveedorMovimientoId()), errores);
        return new OrdenPagoDirectaContexto(comprobante.getComprobanteId(), comprobante.getDebita(),
                ordenPago.getPrefijo(), ordenPago.getNumeroComprobante(), ordenPago.getFechaComprobante(),
                ordenPago.getFechaContable(), ordenPago.getOrdenContable(),
                ordenPago.getProveedorMovimientoId(), CUENTA_OBLIGACION,
                valorAbsoluto(ordenPago.getImporte()), gastos, valores);
    }

    private List<ValorMovimiento> valoresDe(Long proveedorMovimientoId) {
        List<Long> ids = proveedorValorService.findAllByProveedorMovimientoId(proveedorMovimientoId).stream()
                .map(ProveedorValor::valorMovimientoId)
                .toList();
        return ids.isEmpty() ? List.of() : valorMovimientoService.findAllByIdIn(ids);
    }

    private List<ContextoValor> contextoValoresDeBase(List<ValorMovimiento> valores, List<String> errores) {
        List<ContextoValor> contexto = new ArrayList<>();
        int posicion = 0;
        for (ValorMovimiento valor : valores) {
            OffsetDateTime fechaEmision = normalizar(valor.fechaEmision());
            if (fechaEmision == null) {
                errores.add("ERROR: Valor SIN Fecha de Emisión");
                continue;
            }
            if (valor.importe() == null || valor.importe().signum() == 0) {
                errores.add("ERROR: Importe de Valor NO Válido");
                continue;
            }
            Valor tipo = valorService.getValorById(valor.valorId());
            BigDecimal contrapartida = tipo.numeroCuenta();
            if (valor.bancariaIdOrigen() != null) {
                Bancaria bancaria = getBancariaByIdUseCase.getBancariaById(valor.bancariaIdOrigen());
                if (bancaria.getNumeroCuenta() != null && bancaria.getNumeroCuenta().signum() > 0) {
                    contrapartida = bancaria.getNumeroCuenta();
                }
            }
            if (contrapartida == null || contrapartida.signum() == 0) {
                errores.add("ERROR: Valor SIN Cuenta Contable (" + tipo.concepto() + ")");
                continue;
            }
            contexto.add(new ContextoValor(valor.valorMovimientoId(), valor.valorId(), valor.importe(),
                    fechaEmision, valor.fechaContable(), valor.ordenContable(), contrapartida, posicion++));
        }
        return contexto;
    }

    private Proveedor cargarProveedor(OrdenPagoSolicitud solicitud) {
        if (solicitud.proveedorId() == null) {
            throw new OrdenPagoValidacionException(List.of("ERROR: Proveedor NO Seleccionado"));
        }
        return proveedorService.getByProveedorId(solicitud.proveedorId())
                .orElseThrow(() -> new OrdenPagoValidacionException(
                        List.of("ERROR: Proveedor NO Existe")));
    }

    private Comprobante cargarComprobante(OrdenPagoSolicitud solicitud) {
        if (solicitud.comprobanteId() == null) {
            throw new OrdenPagoValidacionException(List.of("ERROR: Comprobante NO Seleccionado"));
        }
        return comprobanteService.findByComprobanteId(solicitud.comprobanteId());
    }

    private List<ProveedorMovimiento> cargarFacturas(List<OrdenPagoFactura> facturasSolicitud, Proveedor proveedor,
                                                     List<String> errores) {
        List<Long> ids = facturasSolicitud.stream()
                .filter(factura -> factura.proveedorMovimientoId() != null)
                .map(OrdenPagoFactura::proveedorMovimientoId)
                .toList();
        if (ids.isEmpty()) {
            return List.of();
        }
        Map<Long, ProveedorMovimiento> porId = proveedorMovimientoService.findAllByProveedorMovimientoIdIn(ids).stream()
                .collect(Collectors.toMap(ProveedorMovimiento::getProveedorMovimientoId, factura -> factura));
        List<ProveedorMovimiento> facturas = new ArrayList<>();
        for (OrdenPagoFactura solicitud : facturasSolicitud) {
            ProveedorMovimiento factura = porId.get(solicitud.proveedorMovimientoId());
            if (factura == null) {
                errores.add("ERROR: Comprobante NO Existe (" + solicitud.proveedorMovimientoId() + ")");
                continue;
            }
            if (!Objects.equals(factura.getProveedorId(), proveedor.getProveedorId())) {
                errores.add("ERROR: Comprobante de OTRO Proveedor (" + factura.getPrefijo() + "-"
                        + factura.getNumeroComprobante() + ")");
                continue;
            }
            if (factura.getFechaAnulacion() != null) {
                errores.add("ERROR: Comprobante ANULADO (" + factura.getPrefijo() + "-"
                        + factura.getNumeroComprobante() + ")");
                continue;
            }
            BigDecimal pago = valorAbsoluto(solicitud.pago());
            if (pago.signum() != 0 && valorAbsoluto(factura.getCancelado()).add(pago)
                    .compareTo(valorAbsoluto(factura.getImporte())) > 0) {
                errores.add("ERROR: Comprobante " + factura.getPrefijo() + "-" + factura.getNumeroComprobante()
                        + " SIN Saldo SUFICIENTE");
            }
            facturas.add(factura);
        }
        return facturas;
    }

    private Long resolverNumero(OrdenPagoSolicitud solicitud, Comprobante comprobante, Integer prefijo,
                                List<String> errores) {
        Long manual = solicitud.numeroManual();
        if (manual != null && manual > 0) {
            if (proveedorMovimientoService.existsByOrdenPago(prefijo, manual)) {
                errores.add("ERROR: OP Existente");
            }
            return manual;
        }
        if (comprobante.getOrdenPago() != null && comprobante.getOrdenPago() != 0) {
            return proveedorMovimientoService.findLastOrdenPagoOptional(prefijo)
                    .map(ultima -> ultima.getNumeroComprobante() == null ? 1L : ultima.getNumeroComprobante() + 1)
                    .orElse(1L);
        }
        if (comprobante.getAplicaPendiente() != null && comprobante.getAplicaPendiente() != 0) {
            return proveedorMovimientoService.findLastByComprobante(prefijo, comprobante.getComprobanteId())
                    .map(ultima -> ultima.getNumeroComprobante() == null ? 1L : ultima.getNumeroComprobante() + 1)
                    .orElse(1L);
        }
        return 0L;
    }

    private List<ContextoFactura> contextoFacturas(List<ProveedorMovimiento> facturas, Proveedor proveedor,
                                                   List<String> errores) {
        List<ContextoFactura> contexto = new ArrayList<>();
        for (ProveedorMovimiento factura : facturas) {
            Comprobante comprobante = comprobanteService.findByComprobanteId(factura.getComprobanteId());
            List<ContextoArticulo> articulos = new ArrayList<>();
            for (ProveedorArticulo proveedorArticulo : proveedorArticuloService
                    .findAllByProveedorMovimientoId(factura.getProveedorMovimientoId())) {
                Optional<Articulo> articulo = articuloService.getArticuloById(proveedorArticulo.articuloId());
                if (articulo.isEmpty() || articulo.get().getNumeroCuenta() == null) {
                    errores.add("ERROR: Artículo SIN Cuenta Contable ("
                            + proveedorArticulo.articuloId() + ")");
                    continue;
                }
                articulos.add(new ContextoArticulo(articulo.get().getNumeroCuenta(), proveedorArticulo.precioFinal()));
            }
            contexto.add(new ContextoFactura(factura.getProveedorMovimientoId(), factura.getComprobanteId(),
                    comprobante.getDebita(), factura.getProveedorId(), factura.getImporte(),
                    proveedor.getNumeroCuenta(), factura.getFechaContable(), factura.getOrdenContable(), articulos));
        }
        return contexto;
    }

    private List<ContextoEntrega> contextoEntregas(List<ProveedorMovimiento> facturas, List<String> errores) {
        List<Long> facturaIds = facturas.stream().map(ProveedorMovimiento::getProveedorMovimientoId).toList();
        if (facturaIds.isEmpty()) {
            return List.of();
        }
        Map<Long, ProveedorMovimiento> porId = facturas.stream()
                .collect(Collectors.toMap(ProveedorMovimiento::getProveedorMovimientoId, factura -> factura));
        Map<Integer, Byte> debitaPorComprobante = new LinkedHashMap<>();
        Map<Long, List<EntregaAsignacion>> porEntrega = entregaService.findAsignacionesByProveedorMovimientoId(facturaIds)
                .stream()
                .filter(asignacion -> asignacion.detalle() != null && asignacion.entrega() != null)
                .collect(Collectors.groupingBy(asignacion -> asignacion.entrega().entregaId(), LinkedHashMap::new,
                        Collectors.toList()));
        List<ContextoEntrega> contexto = new ArrayList<>();
        porEntrega.forEach((entregaId, asignaciones) -> {
            Entrega entrega = asignaciones.get(0).entrega();
            List<ContextoEntregaDetalle> detalles = new ArrayList<>();
            Integer proveedorId = null;
            Byte debita = 0;
            for (EntregaAsignacion asignacion : asignaciones) {
                EntregaDetalle detalle = asignacion.detalle();
                ProveedorMovimiento factura = porId.get(detalle.proveedorMovimientoId());
                if (factura == null) {
                    continue;
                }
                proveedorId = factura.getProveedorId();
                debita = debitaPorComprobante.computeIfAbsent(factura.getComprobanteId(),
                        comprobanteId -> comprobanteService.findByComprobanteId(comprobanteId).getDebita());
                Optional<UbicacionArticulo> ubicacionArticulo = ubicacionArticuloService
                        .getByUbicacionAndArticulo(entrega.ubicacionId(), detalle.articuloId());
                Optional<Articulo> articulo = articuloService.getArticuloById(detalle.articuloId());
                if (articulo.isEmpty() || articulo.get().getNumeroCuenta() == null) {
                    errores.add("ERROR: Artículo SIN Cuenta Contable (" + detalle.articuloId() + ")");
                    continue;
                }
                BigDecimal cuentaUbicacion = ubicacionArticulo.map(UbicacionArticulo::getNumeroCuenta).orElse(null);
                String nombreUbicacion = ubicacionArticulo.map(UbicacionArticulo::getUbicacion)
                        .map(ubicacion -> ubicacion.getNombre()).orElse("");
                detalles.add(new ContextoEntregaDetalle(entrega.ubicacionId(), detalle.articuloId(),
                        detalle.cantidad(), articulo.get().getNumeroCuenta(), cuentaUbicacion,
                        nombreUbicacion, articulo.get().getNombre()));
            }
            if (!detalles.isEmpty()) {
                contexto.add(new ContextoEntrega(entregaId, null, debita, proveedorId, entrega.fechaContable(),
                        entrega.ordenContable(), detalles));
            }
        });
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

    private BigDecimal totalFacturas(List<OrdenPagoFactura> facturas) {
        return facturas.stream()
                .map(factura -> valorAbsoluto(factura.pago()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal totalValores(List<OrdenPagoValor> valores) {
        if (valores == null) {
            return BigDecimal.ZERO;
        }
        return valores.stream()
                .map(valor -> valorAbsoluto(valor.importe()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal valorAbsoluto(BigDecimal importe) {
        return importe == null ? BigDecimal.ZERO : importe.abs();
    }

    private static OffsetDateTime normalizar(OffsetDateTime fecha) {
        return fecha == null ? null
                : fecha.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}

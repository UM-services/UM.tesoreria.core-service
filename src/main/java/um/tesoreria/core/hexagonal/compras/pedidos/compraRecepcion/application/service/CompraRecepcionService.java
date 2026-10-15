package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.service.CompraFormalizacionService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.service.CompraPedidoService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.exception.CompraRecepcionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPatrimonio;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPendienteItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionResultado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.in.CreateCompraRecepcionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.in.GetCompraRecepcionByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.in.ListCompraRecepcionesUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionPedidoPort;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionSecuenciaPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Fachada de la recepción y conformidad (etapa 9).
 *
 * <p>Composición cross-slice en la capa de aplicación (patrón pautado del subdominio
 * {@code pedidos}): la entrega se valida contra el detalle de la Orden de Compra
 * ({@link CompraFormalizacionService}), el saldo se deriva de las entregas ya registradas y el
 * avance de los pedidos cubiertos se dispara con {@link CompraPedidoService}
 * ({@code RECEPCION_PARCIAL} / {@code RECEPCIONADO}).</p>
 */
@Service
@RequiredArgsConstructor
public class CompraRecepcionService {

    private final CreateCompraRecepcionUseCase createCompraRecepcionUseCase;
    private final GetCompraRecepcionByIdUseCase getCompraRecepcionByIdUseCase;
    private final ListCompraRecepcionesUseCase listCompraRecepcionesUseCase;
    private final CompraRecepcionRepository compraRecepcionRepository;
    private final CompraRecepcionSecuenciaPort compraRecepcionSecuenciaPort;
    private final CompraRecepcionPedidoPort compraRecepcionPedidoPort;
    // Excepción cross-slice autorizada: composición de slices del subdominio "pedidos".
    private final CompraFormalizacionService compraFormalizacionService;
    private final CompraPedidoService compraPedidoService;

    public Optional<CompraRecepcion> getById(Long compraRecepcionId) {
        return getCompraRecepcionByIdUseCase.getById(compraRecepcionId);
    }

    public List<CompraRecepcion> listar(CompraRecepcionCriteria criteria) {
        return listCompraRecepcionesUseCase.listar(criteria);
    }

    /**
     * Bandeja de recepción: Órdenes de Compra del ejercicio con recepción pendiente. Una OC deja
     * de listarse cuando todos sus pedidos quedaron {@code RECEPCIONADO} — por completar las
     * cantidades o por el cierre manual con motivo (D-REC-8).
     */
    public List<CompraRecepcionPendiente> pendientes(Integer ejercicioId) {
        return compraFormalizacionService.listar(new CompraFormalizacionCriteria(ejercicioId, null, null, null))
                .stream()
                .filter(oc -> !cerrada(oc))
                .map(oc -> resumen(oc, compraRecepcionRepository.findByCompraFormalizacionId(oc.getCompraFormalizacionId())))
                .toList();
    }

    /** Recepciones y saldos de una Orden de Compra. */
    public Optional<CompraRecepcionPendiente> porFormalizacion(Long compraFormalizacionId) {
        return compraFormalizacionService.getById(compraFormalizacionId)
                .map(oc -> resumen(oc, compraRecepcionRepository.findByCompraFormalizacionId(compraFormalizacionId)));
    }

    /**
     * Registra una entrega: valida ítems y cantidades contra el saldo de la OC (D-REC-13), reserva
     * el número {@code RC-AAAA-NNNNNN}, deriva el resultado general (D-REC-2) y avanza los pedidos
     * cubiertos (parcial o total).
     */
    @Transactional
    public CompraRecepcion registrar(Long compraFormalizacionId, CompraRecepcion datos, Integer usuarioId) {
        CompraFormalizacion oc = compraFormalizacionService.getById(compraFormalizacionId)
                .orElseThrow(() -> new CompraRecepcionException(
                        "No existe la Orden de Compra " + compraFormalizacionId));
        List<CompraRecepcionItem> items = datos == null || datos.getItems() == null
                ? List.of() : new ArrayList<>(datos.getItems());
        validarItems(oc, items);

        LocalDateTime ahora = LocalDateTime.now();
        int anio = (oc.getFecha() != null ? oc.getFecha() : ahora).getYear();
        int correlativo = compraRecepcionSecuenciaPort.reservarNumero(oc.getEjercicioId(), anio);

        CompraRecepcion aCrear = CompraRecepcion.builder()
                .compraFormalizacionId(oc.getCompraFormalizacionId())
                .numero(String.format("RC-%d-%06d", anio, correlativo))
                .ejercicioId(oc.getEjercicioId())
                .fecha(datos.getFecha() != null ? datos.getFecha() : ahora)
                .remito(datos.getRemito())
                .resultado(CompraRecepcionResultado.masGrave(items.stream()
                        .map(CompraRecepcionItem::getResultado).toList()))
                .patrimonio(datos.getPatrimonio() != null
                        ? datos.getPatrimonio() : CompraRecepcionPatrimonio.NO_CORRESPONDE)
                .observacion(datos.getObservacion())
                .usuarioId(usuarioId)
                .items(items)
                .build();

        CompraRecepcion creada = createCompraRecepcionUseCase.crear(aCrear);
        actualizarPedidos(oc, usuarioId);
        return creada;
    }

    /**
     * Cierre manual de la recepción de una OC con saldo abierto (D-REC-8): exige motivo y avanza
     * los pedidos cubiertos a {@code RECEPCIONADO}, asentando el motivo en el historial del pedido.
     */
    @Transactional
    public void cerrar(Long compraFormalizacionId, String motivo, Integer usuarioId) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El cierre manual de la recepción requiere un motivo");
        }
        CompraFormalizacion oc = compraFormalizacionService.getById(compraFormalizacionId)
                .orElseThrow(() -> new CompraRecepcionException(
                        "No existe la Orden de Compra " + compraFormalizacionId));
        List<Integer> pedidoIds = pedidosDe(oc);
        if (pedidoIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "La Orden de Compra " + compraFormalizacionId + " no tiene pedidos asociados");
        }
        for (Integer pedidoId : pedidoIds) {
            compraPedidoService.marcarRecepcionado(pedidoId, usuarioId, "Cierre manual de recepción: " + motivo);
        }
    }

    // ---- Interno ----

    private void validarItems(CompraFormalizacion oc, List<CompraRecepcionItem> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("La recepción requiere al menos un ítem");
        }
        Map<Long, CompraFormalizacionItem> ocItems = oc.getItems() == null ? Map.of()
                : oc.getItems().stream()
                        .filter(item -> item.getCompraFormalizacionItemId() != null)
                        .collect(Collectors.toMap(CompraFormalizacionItem::getCompraFormalizacionItemId,
                                Function.identity(), (a, b) -> a));
        Map<Long, BigDecimal> cubierto = CalculadoraSaldosRecepcion.cubiertoPorItem(
                compraRecepcionRepository.findByCompraFormalizacionId(oc.getCompraFormalizacionId()));
        Set<Long> vistos = new HashSet<>();
        int orden = 1;
        for (CompraRecepcionItem item : items) {
            Long itemId = item.getCompraFormalizacionItemId();
            CompraFormalizacionItem ocItem = itemId == null ? null : ocItems.get(itemId);
            if (ocItem == null) {
                throw new IllegalArgumentException(
                        "El ítem " + itemId + " no pertenece a la Orden de Compra " + oc.getCompraFormalizacionId());
            }
            if (!vistos.add(itemId)) {
                throw new IllegalArgumentException("El ítem " + itemId + " está repetido en la recepción");
            }
            BigDecimal cantidad = item.getCantidadRecibida();
            if (cantidad == null || cantidad.signum() < 0) {
                throw new IllegalArgumentException(
                        "La cantidad recibida del ítem " + itemId + " debe ser mayor o igual a cero");
            }
            BigDecimal saldo = CalculadoraSaldosRecepcion.saldo(ocItem.getCantidad(), cubierto.get(itemId));
            if (cantidad.compareTo(saldo) > 0) {
                throw new IllegalArgumentException("La cantidad recibida (" + cantidad
                        + ") supera el saldo pendiente (" + saldo + ") del ítem " + itemId);
            }
            if (item.getResultado() == null) {
                item.setResultado(CompraRecepcionResultado.CONFORME);
            }
            if (item.getOrden() == null) {
                item.setOrden(orden);
            }
            orden++;
        }
        boolean alguna = items.stream()
                .anyMatch(item -> item.getCantidadRecibida() != null && item.getCantidadRecibida().signum() > 0);
        if (!alguna) {
            throw new IllegalArgumentException("La recepción requiere al menos una cantidad mayor a cero");
        }
    }

    /** Avanza cada pedido cubierto por la OC a parcial o recepcionado según el saldo resultante. */
    private void actualizarPedidos(CompraFormalizacion oc, Integer usuarioId) {
        Map<Long, Integer> pedidoPorItem = compraRecepcionPedidoPort
                .pedidoIdsByFormalizacionItem(oc.getCompraFormalizacionId());
        if (pedidoPorItem.isEmpty() || oc.getItems() == null) {
            return;
        }
        Map<Long, BigDecimal> cubierto = CalculadoraSaldosRecepcion.cubiertoPorItem(
                compraRecepcionRepository.findByCompraFormalizacionId(oc.getCompraFormalizacionId()));
        Map<Integer, List<CompraFormalizacionItem>> porPedido = oc.getItems().stream()
                .filter(item -> pedidoPorItem.get(item.getCompraFormalizacionItemId()) != null)
                .collect(Collectors.groupingBy(item -> pedidoPorItem.get(item.getCompraFormalizacionItemId()),
                        LinkedHashMap::new, Collectors.toList()));
        porPedido.forEach((pedidoId, items) -> {
            boolean completo = items.stream().allMatch(item -> CalculadoraSaldosRecepcion
                    .saldo(item.getCantidad(), cubierto.get(item.getCompraFormalizacionItemId()))
                    .signum() == 0);
            if (completo) {
                compraPedidoService.marcarRecepcionado(pedidoId, usuarioId, null);
            } else {
                compraPedidoService.marcarRecepcionParcial(pedidoId);
            }
        });
    }

    /** {@code true} cuando todos los pedidos de la OC están {@code RECEPCIONADO}. */
    private boolean cerrada(CompraFormalizacion oc) {
        List<Integer> pedidoIds = pedidosDe(oc);
        if (pedidoIds.isEmpty()) {
            return false;
        }
        return pedidoIds.stream().allMatch(pedidoId -> compraPedidoService.getById(pedidoId)
                .map(pedido -> pedido.getEstado() == CompraPedidoEstado.RECEPCIONADO)
                .orElse(false));
    }

    private List<Integer> pedidosDe(CompraFormalizacion oc) {
        return compraRecepcionPedidoPort.pedidoIdsByFormalizacionItem(oc.getCompraFormalizacionId())
                .values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream()
                .toList();
    }

    private CompraRecepcionPendiente resumen(CompraFormalizacion oc, List<CompraRecepcion> recepciones) {
        Map<Long, BigDecimal> cubierto = CalculadoraSaldosRecepcion.cubiertoPorItem(recepciones);
        List<CompraRecepcionPendienteItem> items = (oc.getItems() == null ? List.<CompraFormalizacionItem>of()
                : oc.getItems()).stream()
                .map(item -> {
                    BigDecimal recibido = item.getCompraFormalizacionItemId() == null ? BigDecimal.ZERO
                            : cubierto.getOrDefault(item.getCompraFormalizacionItemId(), BigDecimal.ZERO);
                    return new CompraRecepcionPendienteItem(
                            item.getCompraFormalizacionItemId(),
                            item.getCompraPedidoItemId(),
                            item.getOrden(),
                            item.getCantidad(),
                            recibido,
                            CalculadoraSaldosRecepcion.saldo(item.getCantidad(), recibido),
                            item.getUnidad(),
                            item.getDescripcion(),
                            item.getEspecificaciones(),
                            item.getPrecioUnitario(),
                            item.getSubtotal(),
                            item.getMarca());
                })
                .toList();
        return new CompraRecepcionPendiente(
                oc.getCompraFormalizacionId(),
                oc.getNumero(),
                oc.getNumeroExterno(),
                oc.getEjercicioId(),
                oc.getProveedorId(),
                oc.getFecha(),
                oc.getMonto(),
                oc.getMoneda(),
                items,
                recepciones == null ? List.of() : recepciones);
    }

}

package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.service.CompraCotizacionService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacionDecision;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.exception.CompraFormalizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in.CreateCompraFormalizacionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in.GetCompraFormalizacionByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in.ListCompraFormalizacionesUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out.CompraFormalizacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out.CompraFormalizacionSecuenciaPort;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.service.CompraPedidoItemService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Fachada de la Orden de Compra (etapa 8).
 *
 * <p>Composición cross-slice en la capa de aplicación (patrón pautado del subdominio
 * {@code pedidos}): el instrumento se arma desde la adjudicación y el proceso de cotización
 * ({@link CompraCotizacionService}) y desde los ítems del pedido ({@link CompraPedidoItemService}),
 * y dispara las transiciones de estado del proceso y de los pedidos cubiertos.</p>
 */
@Service
@RequiredArgsConstructor
public class CompraFormalizacionService {

    private static final CompraAdjudicacionDecision DECISION_ADJUDICADA = CompraAdjudicacionDecision.ADJUDICADA;

    private final CreateCompraFormalizacionUseCase createCompraFormalizacionUseCase;
    private final GetCompraFormalizacionByIdUseCase getCompraFormalizacionByIdUseCase;
    private final ListCompraFormalizacionesUseCase listCompraFormalizacionesUseCase;
    private final CompraFormalizacionRepository compraFormalizacionRepository;
    private final CompraFormalizacionSecuenciaPort compraFormalizacionSecuenciaPort;
    // Excepción cross-slice autorizada: composición de slices del subdominio "pedidos".
    private final CompraCotizacionService compraCotizacionService;
    private final CompraPedidoItemService compraPedidoItemService;

    public Optional<CompraFormalizacion> getById(Long compraFormalizacionId) {
        return getCompraFormalizacionByIdUseCase.getById(compraFormalizacionId);
    }

    public List<CompraFormalizacion> listar(CompraFormalizacionCriteria criteria) {
        return listCompraFormalizacionesUseCase.listar(criteria);
    }

    /** La OC ya emitida para un proceso de cotización, si existe. */
    public Optional<CompraFormalizacion> porCotizacion(Long compraCotizacionId) {
        return compraCotizacionService.adjudicacion(compraCotizacionId)
                .map(CompraAdjudicacion::getCompraAdjudicacionId)
                .flatMap(compraFormalizacionRepository::findByCompraAdjudicacionId);
    }

    /** Adjudicaciones del ejercicio pendientes de emitir la OC (bandeja de formalización). */
    public List<CompraFormalizacionPendiente> pendientes(Integer ejercicioId) {
        return compraCotizacionService
                .listar(new CompraCotizacionCriteria(CompraCotizacionEstado.ADJUDICADA.name(), ejercicioId, null, null))
                .stream()
                .map(this::pendiente)
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * Emite la Orden de Compra de un proceso adjudicado: reserva el número {@code OC-AAAA-NNNNNN},
     * snapshotea condiciones e ítems y avanza el proceso a {@code FORMALIZADA} con sus pedidos a
     * {@code FORMALIZADO}. Idempotente: si la adjudicación ya tiene OC, devuelve la existente.
     */
    @Transactional
    public CompraFormalizacion formalizar(Long compraCotizacionId, CompraFormalizacion datos, Integer usuarioId) {
        CompraAdjudicacion adjudicacion = compraCotizacionService.adjudicacion(compraCotizacionId)
                .filter(a -> a.getDecision() == DECISION_ADJUDICADA)
                .orElseThrow(() -> new CompraFormalizacionException(
                        "El proceso de cotización " + compraCotizacionId + " no tiene una adjudicación válida"));
        Optional<CompraFormalizacion> existente = compraFormalizacionRepository
                .findByCompraAdjudicacionId(adjudicacion.getCompraAdjudicacionId());
        if (existente.isPresent()) {
            return existente.get();
        }
        CompraCotizacion proceso = compraCotizacionService.getById(compraCotizacionId)
                .orElseThrow(() -> new CompraFormalizacionException(
                        "No existe el proceso de cotización " + compraCotizacionId));
        CompraCotizacionProveedor awarded = documentoAdjudicado(proceso, adjudicacion)
                .orElseThrow(() -> new CompraFormalizacionException(
                        "La oferta adjudicada no pertenece al proceso " + compraCotizacionId));

        LocalDateTime ahora = LocalDateTime.now();
        int anio = (proceso.getFecha() != null ? proceso.getFecha() : ahora).getYear();
        int correlativo = compraFormalizacionSecuenciaPort.reservarNumero(proceso.getEjercicioId(), anio);

        CompraFormalizacion aCrear = CompraFormalizacion.builder()
                .compraAdjudicacionId(adjudicacion.getCompraAdjudicacionId())
                .numero(String.format("OC-%d-%06d", anio, correlativo))
                .numeroExterno(datos != null ? datos.getNumeroExterno() : null)
                .ejercicioId(proceso.getEjercicioId())
                .fecha(ahora)
                .proveedorId(adjudicacion.getProveedorAdjudicadoId() != null
                        ? adjudicacion.getProveedorAdjudicadoId() : awarded.getProveedorId())
                .moneda(elegir(datos != null ? datos.getMoneda() : null, awarded.getMoneda()))
                .monto(elegir(datos != null ? datos.getMonto() : null, adjudicacion.getMonto()))
                .condicionPago(elegir(datos != null ? datos.getCondicionPago() : null, awarded.getCondicionPago()))
                .plazoEntrega(elegir(datos != null ? datos.getPlazoEntrega() : null, awarded.getPlazoEntrega()))
                .garantia(elegir(datos != null ? datos.getGarantia() : null, awarded.getGarantia()))
                .objeto(datos != null ? datos.getObjeto() : null)
                .observacion(datos != null ? datos.getObservacion() : null)
                .usuarioId(usuarioId)
                .items(construirItems(proceso, awarded))
                .build();

        CompraFormalizacion creada = createCompraFormalizacionUseCase.crear(aCrear);
        compraCotizacionService.formalizar(compraCotizacionId);
        return creada;
    }

    // ---- Interno ----

    private Optional<CompraFormalizacionPendiente> pendiente(CompraCotizacion proceso) {
        Optional<CompraAdjudicacion> adjudicacion = compraCotizacionService.adjudicacion(proceso.getCompraCotizacionId())
                .filter(a -> a.getDecision() == DECISION_ADJUDICADA);
        if (adjudicacion.isEmpty()
                || compraFormalizacionRepository.existsByCompraAdjudicacionId(adjudicacion.get().getCompraAdjudicacionId())) {
            return Optional.empty();
        }
        return documentoAdjudicado(proceso, adjudicacion.get()).map(awarded -> new CompraFormalizacionPendiente(
                proceso.getCompraCotizacionId(),
                proceso.getNumero(),
                proceso.getEjercicioId(),
                adjudicacion.get().getCompraAdjudicacionId(),
                awarded.getCompraCotizacionProveedorId(),
                awarded.getProveedorId(),
                adjudicacion.get().getMonto(),
                awarded.getMoneda(),
                awarded.getCondicionPago(),
                awarded.getPlazoEntrega(),
                awarded.getGarantia(),
                construirItems(proceso, awarded)));
    }

    private Optional<CompraCotizacionProveedor> documentoAdjudicado(CompraCotizacion proceso, CompraAdjudicacion adjudicacion) {
        if (proceso.getProveedores() == null) {
            return Optional.empty();
        }
        return proceso.getProveedores().stream()
                .filter(p -> p.getCompraCotizacionProveedorId() != null
                        && p.getCompraCotizacionProveedorId().equals(adjudicacion.getCompraCotizacionProveedorId()))
                .findFirst();
    }

    /**
     * Snapshot de ítems: cantidad y vínculo desde el proceso, descripción/unidad desde el ítem del
     * pedido y precio/marca desde la oferta adjudicada (nulos si el proveedor no cotizó ese ítem).
     */
    private List<CompraFormalizacionItem> construirItems(CompraCotizacion proceso, CompraCotizacionProveedor awarded) {
        Map<Integer, CompraPedidoItem> pedidoItems = pedidoItemsPorId(proceso.getCompraCotizacionId());
        Map<Long, CompraCotizacionProveedorItem> precios = awarded.getItems() == null ? Map.of()
                : awarded.getItems().stream()
                        .filter(i -> i.getCompraCotizacionItemId() != null)
                        .collect(Collectors.toMap(CompraCotizacionProveedorItem::getCompraCotizacionItemId,
                                Function.identity(), (a, b) -> a));
        List<CompraFormalizacionItem> items = new ArrayList<>();
        int orden = 1;
        for (CompraCotizacionItem item : proceso.getItems() == null ? List.<CompraCotizacionItem>of() : proceso.getItems()) {
            CompraPedidoItem pedidoItem = item.getCompraPedidoItemId() == null
                    ? null : pedidoItems.get(item.getCompraPedidoItemId());
            CompraCotizacionProveedorItem precio = item.getCompraCotizacionItemId() == null
                    ? null : precios.get(item.getCompraCotizacionItemId());
            items.add(CompraFormalizacionItem.builder()
                    .compraCotizacionItemId(item.getCompraCotizacionItemId())
                    .compraPedidoItemId(item.getCompraPedidoItemId())
                    .orden(item.getOrden() != null ? item.getOrden() : orden)
                    .cantidad(item.getCantidad())
                    .unidad(pedidoItem != null ? pedidoItem.getUnidad() : null)
                    .descripcion(pedidoItem != null ? pedidoItem.getDescripcion() : null)
                    .especificaciones(pedidoItem != null ? pedidoItem.getEspecificaciones() : null)
                    .precioUnitario(precio != null ? precio.getPrecioUnitario() : null)
                    .subtotal(precio != null ? precio.getSubtotal() : null)
                    .marca(precio != null ? precio.getMarca() : null)
                    .build());
            orden++;
        }
        return items;
    }

    private Map<Integer, CompraPedidoItem> pedidoItemsPorId(Long compraCotizacionId) {
        Map<Integer, CompraPedidoItem> porId = new LinkedHashMap<>();
        for (Integer pedidoId : compraCotizacionService.pedidoIdsByProceso(compraCotizacionId)) {
            for (CompraPedidoItem item : compraPedidoItemService.getByPedido(pedidoId)) {
                if (item.getCompraPedidoItemId() != null) {
                    porId.put(item.getCompraPedidoItemId(), item);
                }
            }
        }
        return porId;
    }

    private static <T> T elegir(T valorDatos, T valorSnapshot) {
        return valorDatos != null ? valorDatos : valorSnapshot;
    }

}

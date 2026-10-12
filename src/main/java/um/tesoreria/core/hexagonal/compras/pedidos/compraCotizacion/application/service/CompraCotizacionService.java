package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacionDecision;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.*;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraAdjudicacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionSecuenciaPort;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.service.CompraCotizacionTramoService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.service.CompraPedidoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Fachada del proceso de cotización.
 *
 * <p>Composición cross-slice en la capa de aplicación: al enviar, el proceso avanza y los pedidos
 * cuyos ítems quedaron íntegramente cubiertos pasan a {@code EN_COTIZACION} (vía
 * {@link CompraPedidoService}, mismo patrón pautado en el subdominio {@code pedidos}).</p>
 */
@Service
@RequiredArgsConstructor
public class CompraCotizacionService {

    private final CreateCompraCotizacionUseCase createCompraCotizacionUseCase;
    private final UpdateCompraCotizacionUseCase updateCompraCotizacionUseCase;
    private final GetCompraCotizacionByIdUseCase getCompraCotizacionByIdUseCase;
    private final ListCompraCotizacionesUseCase listCompraCotizacionesUseCase;
    private final EnviarCompraCotizacionUseCase enviarCompraCotizacionUseCase;
    private final AnularCompraCotizacionUseCase anularCompraCotizacionUseCase;
    private final ListPedidoItemsPendientesUseCase listPedidoItemsPendientesUseCase;
    private final CompraCotizacionSecuenciaPort compraCotizacionSecuenciaPort;
    private final CompraCotizacionRepository compraCotizacionRepository;
    private final CompraPedidoService compraPedidoService;
    private final CompraCotizacionTramoService compraCotizacionTramoService;
    private final RegistrarRespuestaCompraCotizacionUseCase registrarRespuestaCompraCotizacionUseCase;
    private final MarcarNoCotizaCompraCotizacionUseCase marcarNoCotizaCompraCotizacionUseCase;
    private final RecomendarCompraCotizacionUseCase recomendarCompraCotizacionUseCase;
    private final CerrarCompraCotizacionUseCase cerrarCompraCotizacionUseCase;
    private final AdjudicarCompraCotizacionUseCase adjudicarCompraCotizacionUseCase;
    private final FormalizarCompraCotizacionUseCase formalizarCompraCotizacionUseCase;
    private final CompraAdjudicacionRepository compraAdjudicacionRepository;

    public CompraCotizacion crear(CompraCotizacion proceso) {
        return createCompraCotizacionUseCase.crear(completarMontos(proceso));
    }

    public CompraCotizacion actualizar(Long compraCotizacionId, CompraCotizacion proceso) {
        return updateCompraCotizacionUseCase.actualizar(compraCotizacionId, completarMontos(proceso));
    }

    /**
     * Recalcula el monto estimado del proceso (suma de los montos estimados de los pedidos
     * combinados, D-COTZ-3) y su mínimo de cotizaciones (tramos × referencia).
     */
    private CompraCotizacion completarMontos(CompraCotizacion proceso) {
        if (proceso == null) {
            return null;
        }
        List<Integer> itemIds = proceso.getItems() == null ? List.of()
                : proceso.getItems().stream().map(i -> i.getCompraPedidoItemId()).toList();
        BigDecimal suma = compraCotizacionRepository.montoEstimadoDeItems(itemIds);
        proceso.setMontoEstimado(suma);
        proceso.setCantidadMinima(compraCotizacionTramoService
                .resolverMinimo(proceso.getEjercicioId(), suma).orElse(null));
        return proceso;
    }

    public Optional<CompraCotizacion> getById(Long compraCotizacionId) {
        return getCompraCotizacionByIdUseCase.getById(compraCotizacionId);
    }

    public List<CompraCotizacion> listar(CompraCotizacionCriteria criteria) {
        return listCompraCotizacionesUseCase.listar(criteria);
    }

    public List<CompraCotizacionPendiente> pendientes(Integer ejercicioId, List<Integer> dependenciaIds) {
        return listPedidoItemsPendientesUseCase.listar(ejercicioId, dependenciaIds);
    }

    /**
     * Documentos de proveedor en {@code ENVIADA} de todos los procesos (bandeja "Cotizaciones
     * pedidas"). Reutiliza el listado por ejercicio y aplana proceso + documento + ítems del
     * proceso; no persiste ni agrega DDL.
     */
    public List<CompraCotizacionProveedorPendiente> respuestasPendientes(Integer ejercicioId) {
        return listCompraCotizacionesUseCase.listar(new CompraCotizacionCriteria(null, ejercicioId, null, null))
                .stream()
                .flatMap(proceso -> (proceso.getProveedores() == null ? List.<CompraCotizacionProveedor>of()
                        : proceso.getProveedores()).stream()
                        .filter(proveedor -> proveedor.getEstado() == CompraCotizacionProveedorEstado.ENVIADA)
                        .map(proveedor -> new CompraCotizacionProveedorPendiente(proceso.getCompraCotizacionId(),
                                proceso.getNumero(), proceso.getEjercicioId(), proceso.getFechaLimiteRespuesta(),
                                proveedor, proceso.getItems() == null ? List.of() : proceso.getItems())))
                .toList();
    }

    public CompraCotizacion anular(Long compraCotizacionId, String motivo) {
        return anularCompraCotizacionUseCase.anular(compraCotizacionId, motivo);
    }

    /**
     * Envía el proceso: reserva el número {@code SC-AAAA-NNNNNN} (sólo la primera vez), valida el
     * mínimo y marca los documentos de proveedor. Luego, cada pedido cuyos ítems quedan cubiertos
     * por procesos enviados pasa a {@code EN_COTIZACION}.
     */
    @Transactional
    public CompraCotizacion enviar(Long compraCotizacionId) {
        CompraCotizacion proceso = getCompraCotizacionByIdUseCase.getById(compraCotizacionId)
                .orElseThrow(() -> new CompraCotizacionException(compraCotizacionId));
        String numero = proceso.getNumero();
        if (numero == null) {
            int anio = (proceso.getFecha() != null ? proceso.getFecha() : LocalDateTime.now()).getYear();
            int correlativo = compraCotizacionSecuenciaPort.reservarNumero(proceso.getEjercicioId(), anio);
            numero = String.format("SC-%d-%06d", anio, correlativo);
        }
        CompraCotizacion enviado = enviarCompraCotizacionUseCase.enviar(compraCotizacionId, numero);
        for (Integer pedidoId : compraCotizacionRepository.findPedidoIdsByProceso(compraCotizacionId)) {
            if (compraCotizacionRepository.todosLosItemsEnProcesosEnviados(pedidoId)) {
                compraPedidoService.marcarEnCotizacion(pedidoId);
            }
        }
        return enviado;
    }

    // ---- Etapa 6C: respuestas, comparación y recomendación ----

    public CompraCotizacionProveedor registrarRespuesta(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                                        CompraCotizacionProveedor respuesta) {
        return registrarRespuestaCompraCotizacionUseCase.registrarRespuesta(
                compraCotizacionId, compraCotizacionProveedorId, respuesta);
    }

    public CompraCotizacionProveedor marcarNoCotiza(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                                    String observacion) {
        return marcarNoCotizaCompraCotizacionUseCase.marcarNoCotiza(
                compraCotizacionId, compraCotizacionProveedorId, observacion);
    }

    /** Proceso completo (ítems + proveedores con su detalle) para la matriz de comparación. */
    public CompraCotizacion comparacion(Long compraCotizacionId) {
        return getCompraCotizacionByIdUseCase.getById(compraCotizacionId)
                .orElseThrow(() -> new CompraCotizacionException(compraCotizacionId));
    }

    /**
     * Registra la recomendación y avanza a {@code PENDIENTE_ADJUDICACION} los pedidos cubiertos
     * por el proceso (D-COTZ-10).
     */
    @Transactional
    public CompraCotizacion recomendar(Long compraCotizacionId, Long compraCotizacionProveedorId, String fundamento) {
        CompraCotizacion recomendado = recomendarCompraCotizacionUseCase.recomendar(
                compraCotizacionId, compraCotizacionProveedorId, fundamento);
        for (Integer pedidoId : compraCotizacionRepository.findPedidoIdsByProceso(compraCotizacionId)) {
            compraPedidoService.marcarPendienteAdjudicacion(pedidoId);
        }
        return recomendado;
    }

    public CompraCotizacion cerrar(Long compraCotizacionId) {
        return cerrarCompraCotizacionUseCase.cerrar(compraCotizacionId);
    }

    // ---- Etapa 7: adjudicación por monto ----

    /**
     * Registra la adjudicación del proceso recomendado y, si se adjudica, avanza a
     * {@code ADJUDICADO} los pedidos cubiertos.
     */
    @Transactional
    public CompraAdjudicacion adjudicar(Long compraCotizacionId, CompraAdjudicacionDecision decision,
                                        Long compraCotizacionProveedorId, Integer proveedorAdjudicadoId,
                                        BigDecimal monto, String fundamento, Long autoridadUsuarioId,
                                        BigDecimal limiteAplicado) {
        CompraAdjudicacion adjudicacion = adjudicarCompraCotizacionUseCase.adjudicar(compraCotizacionId, decision,
                compraCotizacionProveedorId, proveedorAdjudicadoId, monto, fundamento, autoridadUsuarioId,
                limiteAplicado);
        if (decision == CompraAdjudicacionDecision.ADJUDICADA) {
            for (Integer pedidoId : compraCotizacionRepository.findPedidoIdsByProceso(compraCotizacionId)) {
                compraPedidoService.marcarAdjudicado(pedidoId);
            }
        }
        return adjudicacion;
    }

    public Optional<CompraAdjudicacion> adjudicacion(Long compraCotizacionId) {
        return compraAdjudicacionRepository.findByCompraCotizacionId(compraCotizacionId);
    }

    // ---- Etapa 8: formalización (Orden de Compra) ----

    /**
     * Marca el proceso adjudicado como {@code FORMALIZADA} y avanza a {@code FORMALIZADO} los
     * pedidos cubiertos por sus ítems (gate de la recepción).
     */
    @Transactional
    public CompraCotizacion formalizar(Long compraCotizacionId) {
        CompraCotizacion formalizado = formalizarCompraCotizacionUseCase.formalizar(compraCotizacionId);
        for (Integer pedidoId : compraCotizacionRepository.findPedidoIdsByProceso(compraCotizacionId)) {
            compraPedidoService.marcarFormalizado(pedidoId);
        }
        return formalizado;
    }

    /** Ids de pedido alcanzados por los ítems del proceso (trazabilidad de la formalización). */
    public List<Integer> pedidoIdsByProceso(Long compraCotizacionId) {
        return compraCotizacionRepository.findPedidoIdsByProceso(compraCotizacionId);
    }
}

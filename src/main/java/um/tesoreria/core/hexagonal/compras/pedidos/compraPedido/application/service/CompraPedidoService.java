package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.exception.CompraPedidoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in.*;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.service.CompraPedidoItemService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.application.service.CompraPedidoSecuenciaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Fachada del pedido de compra.
 *
 * <p>Composición cross-slice en la capa de aplicación (mismo patrón que
 * {@code ChequeraSerieService} que inyecta {@code ChequeraCuotaService}): el pedido compone
 * el detalle y el correlativo anual, que viven en slices propios. Los dominios de cada
 * slice permanecen puros.</p>
 */
@Service
@RequiredArgsConstructor
public class CompraPedidoService {

    private final CreateCompraPedidoUseCase createCompraPedidoUseCase;
    private final UpdateCompraPedidoUseCase updateCompraPedidoUseCase;
    private final EnviarCompraPedidoUseCase enviarCompraPedidoUseCase;
    private final AutorizarCompraPedidoUseCase autorizarCompraPedidoUseCase;
    private final RechazarCompraPedidoUseCase rechazarCompraPedidoUseCase;
    private final GetCompraPedidoByIdUseCase getCompraPedidoByIdUseCase;
    private final GetCompraPedidoByNumeroUseCase getCompraPedidoByNumeroUseCase;
    private final ListCompraPedidosUseCase listCompraPedidosUseCase;

    // Excepción cross-slice autorizada: composición de slices del subdominio "pedidos".
    private final CompraPedidoItemService compraPedidoItemService;
    private final CompraPedidoSecuenciaService compraPedidoSecuenciaService;

    @Transactional
    public CompraPedido crear(CompraPedido pedido, List<CompraPedidoItem> items) {
        CompraPedido creado = createCompraPedidoUseCase.crear(pedido);
        compraPedidoItemService.reemplazarItems(creado.getCompraPedidoId(), items);
        return creado;
    }

    @Transactional
    public CompraPedido actualizar(Integer compraPedidoId, CompraPedido datos, List<CompraPedidoItem> items) {
        CompraPedido actualizado = updateCompraPedidoUseCase.actualizar(compraPedidoId, datos);
        compraPedidoItemService.reemplazarItems(compraPedidoId, items);
        return actualizado;
    }

    /**
     * Envía el pedido: reserva el correlativo anual y lo fija en el número público
     * {@code PC-AAAA-NNNNNN}. La reserva y la transición de estado ocurren en la misma
     * transacción.
     */
    @Transactional
    public CompraPedido enviar(Integer compraPedidoId) {
        CompraPedido pedido = getCompraPedidoByIdUseCase.getById(compraPedidoId)
                .orElseThrow(() -> new CompraPedidoException(compraPedidoId));
        int anio = (pedido.getFecha() != null ? pedido.getFecha() : LocalDateTime.now()).getYear();
        int correlativo = compraPedidoSecuenciaService.reservarNumero(pedido.getEjercicioId(), anio);
        return enviarCompraPedidoUseCase.enviar(compraPedidoId, formatearNumero(anio, correlativo));
    }

    @Transactional
    public CompraPedido autorizar(Integer compraPedidoId, Integer autorizanteId) {
        return autorizarCompraPedidoUseCase.autorizar(compraPedidoId, autorizanteId);
    }

    @Transactional
    public CompraPedido rechazar(Integer compraPedidoId) {
        return rechazarCompraPedidoUseCase.rechazar(compraPedidoId);
    }

    public Optional<CompraPedido> getById(Integer compraPedidoId) {
        return getCompraPedidoByIdUseCase.getById(compraPedidoId);
    }

    public Optional<CompraPedido> getByNumero(String numero) {
        return getCompraPedidoByNumeroUseCase.getByNumero(numero);
    }

    public List<CompraPedido> listar(CompraPedidoCriteria criteria) {
        return listCompraPedidosUseCase.listar(criteria);
    }

    private String formatearNumero(int anio, int correlativo) {
        return String.format("PC-%d-%06d", anio, correlativo);
    }

}

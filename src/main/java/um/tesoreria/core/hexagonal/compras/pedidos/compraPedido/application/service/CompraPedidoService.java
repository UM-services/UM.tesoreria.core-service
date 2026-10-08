package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.exception.CompraPedidoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in.*;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.application.service.CompraPedidoHistorialService;
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
 * el detalle, el correlativo anual y la línea de tiempo de estados, que viven en slices
 * propios. Los dominios de cada slice permanecen puros.</p>
 */
@Service
@RequiredArgsConstructor
public class CompraPedidoService {

    private final CreateCompraPedidoUseCase createCompraPedidoUseCase;
    private final UpdateCompraPedidoUseCase updateCompraPedidoUseCase;
    private final EnviarCompraPedidoUseCase enviarCompraPedidoUseCase;
    private final AprobarCompraPedidoUseCase aprobarCompraPedidoUseCase;
    private final RechazarCompraPedidoUseCase rechazarCompraPedidoUseCase;
    private final DescartarCompraPedidoUseCase descartarCompraPedidoUseCase;
    private final GetCompraPedidoByIdUseCase getCompraPedidoByIdUseCase;
    private final GetCompraPedidoByNumeroUseCase getCompraPedidoByNumeroUseCase;
    private final ListCompraPedidosUseCase listCompraPedidosUseCase;

    // Excepción cross-slice autorizada: composición de slices del subdominio "pedidos".
    private final CompraPedidoItemService compraPedidoItemService;
    private final CompraPedidoSecuenciaService compraPedidoSecuenciaService;
    private final CompraPedidoHistorialService compraPedidoHistorialService;

    @Transactional
    public CompraPedido crear(CompraPedido pedido, List<CompraPedidoItem> items) {
        CompraPedido creado = createCompraPedidoUseCase.crear(pedido);
        compraPedidoItemService.reemplazarItems(creado.getCompraPedidoId(), items);
        registrarHistorial(creado, creado.getSolicitanteId(), null);
        return creado;
    }

    @Transactional
    public CompraPedido actualizar(Integer compraPedidoId, CompraPedido datos, List<CompraPedidoItem> items) {
        CompraPedido actualizado = updateCompraPedidoUseCase.actualizar(compraPedidoId, datos);
        compraPedidoItemService.reemplazarItems(compraPedidoId, items);
        return actualizado;
    }

    /**
     * Presenta el pedido a la bandeja del autorizante: reserva el correlativo anual (sólo la
     * primera vez) y lo fija en el número público {@code PC-AAAA-NNNNNN}. La reserva y la
     * transición de estado ocurren en la misma transacción.
     */
    @Transactional
    public CompraPedido enviar(Integer compraPedidoId, Integer usuarioId) {
        CompraPedido pedido = getCompraPedidoByIdUseCase.getById(compraPedidoId)
                .orElseThrow(() -> new CompraPedidoException(compraPedidoId));
        String numero = pedido.getNumero();
        if (numero == null) {
            int anio = (pedido.getFecha() != null ? pedido.getFecha() : LocalDateTime.now()).getYear();
            int correlativo = compraPedidoSecuenciaService.reservarNumero(pedido.getEjercicioId(), anio);
            numero = formatearNumero(anio, correlativo);
        }
        CompraPedido enviado = enviarCompraPedidoUseCase.enviar(compraPedidoId, numero);
        registrarHistorial(enviado, usuarioId, null);
        return enviado;
    }

    @Transactional
    public CompraPedido aprobar(Integer compraPedidoId, Integer autorizanteId) {
        CompraPedido aprobado = aprobarCompraPedidoUseCase.aprobar(compraPedidoId, autorizanteId);
        registrarHistorial(aprobado, autorizanteId, null);
        return aprobado;
    }

    @Transactional
    public CompraPedido rechazar(Integer compraPedidoId, Integer autorizanteId, String motivo) {
        CompraPedido rechazado = rechazarCompraPedidoUseCase.rechazar(compraPedidoId, autorizanteId, motivo);
        registrarHistorial(rechazado, autorizanteId, motivo);
        return rechazado;
    }

    @Transactional
    public CompraPedido descartar(Integer compraPedidoId, Integer usuarioId, String motivo) {
        CompraPedido descartado = descartarCompraPedidoUseCase.descartar(compraPedidoId, motivo);
        registrarHistorial(descartado, usuarioId, motivo);
        return descartado;
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

    private void registrarHistorial(CompraPedido pedido, Integer usuarioId, String observacion) {
        if (pedido.getEstado() == null) {
            return;
        }
        compraPedidoHistorialService.registrar(pedido.getCompraPedidoId(), pedido.getEstado().name(),
                usuarioId, observacion);
    }

    private String formatearNumero(int anio, int correlativo) {
        return String.format("PC-%d-%06d", anio, correlativo);
    }

}

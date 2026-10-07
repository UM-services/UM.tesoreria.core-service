package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in.*;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.service.CompraPedidoItemService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.application.service.CompraPedidoSecuenciaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraPedidoServiceTest {

    @Mock private CreateCompraPedidoUseCase createCompraPedidoUseCase;
    @Mock private UpdateCompraPedidoUseCase updateCompraPedidoUseCase;
    @Mock private EnviarCompraPedidoUseCase enviarCompraPedidoUseCase;
    @Mock private AutorizarCompraPedidoUseCase autorizarCompraPedidoUseCase;
    @Mock private RechazarCompraPedidoUseCase rechazarCompraPedidoUseCase;
    @Mock private GetCompraPedidoByIdUseCase getCompraPedidoByIdUseCase;
    @Mock private GetCompraPedidoByNumeroUseCase getCompraPedidoByNumeroUseCase;
    @Mock private ListCompraPedidosUseCase listCompraPedidosUseCase;
    @Mock private CompraPedidoItemService compraPedidoItemService;
    @Mock private CompraPedidoSecuenciaService compraPedidoSecuenciaService;

    private CompraPedidoService service;

    @BeforeEach
    void setUp() {
        service = new CompraPedidoService(createCompraPedidoUseCase, updateCompraPedidoUseCase,
                enviarCompraPedidoUseCase, autorizarCompraPedidoUseCase, rechazarCompraPedidoUseCase,
                getCompraPedidoByIdUseCase, getCompraPedidoByNumeroUseCase, listCompraPedidosUseCase,
                compraPedidoItemService, compraPedidoSecuenciaService);
    }

    @Test
    void crearGrabaLaCabeceraYReemplazaElDetalle() {
        CompraPedido datos = CompraPedido.builder().solicitanteId(1).dependenciaId(2).build();
        CompraPedido creado = CompraPedido.builder().compraPedidoId(5).solicitanteId(1).dependenciaId(2).build();
        List<CompraPedidoItem> items = List.of(CompraPedidoItem.builder().orden(1).build());
        when(createCompraPedidoUseCase.crear(datos)).thenReturn(creado);

        CompraPedido resultado = service.crear(datos, items);

        assertThat(resultado.getCompraPedidoId()).isEqualTo(5);
        verify(compraPedidoItemService).reemplazarItems(5, items);
    }

    @Test
    void enviarReservaElCorrelativoYFormateaElNumero() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5)
                .ejercicioId(7)
                .fecha(LocalDateTime.of(2026, 10, 6, 9, 0))
                .build();
        when(getCompraPedidoByIdUseCase.getById(5)).thenReturn(Optional.of(pedido));
        when(compraPedidoSecuenciaService.reservarNumero(7, 2026)).thenReturn(1);
        when(enviarCompraPedidoUseCase.enviar(eq(5), eq("PC-2026-000001"))).thenReturn(pedido);

        service.enviar(5);

        verify(compraPedidoSecuenciaService).reservarNumero(7, 2026);
        verify(enviarCompraPedidoUseCase).enviar(5, "PC-2026-000001");
    }

    @Test
    void autorizarYRechazarDeleganEnSusCasosDeUso() {
        CompraPedido pedido = CompraPedido.builder().compraPedidoId(5).build();
        when(autorizarCompraPedidoUseCase.autorizar(5, 9)).thenReturn(pedido);
        when(rechazarCompraPedidoUseCase.rechazar(5)).thenReturn(pedido);

        assertThat(service.autorizar(5, 9)).isEqualTo(pedido);
        assertThat(service.rechazar(5)).isEqualTo(pedido);
    }

}

package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in.*;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.application.service.CompraPedidoHistorialService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.service.CompraPedidoItemService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.application.service.CompraPedidoSecuenciaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraPedidoServiceTest {

    @Mock private CreateCompraPedidoUseCase createCompraPedidoUseCase;
    @Mock private UpdateCompraPedidoUseCase updateCompraPedidoUseCase;
    @Mock private EnviarCompraPedidoUseCase enviarCompraPedidoUseCase;
    @Mock private AprobarCompraPedidoUseCase aprobarCompraPedidoUseCase;
    @Mock private RechazarCompraPedidoUseCase rechazarCompraPedidoUseCase;
    @Mock private DescartarCompraPedidoUseCase descartarCompraPedidoUseCase;
    @Mock private EstimarCompraPedidoUseCase estimarCompraPedidoUseCase;
    @Mock private AutorizarPresupuestoCompraPedidoUseCase autorizarPresupuestoCompraPedidoUseCase;
    @Mock private RechazarPresupuestoCompraPedidoUseCase rechazarPresupuestoCompraPedidoUseCase;
    @Mock private GetCompraPedidoByIdUseCase getCompraPedidoByIdUseCase;
    @Mock private GetCompraPedidoByNumeroUseCase getCompraPedidoByNumeroUseCase;
    @Mock private ListCompraPedidosUseCase listCompraPedidosUseCase;
    @Mock private CompraPedidoItemService compraPedidoItemService;
    @Mock private CompraPedidoSecuenciaService compraPedidoSecuenciaService;
    @Mock private CompraPedidoHistorialService compraPedidoHistorialService;

    private CompraPedidoService service;

    @BeforeEach
    void setUp() {
        service = new CompraPedidoService(createCompraPedidoUseCase, updateCompraPedidoUseCase,
                enviarCompraPedidoUseCase, aprobarCompraPedidoUseCase, rechazarCompraPedidoUseCase,
                descartarCompraPedidoUseCase, estimarCompraPedidoUseCase,
                autorizarPresupuestoCompraPedidoUseCase, rechazarPresupuestoCompraPedidoUseCase,
                getCompraPedidoByIdUseCase, getCompraPedidoByNumeroUseCase,
                listCompraPedidosUseCase, compraPedidoItemService, compraPedidoSecuenciaService,
                compraPedidoHistorialService);
    }

    @Test
    void crearGrabaLaCabeceraReemplazaElDetalleYRegistraElHistorial() {
        CompraPedido datos = CompraPedido.builder().solicitanteId(1).dependenciaId(2).build();
        CompraPedido creado = CompraPedido.builder()
                .compraPedidoId(5)
                .solicitanteId(1)
                .dependenciaId(2)
                .estado(CompraPedidoEstado.BORRADOR)
                .build();
        List<CompraPedidoItem> items = List.of(CompraPedidoItem.builder().orden(1).build());
        when(createCompraPedidoUseCase.crear(datos)).thenReturn(creado);

        CompraPedido resultado = service.crear(datos, items);

        assertThat(resultado.getCompraPedidoId()).isEqualTo(5);
        verify(compraPedidoItemService).reemplazarItems(5, items);
        verify(compraPedidoHistorialService).registrar(5, "BORRADOR", 1, null);
    }

    @Test
    void actualizarGrabaLaCabeceraSinRegistrarHistorial() {
        CompraPedido datos = CompraPedido.builder().necesidad("x").build();
        CompraPedido actualizado = CompraPedido.builder().compraPedidoId(5).build();
        when(updateCompraPedidoUseCase.actualizar(5, datos)).thenReturn(actualizado);

        service.actualizar(5, datos, List.of());

        verify(compraPedidoItemService).reemplazarItems(eq(5), any());
        verifyNoInteractions(compraPedidoHistorialService);
    }

    @Test
    void enviarReservaElCorrelativoYFormateaElNumero() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5)
                .ejercicioId(7)
                .estado(CompraPedidoEstado.BORRADOR)
                .fecha(LocalDateTime.of(2026, 10, 6, 9, 0))
                .build();
        CompraPedido enviado = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.PENDIENTE_ENVIO)
                .build();
        when(getCompraPedidoByIdUseCase.getById(5)).thenReturn(Optional.of(pedido));
        when(compraPedidoSecuenciaService.reservarNumero(7, 2026)).thenReturn(1);
        when(enviarCompraPedidoUseCase.enviar(5, "PC-2026-000001")).thenReturn(enviado);

        service.enviar(5, 1);

        verify(compraPedidoSecuenciaService).reservarNumero(7, 2026);
        verify(enviarCompraPedidoUseCase).enviar(5, "PC-2026-000001");
        verify(compraPedidoHistorialService).registrar(5, "PENDIENTE_ENVIO", 1, null);
    }

    @Test
    void enviarUnRechazadoReutilizaElNumeroExistente() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5)
                .numero("PC-2026-000042")
                .ejercicioId(7)
                .estado(CompraPedidoEstado.RECHAZADO)
                .fecha(LocalDateTime.of(2026, 10, 6, 9, 0))
                .build();
        CompraPedido enviado = CompraPedido.builder()
                .compraPedidoId(5)
                .numero("PC-2026-000042")
                .estado(CompraPedidoEstado.PENDIENTE_ENVIO)
                .build();
        when(getCompraPedidoByIdUseCase.getById(5)).thenReturn(Optional.of(pedido));
        when(enviarCompraPedidoUseCase.enviar(5, "PC-2026-000042")).thenReturn(enviado);

        service.enviar(5, 1);

        verifyNoInteractions(compraPedidoSecuenciaService);
        verify(enviarCompraPedidoUseCase).enviar(5, "PC-2026-000042");
    }

    @Test
    void aprobarDelegaYRegistraElHistorial() {
        CompraPedido enviado = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.EN_REVISION_COMPRAS)
                .build();
        when(aprobarCompraPedidoUseCase.aprobar(5, 9)).thenReturn(enviado);

        assertThat(service.aprobar(5, 9)).isEqualTo(enviado);
        verify(compraPedidoHistorialService).registrar(5, "EN_REVISION_COMPRAS", 9, null);
    }

    @Test
    void rechazarDelegaYRegistraElMotivo() {
        CompraPedido rechazado = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.RECHAZADO)
                .build();
        when(rechazarCompraPedidoUseCase.rechazar(5, 9, "Falta cotización")).thenReturn(rechazado);

        assertThat(service.rechazar(5, 9, "Falta cotización")).isEqualTo(rechazado);
        verify(compraPedidoHistorialService).registrar(5, "RECHAZADO", 9, "Falta cotización");
    }

    @Test
    void descartarDelegaYRegistraElHistorial() {
        CompraPedido descartado = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.DESCARTADO)
                .build();
        when(descartarCompraPedidoUseCase.descartar(5, "No hace falta")).thenReturn(descartado);

        assertThat(service.descartar(5, 1, "No hace falta")).isEqualTo(descartado);
        verify(compraPedidoHistorialService).registrar(5, "DESCARTADO", 1, "No hace falta");
    }

    @Test
    void getByIdYGetByNumeroDeleganEnSusCasosDeUso() {
        CompraPedido pedido = CompraPedido.builder().compraPedidoId(5).build();
        when(getCompraPedidoByIdUseCase.getById(5)).thenReturn(Optional.of(pedido));
        when(getCompraPedidoByNumeroUseCase.getByNumero("PC-2026-000001")).thenReturn(Optional.of(pedido));

        assertThat(service.getById(5)).contains(pedido);
        assertThat(service.getByNumero("PC-2026-000001")).contains(pedido);
    }

    @Test
    void estimarDelegaYRegistraElHistorial() {
        CompraPedido estimado = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.PENDIENTE_AUTORIZACION_PRESUPUESTO)
                .build();
        when(estimarCompraPedidoUseCase.estimar(5, new BigDecimal("100"), "fuente")).thenReturn(estimado);

        assertThat(service.estimar(5, new BigDecimal("100"), "fuente", 1)).isEqualTo(estimado);
        verify(compraPedidoHistorialService).registrar(5, "PENDIENTE_AUTORIZACION_PRESUPUESTO", 1, null);
    }

    @Test
    void autorizarPresupuestoDelegaYRegistraElHistorial() {
        CompraPedido autorizado = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.AUTORIZADO_PRESUPUESTO)
                .build();
        when(autorizarPresupuestoCompraPedidoUseCase.autorizar(5)).thenReturn(autorizado);

        assertThat(service.autorizarPresupuesto(5, 9)).isEqualTo(autorizado);
        verify(compraPedidoHistorialService).registrar(5, "AUTORIZADO_PRESUPUESTO", 9, null);
    }

    @Test
    void rechazarPresupuestoDelegaYRegistraElMotivo() {
        CompraPedido rechazado = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.RECHAZADO)
                .build();
        when(rechazarPresupuestoCompraPedidoUseCase.rechazar(5, "fuera de política")).thenReturn(rechazado);

        assertThat(service.rechazarPresupuesto(5, 9, "fuera de política")).isEqualTo(rechazado);
        verify(compraPedidoHistorialService).registrar(5, "RECHAZADO", 9, "fuera de política");
    }

}

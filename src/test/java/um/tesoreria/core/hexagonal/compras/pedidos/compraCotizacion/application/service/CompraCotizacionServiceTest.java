package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacionDecision;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.*;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraAdjudicacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionSecuenciaPort;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.service.CompraCotizacionTramoService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.service.CompraPedidoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraCotizacionServiceTest {

    @Mock private CreateCompraCotizacionUseCase createCompraCotizacionUseCase;
    @Mock private UpdateCompraCotizacionUseCase updateCompraCotizacionUseCase;
    @Mock private GetCompraCotizacionByIdUseCase getCompraCotizacionByIdUseCase;
    @Mock private ListCompraCotizacionesUseCase listCompraCotizacionesUseCase;
    @Mock private EnviarCompraCotizacionUseCase enviarCompraCotizacionUseCase;
    @Mock private AnularCompraCotizacionUseCase anularCompraCotizacionUseCase;
    @Mock private ListPedidoItemsPendientesUseCase listPedidoItemsPendientesUseCase;
    @Mock private CompraCotizacionSecuenciaPort compraCotizacionSecuenciaPort;
    @Mock private CompraCotizacionRepository compraCotizacionRepository;
    @Mock private CompraPedidoService compraPedidoService;
    @Mock private CompraCotizacionTramoService compraCotizacionTramoService;
    @Mock private RegistrarRespuestaCompraCotizacionUseCase registrarRespuestaCompraCotizacionUseCase;
    @Mock private MarcarNoCotizaCompraCotizacionUseCase marcarNoCotizaCompraCotizacionUseCase;
    @Mock private RecomendarCompraCotizacionUseCase recomendarCompraCotizacionUseCase;
    @Mock private CerrarCompraCotizacionUseCase cerrarCompraCotizacionUseCase;
    @Mock private AdjudicarCompraCotizacionUseCase adjudicarCompraCotizacionUseCase;
    @Mock private FormalizarCompraCotizacionUseCase formalizarCompraCotizacionUseCase;
    @Mock private CompraAdjudicacionRepository compraAdjudicacionRepository;

    private CompraCotizacionService service;

    @BeforeEach
    void setUp() {
        service = new CompraCotizacionService(createCompraCotizacionUseCase, updateCompraCotizacionUseCase,
                getCompraCotizacionByIdUseCase, listCompraCotizacionesUseCase, enviarCompraCotizacionUseCase,
                anularCompraCotizacionUseCase, listPedidoItemsPendientesUseCase, compraCotizacionSecuenciaPort,
                compraCotizacionRepository, compraPedidoService, compraCotizacionTramoService,
                registrarRespuestaCompraCotizacionUseCase, marcarNoCotizaCompraCotizacionUseCase,
                recomendarCompraCotizacionUseCase, cerrarCompraCotizacionUseCase,
                adjudicarCompraCotizacionUseCase, formalizarCompraCotizacionUseCase, compraAdjudicacionRepository);
    }

    private CompraCotizacion borrador(Long id, String numero) {
        return CompraCotizacion.builder()
                .compraCotizacionId(id)
                .numero(numero)
                .ejercicioId(15)
                .fecha(LocalDateTime.of(2026, 10, 11, 9, 0))
                .estado(CompraCotizacionEstado.BORRADOR)
                .build();
    }

    @Test
    void enviarReservaNumeroYMarcaPedidosCubiertos() {
        CompraCotizacion proceso = borrador(1L, null);
        CompraCotizacion enviado = borrador(1L, "SC-2026-000007");
        enviado.setEstado(CompraCotizacionEstado.ENVIADA);
        when(getCompraCotizacionByIdUseCase.getById(1L)).thenReturn(Optional.of(proceso));
        when(compraCotizacionSecuenciaPort.reservarNumero(15, 2026)).thenReturn(7);
        when(enviarCompraCotizacionUseCase.enviar(1L, "SC-2026-000007")).thenReturn(enviado);
        when(compraCotizacionRepository.findPedidoIdsByProceso(1L)).thenReturn(List.of(2, 3));
        when(compraCotizacionRepository.todosLosItemsEnProcesosEnviados(2)).thenReturn(true);
        when(compraCotizacionRepository.todosLosItemsEnProcesosEnviados(3)).thenReturn(false);

        CompraCotizacion resultado = service.enviar(1L);

        assertThat(resultado.getNumero()).isEqualTo("SC-2026-000007");
        verify(enviarCompraCotizacionUseCase).enviar(1L, "SC-2026-000007");
        verify(compraPedidoService).marcarEnCotizacion(2);
        verify(compraPedidoService, never()).marcarEnCotizacion(3);
    }

    @Test
    void enviarNoReservaSiYaTieneNumero() {
        CompraCotizacion proceso = borrador(1L, "SC-2026-000001");
        when(getCompraCotizacionByIdUseCase.getById(1L)).thenReturn(Optional.of(proceso));
        when(enviarCompraCotizacionUseCase.enviar(1L, "SC-2026-000001")).thenReturn(proceso);
        when(compraCotizacionRepository.findPedidoIdsByProceso(1L)).thenReturn(List.of());

        service.enviar(1L);

        verify(compraCotizacionSecuenciaPort, never()).reservarNumero(any(), any());
    }

    @Test
    void enviarInexistenteFalla() {
        when(getCompraCotizacionByIdUseCase.getById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.enviar(99L)).isInstanceOf(CompraCotizacionException.class);
    }

    @Test
    void crearCalculaMontoYMinimoPorLaSumaDePedidos() {
        CompraCotizacion datos = borrador(null, null);
        datos.setItems(List.of(um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionItem
                .builder().compraPedidoItemId(100).orden(1).cantidad(java.math.BigDecimal.ONE).build()));
        when(compraCotizacionRepository.montoEstimadoDeItems(any())).thenReturn(new BigDecimal("900000.00"));
        when(compraCotizacionTramoService.resolverMinimo(15, new BigDecimal("900000.00"))).thenReturn(Optional.of(3));
        when(createCompraCotizacionUseCase.crear(any())).thenAnswer(i -> i.getArgument(0));

        CompraCotizacion creado = service.crear(datos);

        assertThat(creado.getMontoEstimado()).isEqualByComparingTo("900000.00");
        assertThat(creado.getCantidadMinima()).isEqualTo(3);
    }

    @Test
    void delegaciones() {
        when(createCompraCotizacionUseCase.crear(any())).thenReturn(borrador(1L, null));
        assertThat(service.crear(borrador(null, null)).getCompraCotizacionId()).isEqualTo(1L);

        when(updateCompraCotizacionUseCase.actualizar(eq(1L), any())).thenReturn(borrador(1L, null));
        assertThat(service.actualizar(1L, borrador(1L, null))).isNotNull();

        when(getCompraCotizacionByIdUseCase.getById(1L)).thenReturn(Optional.of(borrador(1L, null)));
        assertThat(service.getById(1L)).isPresent();

        when(listCompraCotizacionesUseCase.listar(any())).thenReturn(List.of(borrador(1L, null)));
        assertThat(service.listar(new CompraCotizacionCriteria(null, null, null, null))).hasSize(1);

        when(listPedidoItemsPendientesUseCase.listar(15, null)).thenReturn(List.of());
        assertThat(service.pendientes(15, null)).isEmpty();

        when(anularCompraCotizacionUseCase.anular(1L, "x")).thenReturn(borrador(1L, null));
        assertThat(service.anular(1L, "x")).isNotNull();
    }

    @Test
    void respuestasPendientesAplanaSoloDocumentosEnviados() {
        CompraCotizacion proceso = borrador(1L, "SC-2026-000001");
        proceso.setItems(List.of(CompraCotizacionItem.builder().compraCotizacionItemId(9L)
                .compraPedidoItemId(100).orden(1).cantidad(new BigDecimal("3.00")).build()));
        proceso.setProveedores(List.of(
                CompraCotizacionProveedor.builder().compraCotizacionProveedorId(5L).proveedorId(7)
                        .estado(CompraCotizacionProveedorEstado.RESPONDIDA).build(),
                CompraCotizacionProveedor.builder().compraCotizacionProveedorId(6L).proveedorId(8)
                        .estado(CompraCotizacionProveedorEstado.ENVIADA).build()));
        when(listCompraCotizacionesUseCase.listar(any())).thenReturn(List.of(proceso));

        var pendientes = service.respuestasPendientes(15);

        assertThat(pendientes).hasSize(1);
        assertThat(pendientes.get(0).compraCotizacionId()).isEqualTo(1L);
        assertThat(pendientes.get(0).numero()).isEqualTo("SC-2026-000001");
        assertThat(pendientes.get(0).proveedor().getCompraCotizacionProveedorId()).isEqualTo(6L);
        assertThat(pendientes.get(0).items()).hasSize(1);
    }

    @Test
    void recomendarTransicionaLosPedidosAdjudicacion() {
        CompraCotizacion recomendado = borrador(1L, "SC-1");
        recomendado.setEstado(CompraCotizacionEstado.RECOMENDADA);
        when(recomendarCompraCotizacionUseCase.recomendar(1L, 5L, "mejor")).thenReturn(recomendado);
        when(compraCotizacionRepository.findPedidoIdsByProceso(1L)).thenReturn(List.of(2, 3));

        assertThat(service.recomendar(1L, 5L, "mejor")).isEqualTo(recomendado);
        verify(compraPedidoService).marcarPendienteAdjudicacion(2);
        verify(compraPedidoService).marcarPendienteAdjudicacion(3);
    }

    @Test
    void etapa6CDelega() {
        when(registrarRespuestaCompraCotizacionUseCase.registrarRespuesta(eq(1L), eq(5L), any()))
                .thenReturn(CompraCotizacionProveedor.builder().build());
        assertThat(service.registrarRespuesta(1L, 5L, CompraCotizacionProveedor.builder().build())).isNotNull();

        when(marcarNoCotizaCompraCotizacionUseCase.marcarNoCotiza(1L, 5L, "x"))
                .thenReturn(CompraCotizacionProveedor.builder().build());
        assertThat(service.marcarNoCotiza(1L, 5L, "x")).isNotNull();

        when(getCompraCotizacionByIdUseCase.getById(1L)).thenReturn(Optional.of(borrador(1L, null)));
        assertThat(service.comparacion(1L)).isNotNull();

        when(cerrarCompraCotizacionUseCase.cerrar(1L)).thenReturn(borrador(1L, null));
        assertThat(service.cerrar(1L)).isNotNull();
    }

    @Test
    void adjudicarTransicionaLosPedidos() {
        CompraAdjudicacion adjudicacion = CompraAdjudicacion.builder()
                .compraAdjudicacionId(1L).compraCotizacionId(1L)
                .decision(CompraAdjudicacionDecision.ADJUDICADA).build();
        when(adjudicarCompraCotizacionUseCase.adjudicar(eq(1L), eq(CompraAdjudicacionDecision.ADJUDICADA), eq(5L),
                eq(7), any(), any(), eq(38L), any())).thenReturn(adjudicacion);
        when(compraCotizacionRepository.findPedidoIdsByProceso(1L)).thenReturn(List.of(2, 3));

        CompraAdjudicacion resultado = service.adjudicar(1L, CompraAdjudicacionDecision.ADJUDICADA, 5L, 7,
                new BigDecimal("28000.00"), "menor total", 38L, new BigDecimal("300000.00"));

        assertThat(resultado).isEqualTo(adjudicacion);
        verify(compraPedidoService).marcarAdjudicado(2);
        verify(compraPedidoService).marcarAdjudicado(3);
    }

    @Test
    void devolverNoTransicionaPedidos() {
        when(adjudicarCompraCotizacionUseCase.adjudicar(eq(1L), eq(CompraAdjudicacionDecision.DEVUELTA_A_COMPRAS),
                any(), any(), any(), any(), any(), any())).thenReturn(CompraAdjudicacion.builder()
                        .decision(CompraAdjudicacionDecision.DEVUELTA_A_COMPRAS).build());

        service.adjudicar(1L, CompraAdjudicacionDecision.DEVUELTA_A_COMPRAS, null, null, null, "revisar", 38L, null);

        verify(compraPedidoService, never()).marcarAdjudicado(any());
    }

    @Test
    void adjudicacionDelega() {
        when(compraAdjudicacionRepository.findByCompraCotizacionId(1L))
                .thenReturn(Optional.of(CompraAdjudicacion.builder().build()));
        assertThat(service.adjudicacion(1L)).isPresent();
    }

    @Test
    void formalizarTransicionaLosPedidosCubiertos() {
        CompraCotizacion formalizado = borrador(1L, "SC-1");
        formalizado.setEstado(CompraCotizacionEstado.FORMALIZADA);
        when(formalizarCompraCotizacionUseCase.formalizar(1L)).thenReturn(formalizado);
        when(compraCotizacionRepository.findPedidoIdsByProceso(1L)).thenReturn(List.of(2, 3));

        assertThat(service.formalizar(1L).getEstado()).isEqualTo(CompraCotizacionEstado.FORMALIZADA);
        verify(compraPedidoService).marcarFormalizado(2);
        verify(compraPedidoService).marcarFormalizado(3);
    }

    @Test
    void pedidoIdsByProcesoDelega() {
        when(compraCotizacionRepository.findPedidoIdsByProceso(1L)).thenReturn(List.of(2));
        assertThat(service.pedidoIdsByProceso(1L)).containsExactly(2);
    }
}

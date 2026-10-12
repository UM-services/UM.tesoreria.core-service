package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in.CreateCompraFormalizacionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in.GetCompraFormalizacionByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in.ListCompraFormalizacionesUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out.CompraFormalizacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out.CompraFormalizacionSecuenciaPort;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.service.CompraPedidoItemService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraFormalizacionServiceTest {

    private static final long COTIZACION_ID = 1L;
    private static final long ADJUDICACION_ID = 9L;

    @Mock private CreateCompraFormalizacionUseCase createCompraFormalizacionUseCase;
    @Mock private GetCompraFormalizacionByIdUseCase getCompraFormalizacionByIdUseCase;
    @Mock private ListCompraFormalizacionesUseCase listCompraFormalizacionesUseCase;
    @Mock private CompraFormalizacionRepository compraFormalizacionRepository;
    @Mock private CompraFormalizacionSecuenciaPort compraFormalizacionSecuenciaPort;
    @Mock private CompraCotizacionService compraCotizacionService;
    @Mock private CompraPedidoItemService compraPedidoItemService;

    private CompraFormalizacionService service;

    @BeforeEach
    void setUp() {
        service = new CompraFormalizacionService(createCompraFormalizacionUseCase,
                getCompraFormalizacionByIdUseCase, listCompraFormalizacionesUseCase, compraFormalizacionRepository,
                compraFormalizacionSecuenciaPort, compraCotizacionService, compraPedidoItemService);
    }

    private CompraAdjudicacion adjudicacion(CompraAdjudicacionDecision decision) {
        return CompraAdjudicacion.builder()
                .compraAdjudicacionId(ADJUDICACION_ID)
                .compraCotizacionId(COTIZACION_ID)
                .ejercicioId(15)
                .compraCotizacionProveedorId(5L)
                .proveedorAdjudicadoId(7)
                .monto(new BigDecimal("28000.00"))
                .decision(decision)
                .build();
    }

    private CompraCotizacion proceso(CompraCotizacionEstado estado) {
        return CompraCotizacion.builder()
                .compraCotizacionId(COTIZACION_ID)
                .numero("SC-2026-000001")
                .ejercicioId(15)
                .fecha(LocalDateTime.of(2026, 10, 11, 9, 0))
                .estado(estado)
                .items(List.of(CompraCotizacionItem.builder().compraCotizacionItemId(9L)
                        .compraPedidoItemId(100).orden(1).cantidad(new BigDecimal("3.00")).build()))
                .proveedores(List.of(CompraCotizacionProveedor.builder()
                        .compraCotizacionProveedorId(5L).proveedorId(7).moneda("ARS")
                        .condicionPago("30 días").plazoEntrega("48 horas").garantia("12 meses")
                        .items(List.of(CompraCotizacionProveedorItem.builder().compraCotizacionItemId(9L)
                                .precioUnitario(new BigDecimal("9000.00")).subtotal(new BigDecimal("27000.00"))
                                .marca("ACME").build()))
                        .build()))
                .build();
    }

    @Test
    void formalizarSnapshoteaCondicionesEItemsYTransiciona() {
        when(compraCotizacionService.adjudicacion(COTIZACION_ID))
                .thenReturn(Optional.of(adjudicacion(CompraAdjudicacionDecision.ADJUDICADA)));
        when(compraFormalizacionRepository.findByCompraAdjudicacionId(ADJUDICACION_ID)).thenReturn(Optional.empty());
        when(compraCotizacionService.getById(COTIZACION_ID))
                .thenReturn(Optional.of(proceso(CompraCotizacionEstado.ADJUDICADA)));
        when(compraCotizacionService.pedidoIdsByProceso(COTIZACION_ID)).thenReturn(List.of(2));
        when(compraPedidoItemService.getByPedido(2)).thenReturn(List.of(CompraPedidoItem.builder()
                .compraPedidoItemId(100).compraPedidoId(2).orden(1).unidad("Unidad")
                .descripcion("Notebook").especificaciones("<p>16 GB</p>").build()));
        when(compraFormalizacionSecuenciaPort.reservarNumero(15, 2026)).thenReturn(3);
        when(createCompraFormalizacionUseCase.crear(any())).thenAnswer(i -> i.getArgument(0));

        CompraFormalizacion datos = CompraFormalizacion.builder().numeroExterno("OC-EXT-1").objeto("<p>Objeto</p>").build();
        CompraFormalizacion creada = service.formalizar(COTIZACION_ID, datos, 38);

        assertThat(creada.getNumero()).isEqualTo("OC-2026-000003");
        assertThat(creada.getNumeroExterno()).isEqualTo("OC-EXT-1");
        assertThat(creada.getCompraAdjudicacionId()).isEqualTo(ADJUDICACION_ID);
        assertThat(creada.getProveedorId()).isEqualTo(7);
        assertThat(creada.getMonto()).isEqualByComparingTo("28000.00");
        assertThat(creada.getCondicionPago()).isEqualTo("30 días");
        assertThat(creada.getPlazoEntrega()).isEqualTo("48 horas");
        assertThat(creada.getGarantia()).isEqualTo("12 meses");
        assertThat(creada.getUsuarioId()).isEqualTo(38);
        assertThat(creada.getItems()).hasSize(1);
        CompraFormalizacionItem item = creada.getItems().get(0);
        assertThat(item.getOrden()).isEqualTo(1);
        assertThat(item.getCantidad()).isEqualByComparingTo("3.00");
        assertThat(item.getDescripcion()).isEqualTo("Notebook");
        assertThat(item.getEspecificaciones()).isEqualTo("<p>16 GB</p>");
        assertThat(item.getPrecioUnitario()).isEqualByComparingTo("9000.00");
        assertThat(item.getSubtotal()).isEqualByComparingTo("27000.00");
        assertThat(item.getMarca()).isEqualTo("ACME");
        verify(compraCotizacionService).formalizar(COTIZACION_ID);
    }

    @Test
    void formalizarEsIdempotenteSiYaExisteLaOc() {
        CompraFormalizacion existente = CompraFormalizacion.builder().compraFormalizacionId(50L).build();
        when(compraCotizacionService.adjudicacion(COTIZACION_ID))
                .thenReturn(Optional.of(adjudicacion(CompraAdjudicacionDecision.ADJUDICADA)));
        when(compraFormalizacionRepository.findByCompraAdjudicacionId(ADJUDICACION_ID))
                .thenReturn(Optional.of(existente));

        assertThat(service.formalizar(COTIZACION_ID, null, 38)).isSameAs(existente);
        verify(createCompraFormalizacionUseCase, never()).crear(any());
        verify(compraCotizacionService, never()).formalizar(any());
    }

    @Test
    void formalizarSinAdjudicacionFalla() {
        when(compraCotizacionService.adjudicacion(COTIZACION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.formalizar(COTIZACION_ID, null, 38))
                .isInstanceOf(CompraFormalizacionException.class);
    }

    @Test
    void formalizarFallaSiLaOfertaAdjudicadaNoPertenece() {
        when(compraCotizacionService.adjudicacion(COTIZACION_ID))
                .thenReturn(Optional.of(adjudicacion(CompraAdjudicacionDecision.ADJUDICADA)));
        when(compraFormalizacionRepository.findByCompraAdjudicacionId(ADJUDICACION_ID)).thenReturn(Optional.empty());
        CompraCotizacion sinOferta = proceso(CompraCotizacionEstado.ADJUDICADA);
        sinOferta.setProveedores(List.of());
        when(compraCotizacionService.getById(COTIZACION_ID)).thenReturn(Optional.of(sinOferta));

        assertThatThrownBy(() -> service.formalizar(COTIZACION_ID, null, 38))
                .isInstanceOf(CompraFormalizacionException.class);
    }

    @Test
    void pendientesListaSoloAdjudicadasSinOc() {
        when(compraCotizacionService.listar(any(CompraCotizacionCriteria.class)))
                .thenReturn(List.of(proceso(CompraCotizacionEstado.ADJUDICADA)));
        when(compraCotizacionService.adjudicacion(COTIZACION_ID))
                .thenReturn(Optional.of(adjudicacion(CompraAdjudicacionDecision.ADJUDICADA)));
        when(compraFormalizacionRepository.existsByCompraAdjudicacionId(ADJUDICACION_ID)).thenReturn(false);
        when(compraCotizacionService.pedidoIdsByProceso(COTIZACION_ID)).thenReturn(List.of(2));
        when(compraPedidoItemService.getByPedido(2)).thenReturn(List.of(CompraPedidoItem.builder()
                .compraPedidoItemId(100).compraPedidoId(2).orden(1).descripcion("Notebook").build()));

        var pendientes = service.pendientes(15);

        assertThat(pendientes).hasSize(1);
        assertThat(pendientes.get(0).compraCotizacionId()).isEqualTo(COTIZACION_ID);
        assertThat(pendientes.get(0).compraAdjudicacionId()).isEqualTo(ADJUDICACION_ID);
        assertThat(pendientes.get(0).proveedorId()).isEqualTo(7);
        assertThat(pendientes.get(0).items()).hasSize(1);
    }

    @Test
    void pendientesExcluyeLasYaFormalizadas() {
        when(compraCotizacionService.listar(any(CompraCotizacionCriteria.class)))
                .thenReturn(List.of(proceso(CompraCotizacionEstado.ADJUDICADA)));
        when(compraCotizacionService.adjudicacion(COTIZACION_ID))
                .thenReturn(Optional.of(adjudicacion(CompraAdjudicacionDecision.ADJUDICADA)));
        when(compraFormalizacionRepository.existsByCompraAdjudicacionId(ADJUDICACION_ID)).thenReturn(true);

        assertThat(service.pendientes(15)).isEmpty();
    }

    @Test
    void porCotizacionDevuelveLaOcDeLaAdjudicacion() {
        CompraFormalizacion oc = CompraFormalizacion.builder().compraFormalizacionId(50L).build();
        when(compraCotizacionService.adjudicacion(COTIZACION_ID))
                .thenReturn(Optional.of(adjudicacion(CompraAdjudicacionDecision.ADJUDICADA)));
        when(compraFormalizacionRepository.findByCompraAdjudicacionId(ADJUDICACION_ID)).thenReturn(Optional.of(oc));

        assertThat(service.porCotizacion(COTIZACION_ID)).contains(oc);
    }

    @Test
    void delegaciones() {
        CompraFormalizacion oc = CompraFormalizacion.builder().compraFormalizacionId(50L).build();
        when(getCompraFormalizacionByIdUseCase.getById(50L)).thenReturn(Optional.of(oc));
        assertThat(service.getById(50L)).contains(oc);

        when(listCompraFormalizacionesUseCase.listar(any())).thenReturn(List.of(oc));
        assertThat(service.listar(new um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model
                .CompraFormalizacionCriteria(15, null, null, null))).hasSize(1);
    }
}

package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.service.CompraFormalizacionService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.service.CompraPedidoService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.exception.CompraRecepcionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPatrimonio;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPendiente;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraRecepcionServiceTest {

    private static final Long FORMALIZACION_ID = 50L;

    @Mock private CreateCompraRecepcionUseCase createCompraRecepcionUseCase;
    @Mock private GetCompraRecepcionByIdUseCase getCompraRecepcionByIdUseCase;
    @Mock private ListCompraRecepcionesUseCase listCompraRecepcionesUseCase;
    @Mock private CompraRecepcionRepository compraRecepcionRepository;
    @Mock private CompraRecepcionSecuenciaPort compraRecepcionSecuenciaPort;
    @Mock private CompraRecepcionPedidoPort compraRecepcionPedidoPort;
    @Mock private CompraFormalizacionService compraFormalizacionService;
    @Mock private CompraPedidoService compraPedidoService;

    private CompraRecepcionService service;

    @BeforeEach
    void setUp() {
        service = new CompraRecepcionService(createCompraRecepcionUseCase, getCompraRecepcionByIdUseCase,
                listCompraRecepcionesUseCase, compraRecepcionRepository, compraRecepcionSecuenciaPort,
                compraRecepcionPedidoPort, compraFormalizacionService, compraPedidoService);
    }

    // ---- Fixtures ----

    private CompraFormalizacion oc() {
        return CompraFormalizacion.builder()
                .compraFormalizacionId(FORMALIZACION_ID)
                .numero("OC-2026-000003")
                .ejercicioId(15)
                .fecha(LocalDateTime.of(2026, 10, 12, 9, 0))
                .proveedorId(7)
                .monto(new BigDecimal("28000.00"))
                .moneda("ARS")
                .items(List.of(
                        CompraFormalizacionItem.builder().compraFormalizacionItemId(100L)
                                .compraPedidoItemId(1000).orden(1).cantidad(new BigDecimal("3.00"))
                                .unidad("Unidad").descripcion("Notebook").build(),
                        CompraFormalizacionItem.builder().compraFormalizacionItemId(101L)
                                .compraPedidoItemId(1001).orden(2).cantidad(new BigDecimal("2.00"))
                                .unidad("Unidad").descripcion("Mouse").build()))
                .build();
    }

    private CompraRecepcionItem item(Long formalizacionItemId, String cantidad, CompraRecepcionResultado resultado) {
        return CompraRecepcionItem.builder()
                .compraFormalizacionItemId(formalizacionItemId)
                .cantidadRecibida(new BigDecimal(cantidad))
                .resultado(resultado)
                .build();
    }

    private CompraRecepcion datos(CompraRecepcionItem... items) {
        return CompraRecepcion.builder()
                .remito("R-0001-004582")
                .items(new ArrayList<>(List.of(items)))
                .build();
    }

    private CompraRecepcion recepcionRegistrada(CompraRecepcionItem... items) {
        return CompraRecepcion.builder().numero("RC-2026-000001")
                .items(List.of(items)).build();
    }

    // ---- registrar ----

    @Test
    void registrarValidaReservaElNumeroYDerivaElResultado() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID)).thenReturn(List.of());
        when(compraRecepcionSecuenciaPort.reservarNumero(15, 2026)).thenReturn(4);
        when(createCompraRecepcionUseCase.crear(any())).thenAnswer(inv -> inv.getArgument(0));
        when(compraRecepcionPedidoPort.pedidoIdsByFormalizacionItem(FORMALIZACION_ID))
                .thenReturn(Map.of(100L, 2, 101L, 2));

        CompraRecepcion creada = service.registrar(FORMALIZACION_ID,
                datos(item(100L, "2.00", null), item(101L, "2.00", CompraRecepcionResultado.CON_OBSERVACIONES)), 38);

        assertThat(creada.getNumero()).isEqualTo("RC-2026-000004");
        assertThat(creada.getEjercicioId()).isEqualTo(15);
        assertThat(creada.getRemito()).isEqualTo("R-0001-004582");
        assertThat(creada.getUsuarioId()).isEqualTo(38);
        assertThat(creada.getFecha()).isNotNull();
        assertThat(creada.getPatrimonio()).isEqualTo(CompraRecepcionPatrimonio.NO_CORRESPONDE);
        assertThat(creada.getResultado()).isEqualTo(CompraRecepcionResultado.CON_OBSERVACIONES);
        assertThat(creada.getItems()).extracting(CompraRecepcionItem::getOrden).containsExactly(1, 2);
        assertThat(creada.getItems().get(0).getResultado()).isEqualTo(CompraRecepcionResultado.CONFORME);
        verify(compraPedidoService).marcarRecepcionParcial(2);
        verify(compraPedidoService, never()).marcarRecepcionado(any(), any(), any());
    }

    @Test
    void registrarRespetaLaFechaYElPatrimonioInformados() {
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 13, 8, 30);
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID)).thenReturn(List.of());
        when(compraRecepcionSecuenciaPort.reservarNumero(15, 2026)).thenReturn(1);
        when(createCompraRecepcionUseCase.crear(any())).thenAnswer(inv -> inv.getArgument(0));
        when(compraRecepcionPedidoPort.pedidoIdsByFormalizacionItem(FORMALIZACION_ID)).thenReturn(Map.of());
        CompraRecepcion datos = datos(item(100L, "1.00", null));
        datos.setFecha(fecha);
        datos.setPatrimonio(CompraRecepcionPatrimonio.INTERVENIDO);

        CompraRecepcion creada = service.registrar(FORMALIZACION_ID, datos, 38);

        assertThat(creada.getFecha()).isEqualTo(fecha);
        assertThat(creada.getPatrimonio()).isEqualTo(CompraRecepcionPatrimonio.INTERVENIDO);
        assertThat(creada.getNumero()).isEqualTo("RC-2026-000001");
    }

    @Test
    void registrarCompletoAvanzaElPedidoARecepcionado() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID))
                .thenReturn(List.of())
                .thenReturn(List.of(recepcionRegistrada(item(100L, "3.00", CompraRecepcionResultado.CONFORME),
                        item(101L, "2.00", CompraRecepcionResultado.CONFORME))));
        when(compraRecepcionSecuenciaPort.reservarNumero(15, 2026)).thenReturn(5);
        when(createCompraRecepcionUseCase.crear(any())).thenAnswer(inv -> inv.getArgument(0));
        when(compraRecepcionPedidoPort.pedidoIdsByFormalizacionItem(FORMALIZACION_ID))
                .thenReturn(Map.of(100L, 2, 101L, 2));

        service.registrar(FORMALIZACION_ID, datos(item(100L, "3.00", null), item(101L, "2.00", null)), 38);

        verify(compraPedidoService).marcarRecepcionado(2, 38, null);
        verify(compraPedidoService, never()).marcarRecepcionParcial(any());
    }

    @Test
    void loNoConformeNoCubreElSaldoYDejaElPedidoParcial() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID))
                .thenReturn(List.of())
                .thenReturn(List.of(recepcionRegistrada(item(100L, "3.00", CompraRecepcionResultado.NO_CONFORME),
                        item(101L, "2.00", CompraRecepcionResultado.CONFORME))));
        when(compraRecepcionSecuenciaPort.reservarNumero(15, 2026)).thenReturn(6);
        when(createCompraRecepcionUseCase.crear(any())).thenAnswer(inv -> inv.getArgument(0));
        when(compraRecepcionPedidoPort.pedidoIdsByFormalizacionItem(FORMALIZACION_ID))
                .thenReturn(Map.of(100L, 2, 101L, 2));

        service.registrar(FORMALIZACION_ID, datos(item(100L, "3.00", CompraRecepcionResultado.NO_CONFORME),
                item(101L, "2.00", null)), 38);

        verify(compraPedidoService).marcarRecepcionParcial(2);
        verify(compraPedidoService, never()).marcarRecepcionado(any(), any(), any());
    }

    @Test
    void registrarNoAvanzaPedidosSiNoHayTrazabilidad() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID)).thenReturn(List.of());
        when(compraRecepcionSecuenciaPort.reservarNumero(15, 2026)).thenReturn(7);
        when(createCompraRecepcionUseCase.crear(any())).thenAnswer(inv -> inv.getArgument(0));
        when(compraRecepcionPedidoPort.pedidoIdsByFormalizacionItem(FORMALIZACION_ID)).thenReturn(Map.of());

        service.registrar(FORMALIZACION_ID, datos(item(100L, "1.00", null)), 38);

        verifyNoInteractions(compraPedidoService);
    }

    @Test
    void registrarFallaSiNoExisteLaOrdenDeCompra() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrar(FORMALIZACION_ID, datos(item(100L, "1.00", null)), 38))
                .isInstanceOf(CompraRecepcionException.class)
                .hasMessageContaining("No existe la Orden de Compra");
    }

    @Test
    void registrarFallaSinItems() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));

        assertThatThrownBy(() -> service.registrar(FORMALIZACION_ID, CompraRecepcion.builder().build(), 38))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("al menos un ítem");
    }

    @Test
    void registrarFallaSiElItemNoPerteneceALaOrdenDeCompra() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> service.registrar(FORMALIZACION_ID, datos(item(999L, "1.00", null)), 38))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no pertenece");
    }

    @Test
    void registrarFallaSiElItemEstaRepetido() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> service.registrar(FORMALIZACION_ID,
                datos(item(100L, "1.00", null), item(100L, "1.00", null)), 38))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("repetido");
    }

    @Test
    void registrarFallaSiLaCantidadEsNegativa() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> service.registrar(FORMALIZACION_ID, datos(item(100L, "-1.00", null)), 38))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor o igual a cero");
    }

    @Test
    void registrarFallaSiLaCantidadSuperaElSaldo() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID))
                .thenReturn(List.of(recepcionRegistrada(item(100L, "2.00", CompraRecepcionResultado.CONFORME))));

        assertThatThrownBy(() -> service.registrar(FORMALIZACION_ID, datos(item(100L, "2.00", null)), 38))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("supera el saldo pendiente");
    }

    @Test
    void registrarFallaSinCantidadesMayoresACero() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> service.registrar(FORMALIZACION_ID,
                datos(item(100L, "0.00", null), item(101L, "0.00", null)), 38))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("al menos una cantidad mayor a cero");
    }

    // ---- cerrar ----

    @Test
    void cerrarRegistraElMotivoEnElHistorialDeLosPedidos() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionPedidoPort.pedidoIdsByFormalizacionItem(FORMALIZACION_ID))
                .thenReturn(Map.of(100L, 2, 101L, 3));

        service.cerrar(FORMALIZACION_ID, "El proveedor no completará el resto", 38);

        verify(compraPedidoService).marcarRecepcionado(2, 38,
                "Cierre manual de recepción: El proveedor no completará el resto");
        verify(compraPedidoService).marcarRecepcionado(3, 38,
                "Cierre manual de recepción: El proveedor no completará el resto");
    }

    @Test
    void cerrarFallaSinMotivo() {
        assertThatThrownBy(() -> service.cerrar(FORMALIZACION_ID, "  ", 38))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("requiere un motivo");
        verifyNoInteractions(compraFormalizacionService);
    }

    @Test
    void cerrarFallaSiNoExisteLaOrdenDeCompra() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cerrar(FORMALIZACION_ID, "motivo", 38))
                .isInstanceOf(CompraRecepcionException.class);
    }

    @Test
    void cerrarFallaSiLaOrdenDeCompraNoTienePedidos() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionPedidoPort.pedidoIdsByFormalizacionItem(FORMALIZACION_ID)).thenReturn(Map.of());

        assertThatThrownBy(() -> service.cerrar(FORMALIZACION_ID, "motivo", 38))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no tiene pedidos asociados");
    }

    // ---- consultas ----

    @Test
    void pendientesListaLasOrdenesConSaldoYSuRecepcion() {
        when(compraFormalizacionService.listar(any(CompraFormalizacionCriteria.class))).thenReturn(List.of(oc()));
        when(compraRecepcionPedidoPort.pedidoIdsByFormalizacionItem(FORMALIZACION_ID))
                .thenReturn(Map.of(100L, 2, 101L, 2));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID))
                .thenReturn(List.of(recepcionRegistrada(item(100L, "1.00", CompraRecepcionResultado.CONFORME))));
        when(compraPedidoService.getById(2)).thenReturn(Optional.of(CompraPedido.builder()
                .compraPedidoId(2).estado(CompraPedidoEstado.RECEPCION_PARCIAL).build()));

        List<CompraRecepcionPendiente> pendientes = service.pendientes(15);

        assertThat(pendientes).hasSize(1);
        CompraRecepcionPendiente pendiente = pendientes.get(0);
        assertThat(pendiente.numero()).isEqualTo("OC-2026-000003");
        assertThat(pendiente.completo()).isFalse();
        assertThat(pendiente.items().get(0).recibido()).isEqualByComparingTo("1.00");
        assertThat(pendiente.items().get(0).saldo()).isEqualByComparingTo("2.00");
        assertThat(pendiente.items().get(1).recibido()).isEqualByComparingTo("0");
        assertThat(pendiente.items().get(1).saldo()).isEqualByComparingTo("2.00");
        assertThat(pendiente.recepciones()).hasSize(1);
    }

    @Test
    void pendientesExcluyeLasOrdenesYaRecepcionadas() {
        when(compraFormalizacionService.listar(any(CompraFormalizacionCriteria.class))).thenReturn(List.of(oc()));
        when(compraRecepcionPedidoPort.pedidoIdsByFormalizacionItem(FORMALIZACION_ID))
                .thenReturn(Map.of(100L, 2, 101L, 2));
        when(compraPedidoService.getById(2)).thenReturn(Optional.of(CompraPedido.builder()
                .compraPedidoId(2).estado(CompraPedidoEstado.RECEPCIONADO).build()));

        assertThat(service.pendientes(15)).isEmpty();
    }

    @Test
    void pendientesListaLaOcSinPedidosTrazables() {
        when(compraFormalizacionService.listar(any(CompraFormalizacionCriteria.class))).thenReturn(List.of(oc()));
        when(compraRecepcionPedidoPort.pedidoIdsByFormalizacionItem(FORMALIZACION_ID)).thenReturn(Map.of());
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID)).thenReturn(List.of());

        assertThat(service.pendientes(15)).hasSize(1);
    }

    @Test
    void porFormalizacionDevuelveSaldosOVacioSiNoExiste() {
        when(compraFormalizacionService.getById(FORMALIZACION_ID)).thenReturn(Optional.of(oc()));
        when(compraRecepcionRepository.findByCompraFormalizacionId(FORMALIZACION_ID)).thenReturn(List.of());
        when(compraFormalizacionService.getById(99L)).thenReturn(Optional.empty());

        assertThat(service.porFormalizacion(FORMALIZACION_ID)).isPresent();
        assertThat(service.porFormalizacion(FORMALIZACION_ID).orElseThrow().items()).hasSize(2);
        assertThat(service.porFormalizacion(99L)).isEmpty();
    }

    @Test
    void delegaciones() {
        CompraRecepcion recepcion = CompraRecepcion.builder().compraRecepcionId(1L).build();
        when(getCompraRecepcionByIdUseCase.getById(1L)).thenReturn(Optional.of(recepcion));
        assertThat(service.getById(1L)).contains(recepcion);

        when(listCompraRecepcionesUseCase.listar(any())).thenReturn(List.of(recepcion));
        assertThat(service.listar(new CompraRecepcionCriteria(15, FORMALIZACION_ID, null, null, null)))
                .hasSize(1);
    }

}

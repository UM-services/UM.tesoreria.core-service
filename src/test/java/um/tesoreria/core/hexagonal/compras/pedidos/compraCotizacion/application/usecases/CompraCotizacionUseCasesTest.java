package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.PedidoItemsPendientesPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraCotizacionUseCasesTest {

    @Mock private CompraCotizacionRepository repository;
    @Mock private PedidoItemsPendientesPort pendientesPort;

    private CompraCotizacion proceso() {
        CompraCotizacion proceso = CompraCotizacion.builder()
                .compraCotizacionId(1L)
                .ejercicioId(15)
                .estado(CompraCotizacionEstado.BORRADOR)
                .cantidadMinima(1)
                .build();
        proceso.setItems(List.of(CompraCotizacionItem.builder()
                .compraPedidoItemId(100).orden(1).cantidad(new BigDecimal("1.00")).build()));
        proceso.setProveedores(List.of(CompraCotizacionProveedor.builder().proveedorId(7).build()));
        return proceso;
    }

    @Test
    void crearFuerzaBorradorYSinNumero() {
        CompraCotizacion datos = CompraCotizacion.builder().ejercicioId(15)
                .estado(CompraCotizacionEstado.ENVIADA).numero("SC-1").build();
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        CompraCotizacion creado = new CreateCompraCotizacionUseCaseImpl(repository).crear(datos);

        assertThat(creado.getEstado()).isEqualTo(CompraCotizacionEstado.BORRADOR);
        assertThat(creado.getNumero()).isNull();
        assertThat(creado.getFecha()).isNotNull();
    }

    @Test
    void crearRequiereEjercicio() {
        assertThatThrownBy(() -> new CreateCompraCotizacionUseCaseImpl(repository)
                .crear(CompraCotizacion.builder().build()))
                .isInstanceOf(CompraCotizacionException.class);
    }

    @Test
    void actualizarSoloEnBorrador() {
        CompraCotizacion existente = proceso();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        CompraCotizacion datos = CompraCotizacion.builder().observacion("nueva").cantidadMinima(2).build();
        CompraCotizacion actualizado = new UpdateCompraCotizacionUseCaseImpl(repository).actualizar(1L, datos);

        assertThat(actualizado.getObservacion()).isEqualTo("nueva");
        assertThat(actualizado.getCantidadMinima()).isEqualTo(2);
    }

    @Test
    void actualizarEnviadoFalla() {
        CompraCotizacion existente = proceso();
        existente.enviar("SC-1");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> new UpdateCompraCotizacionUseCaseImpl(repository)
                .actualizar(1L, CompraCotizacion.builder().build()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getByIdDelega() {
        when(repository.findById(1L)).thenReturn(Optional.of(proceso()));
        assertThat(new GetCompraCotizacionByIdUseCaseImpl(repository).getById(1L)).isPresent();
    }

    @Test
    void listarDelega() {
        when(repository.findByCriteria(any())).thenReturn(List.of(proceso()));
        assertThat(new ListCompraCotizacionesUseCaseImpl(repository)
                .listar(new CompraCotizacionCriteria(null, null, null, null))).hasSize(1);
    }

    @Test
    void enviarFijaElNumero() {
        CompraCotizacion existente = proceso();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        CompraCotizacion enviado = new EnviarCompraCotizacionUseCaseImpl(repository).enviar(1L, "SC-2026-000001");

        assertThat(enviado.getEstado()).isEqualTo(CompraCotizacionEstado.ENVIADA);
        assertThat(enviado.getNumero()).isEqualTo("SC-2026-000001");
    }

    @Test
    void enviarInexistenteFalla() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> new EnviarCompraCotizacionUseCaseImpl(repository).enviar(99L, "SC-1"))
                .isInstanceOf(CompraCotizacionException.class);
    }

    @Test
    void anularDelega() {
        when(repository.findById(1L)).thenReturn(Optional.of(proceso()));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        CompraCotizacion anulado = new AnularCompraCotizacionUseCaseImpl(repository).anular(1L, "desistida");

        assertThat(anulado.getEstado()).isEqualTo(CompraCotizacionEstado.ANULADA);
    }

    @Test
    void pendientesDelega() {
        when(pendientesPort.listar(15, List.of(14))).thenReturn(List.of(
                new CompraCotizacionPendiente(100, 2, "PC-1", 14, 1, new BigDecimal("3.00"),
                        "Unidad", "Notebook", null, null)));

        assertThat(new ListPedidoItemsPendientesUseCaseImpl(pendientesPort).listar(15, List.of(14))).hasSize(1);
    }

    @Test
    void registrarRespuestaDeUnProcesoEnviado() {
        CompraCotizacion proceso = proceso();
        proceso.enviar("SC-2026-000001");
        when(repository.findById(1L)).thenReturn(Optional.of(proceso));
        CompraCotizacionProveedor proveedor = CompraCotizacionProveedor.builder()
                .compraCotizacionProveedorId(5L).compraCotizacionId(1L).build();
        when(repository.findProveedorById(5L)).thenReturn(Optional.of(proveedor));
        when(repository.saveProveedor(any())).thenAnswer(i -> i.getArgument(0));

        CompraCotizacionProveedor respuesta = CompraCotizacionProveedor.builder()
                .moneda("ARS").total(new BigDecimal("100.00"))
                .items(List.of(CompraCotizacionProveedorItem.builder()
                        .compraCotizacionItemId(9L).precioUnitario(new BigDecimal("10.00")).build()))
                .build();

        CompraCotizacionProveedor guardado = new RegistrarRespuestaCompraCotizacionUseCaseImpl(repository)
                .registrarRespuesta(1L, 5L, respuesta);

        assertThat(guardado.esRespondida()).isTrue();
        assertThat(guardado.getItems()).hasSize(1);
    }

    @Test
    void registrarRespuestaRequiereProcesoEnviado() {
        when(repository.findById(1L)).thenReturn(Optional.of(proceso()));

        assertThatThrownBy(() -> new RegistrarRespuestaCompraCotizacionUseCaseImpl(repository)
                .registrarRespuesta(1L, 5L, CompraCotizacionProveedor.builder().build()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void marcarNoCotizaYRecomendarYCerrar() {
        CompraCotizacion proceso = proceso();
        proceso.enviar("SC-2026-000001");
        when(repository.findProveedorById(5L)).thenReturn(Optional.of(CompraCotizacionProveedor.builder()
                .compraCotizacionProveedorId(5L).compraCotizacionId(1L).build()));
        when(repository.saveProveedor(any())).thenAnswer(i -> i.getArgument(0));

        assertThat(new MarcarNoCotizaCompraCotizacionUseCaseImpl(repository).marcarNoCotiza(1L, 5L, "sin stock")
                .getObservacion()).isEqualTo("sin stock");

        when(repository.findById(1L)).thenReturn(Optional.of(proceso));
        proceso.getProveedores().get(0).registrarRespuesta(null, "ARS", new BigDecimal("100.00"), null, null, null, null);
        when(repository.updateHeader(any())).thenAnswer(i -> i.getArgument(0));

        CompraCotizacion recomendado = new RecomendarCompraCotizacionUseCaseImpl(repository).recomendar(1L, 5L, "mejor");
        assertThat(recomendado.getEstado()).isEqualTo(CompraCotizacionEstado.RECOMENDADA);

        CompraCotizacion cerrado = new CerrarCompraCotizacionUseCaseImpl(repository).cerrar(1L);
        assertThat(cerrado.getEstado()).isEqualTo(CompraCotizacionEstado.CERRADA);
    }
}

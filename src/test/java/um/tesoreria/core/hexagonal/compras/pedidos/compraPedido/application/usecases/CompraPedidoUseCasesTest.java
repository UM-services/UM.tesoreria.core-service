package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.exception.CompraPedidoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.EjercicioActual;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.CompraPedidoRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.EjercicioActualPort;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraPedidoUseCasesTest {

    @Mock
    private CompraPedidoRepository repository;

    @Mock
    private EjercicioActualPort ejercicioActualPort;

    @Test
    void crearAsignaFechaDeHoyEjercicioYEstadoBorrador() {
        when(ejercicioActualPort.findEjercicioActual(any())).thenReturn(Optional.of(new EjercicioActual(7, 2026)));
        when(repository.create(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CreateCompraPedidoUseCaseImpl useCase = new CreateCompraPedidoUseCaseImpl(repository, ejercicioActualPort);

        CompraPedido creado = useCase.crear(CompraPedido.builder()
                .solicitanteId(1)
                .dependenciaId(2)
                .build());

        assertThat(creado.getEstado()).isEqualTo(CompraPedidoEstado.BORRADOR);
        assertThat(creado.getNumero()).isNull();
        assertThat(creado.getEjercicioId()).isEqualTo(7);
        assertThat(creado.getFecha()).isNotNull();
    }

    @Test
    void crearExigeSolicitanteYDependencia() {
        CreateCompraPedidoUseCaseImpl useCase = new CreateCompraPedidoUseCaseImpl(repository, ejercicioActualPort);

        assertThatThrownBy(() -> useCase.crear(CompraPedido.builder().dependenciaId(2).build()))
                .isInstanceOf(CompraPedidoException.class);
    }

    @Test
    void crearFallaSiNoHayEjercicioAbierto() {
        when(ejercicioActualPort.findEjercicioActual(any())).thenReturn(Optional.empty());
        CreateCompraPedidoUseCaseImpl useCase = new CreateCompraPedidoUseCaseImpl(repository, ejercicioActualPort);

        assertThatThrownBy(() -> useCase.crear(CompraPedido.builder()
                .solicitanteId(1)
                .dependenciaId(2)
                .build()))
                .isInstanceOf(CompraPedidoException.class);
    }

    @Test
    void enviarFijaElNumero() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.BORRADOR)
                .montoConocido(true)
                .build();
        when(repository.findById(5)).thenReturn(Optional.of(pedido));
        when(repository.update(any())).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));
        EnviarCompraPedidoUseCaseImpl useCase = new EnviarCompraPedidoUseCaseImpl(repository);

        CompraPedido enviado = useCase.enviar(5, "PC-2026-000001");

        assertThat(enviado.getNumero()).isEqualTo("PC-2026-000001");
        assertThat(enviado.getEstado()).isEqualTo(CompraPedidoEstado.PENDIENTE_AUTORIZACION);
    }

    @Test
    void actualizarUnPedidoInexistenteFalla() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        UpdateCompraPedidoUseCaseImpl useCase = new UpdateCompraPedidoUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.actualizar(99, CompraPedido.builder().build()))
                .isInstanceOf(CompraPedidoException.class);
    }

    @Test
    void autorizarUnPedidoInexistenteFalla() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        AutorizarCompraPedidoUseCaseImpl useCase = new AutorizarCompraPedidoUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.autorizar(99, 1)).isInstanceOf(CompraPedidoException.class);
    }

}

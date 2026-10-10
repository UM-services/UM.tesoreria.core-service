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

import java.math.BigDecimal;
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
        assertThat(enviado.getEstado()).isEqualTo(CompraPedidoEstado.PENDIENTE_ENVIO);
    }

    @Test
    void actualizarUnPedidoInexistenteFalla() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        UpdateCompraPedidoUseCaseImpl useCase = new UpdateCompraPedidoUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.actualizar(99, CompraPedido.builder().build()))
                .isInstanceOf(CompraPedidoException.class);
    }

    @Test
    void actualizarAplicaLosDatosDeNegocio() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.BORRADOR)
                .build();
        when(repository.findById(5)).thenReturn(Optional.of(pedido));
        when(repository.update(any())).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));
        UpdateCompraPedidoUseCaseImpl useCase = new UpdateCompraPedidoUseCaseImpl(repository);

        CompraPedido actualizado = useCase.actualizar(5,
                CompraPedido.builder().necesidad("Notebooks").build());

        assertThat(actualizado.getNecesidad()).isEqualTo("Notebooks");
    }

    @Test
    void aprobarUnPedidoInexistenteFalla() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        AprobarCompraPedidoUseCaseImpl useCase = new AprobarCompraPedidoUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.aprobar(99, 1)).isInstanceOf(CompraPedidoException.class);
    }

    @Test
    void aprobarUnPendienteLoDejaEnviado() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.PENDIENTE_ENVIO)
                .build();
        when(repository.findById(5)).thenReturn(Optional.of(pedido));
        when(repository.update(any())).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));
        AprobarCompraPedidoUseCaseImpl useCase = new AprobarCompraPedidoUseCaseImpl(repository);

        CompraPedido aprobado = useCase.aprobar(5, 9);

        assertThat(aprobado.getEstado()).isEqualTo(CompraPedidoEstado.EN_REVISION_COMPRAS);
        assertThat(aprobado.getAutorizanteId()).isEqualTo(9);
    }

    @Test
    void rechazarUnPedidoInexistenteFalla() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        RechazarCompraPedidoUseCaseImpl useCase = new RechazarCompraPedidoUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.rechazar(99, 1, "motivo")).isInstanceOf(CompraPedidoException.class);
    }

    @Test
    void rechazarUnPendienteLoDejaRechazado() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.PENDIENTE_ENVIO)
                .build();
        when(repository.findById(5)).thenReturn(Optional.of(pedido));
        when(repository.update(any())).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));
        RechazarCompraPedidoUseCaseImpl useCase = new RechazarCompraPedidoUseCaseImpl(repository);

        CompraPedido rechazado = useCase.rechazar(5, 9, "Falta cotización");

        assertThat(rechazado.getEstado()).isEqualTo(CompraPedidoEstado.RECHAZADO);
        assertThat(rechazado.getRechazoMotivo()).isEqualTo("Falta cotización");
    }

    @Test
    void descartarUnPedidoInexistenteFalla() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        DescartarCompraPedidoUseCaseImpl useCase = new DescartarCompraPedidoUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.descartar(99, "motivo")).isInstanceOf(CompraPedidoException.class);
    }

    @Test
    void descartarUnBorradorLoDejaDescartado() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.BORRADOR)
                .build();
        when(repository.findById(5)).thenReturn(Optional.of(pedido));
        when(repository.update(any())).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));
        DescartarCompraPedidoUseCaseImpl useCase = new DescartarCompraPedidoUseCaseImpl(repository);

        CompraPedido descartado = useCase.descartar(5, "No hace falta");

        assertThat(descartado.getEstado()).isEqualTo(CompraPedidoEstado.DESCARTADO);
    }

    @Test
    void estimarUnPedidoInexistenteFalla() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        EstimarCompraPedidoUseCaseImpl useCase = new EstimarCompraPedidoUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.estimar(99, new BigDecimal("1"), null))
                .isInstanceOf(CompraPedidoException.class);
    }

    @Test
    void estimarUnPedidoEnRevisionLoDejaPendienteDeAutorizacion() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.EN_REVISION_COMPRAS)
                .build();
        when(repository.findById(5)).thenReturn(Optional.of(pedido));
        when(repository.update(any())).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));
        EstimarCompraPedidoUseCaseImpl useCase = new EstimarCompraPedidoUseCaseImpl(repository);

        CompraPedido estimado = useCase.estimar(5, new BigDecimal("100"), "fuente");

        assertThat(estimado.getEstado()).isEqualTo(CompraPedidoEstado.PENDIENTE_AUTORIZACION_PRESUPUESTO);
    }

    @Test
    void autorizarPresupuestoDeUnPendienteLoDejaAutorizado() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.PENDIENTE_AUTORIZACION_PRESUPUESTO)
                .build();
        when(repository.findById(5)).thenReturn(Optional.of(pedido));
        when(repository.update(any())).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));
        AutorizarPresupuestoCompraPedidoUseCaseImpl useCase = new AutorizarPresupuestoCompraPedidoUseCaseImpl(repository);

        CompraPedido autorizado = useCase.autorizar(5);

        assertThat(autorizado.getEstado()).isEqualTo(CompraPedidoEstado.AUTORIZADO_PRESUPUESTO);
    }

    @Test
    void rechazarPresupuestoDeUnPendienteLoDejaRechazado() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5)
                .estado(CompraPedidoEstado.PENDIENTE_AUTORIZACION_PRESUPUESTO)
                .build();
        when(repository.findById(5)).thenReturn(Optional.of(pedido));
        when(repository.update(any())).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));
        RechazarPresupuestoCompraPedidoUseCaseImpl useCase = new RechazarPresupuestoCompraPedidoUseCaseImpl(repository);

        CompraPedido rechazado = useCase.rechazar(5, "fuera de política");

        assertThat(rechazado.getEstado()).isEqualTo(CompraPedidoEstado.RECHAZADO);
        assertThat(rechazado.getRechazoMotivo()).isEqualTo("fuera de política");
    }

}

package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompraPedidoTest {

    private CompraPedido borrador() {
        return CompraPedido.builder()
                .compraPedidoId(1)
                .solicitanteId(10)
                .dependenciaId(20)
                .fecha(LocalDateTime.of(2026, 10, 6, 9, 0))
                .estado(CompraPedidoEstado.BORRADOR)
                .montoConocido(true)
                .urgente(false)
                .build();
    }

    @Test
    void unBorradorEsEditable() {
        assertThat(borrador().esEditable()).isTrue();
    }

    @Test
    void enviarFijaElNumeroYPasaAAutorizacion() {
        CompraPedido pedido = borrador();

        pedido.enviar("PC-2026-000001");

        assertThat(pedido.getNumero()).isEqualTo("PC-2026-000001");
        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.PENDIENTE_AUTORIZACION);
        assertThat(pedido.esEditable()).isTrue();
    }

    @Test
    void enviarSinMontoConocidoPasaAEstimacion() {
        CompraPedido pedido = borrador();
        pedido.setMontoConocido(false);

        pedido.enviar("PC-2026-000002");

        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.PENDIENTE_ESTIMACION);
    }

    @Test
    void noSePuedeEnviarDosVeces() {
        CompraPedido pedido = borrador();
        pedido.enviar("PC-2026-000001");

        assertThatThrownBy(() -> pedido.enviar("PC-2026-000003"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void autorizarGuardaElAutorizanteYBloqueaLaEdicion() {
        CompraPedido pedido = borrador();
        pedido.enviar("PC-2026-000001");

        pedido.autorizar(99);

        assertThat(pedido.getAutorizanteId()).isEqualTo(99);
        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.AUTORIZADA);
        assertThat(pedido.esEditable()).isFalse();
    }

    @Test
    void noSePuedeAutorizarUnBorrador() {
        assertThatThrownBy(() -> borrador().autorizar(99)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void noSePuedeAutorizarSinAutorizante() {
        CompraPedido pedido = borrador();
        pedido.enviar("PC-2026-000001");

        assertThatThrownBy(() -> pedido.autorizar(null)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rechazarUnPendienteLoDejaRechazado() {
        CompraPedido pedido = borrador();
        pedido.enviar("PC-2026-000001");

        pedido.rechazar();

        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.RECHAZADA);
        assertThat(pedido.esEditable()).isFalse();
    }

    @Test
    void actualizarDatosCambiaSoloLosCamposDeNegocio() {
        CompraPedido pedido = borrador();

        pedido.actualizarDatos(CompraPedido.builder()
                .necesidad("Renovación de equipamiento")
                .montoEstimado(new java.math.BigDecimal("4500000.00"))
                .build());

        assertThat(pedido.getNecesidad()).isEqualTo("Renovación de equipamiento");
        assertThat(pedido.getMontoEstimado()).isEqualByComparingTo("4500000.00");
        assertThat(pedido.getSolicitanteId()).isEqualTo(10);
        assertThat(pedido.getDependenciaId()).isEqualTo(20);
    }

    @Test
    void noSePuedeEditarDespuesDeLaPrimeraAutorizacion() {
        CompraPedido pedido = borrador();
        pedido.enviar("PC-2026-000001");
        pedido.autorizar(99);

        assertThatThrownBy(() -> pedido.actualizarDatos(
                CompraPedido.builder().necesidad("otra").build()))
                .isInstanceOf(IllegalStateException.class);
    }

}

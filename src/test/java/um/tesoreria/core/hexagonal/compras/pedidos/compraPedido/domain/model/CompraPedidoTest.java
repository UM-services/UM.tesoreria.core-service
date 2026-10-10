package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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

    private CompraPedido pendienteDeEnvio() {
        CompraPedido pedido = borrador();
        pedido.enviar("PC-2026-000001");
        return pedido;
    }

    @Test
    void unBorradorEsEditable() {
        assertThat(borrador().esEditable()).isTrue();
    }

    @Test
    void enviarFijaElNumeroYPasaAPendienteDeEnvio() {
        CompraPedido pedido = borrador();

        pedido.enviar("PC-2026-000001");

        assertThat(pedido.getNumero()).isEqualTo("PC-2026-000001");
        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.PENDIENTE_ENVIO);
        assertThat(pedido.esEditable()).isFalse();
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
        CompraPedido pedido = pendienteDeEnvio();

        assertThatThrownBy(() -> pedido.enviar("PC-2026-000003"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void enviarRequiereNumero() {
        assertThatThrownBy(() -> borrador().enviar(" ")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void aprobarGuardaElAutorizanteYFechaYBloqueaLaEdicion() {
        CompraPedido pedido = pendienteDeEnvio();

        pedido.aprobar(99);

        assertThat(pedido.getAutorizanteId()).isEqualTo(99);
        assertThat(pedido.getFechaEnvio()).isNotNull();
        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.EN_REVISION_COMPRAS);
        assertThat(pedido.esEditable()).isFalse();
    }

    @Test
    void noSePuedeAprobarUnBorrador() {
        assertThatThrownBy(() -> borrador().aprobar(99)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void noSePuedeAprobarSinAutorizante() {
        CompraPedido pedido = pendienteDeEnvio();

        assertThatThrownBy(() -> pedido.aprobar(null)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rechazarUnPendienteLoDejaEditableYGuardaElMotivo() {
        CompraPedido pedido = pendienteDeEnvio();

        pedido.rechazar(99, "Falta la cotización");

        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.RECHAZADO);
        assertThat(pedido.getAutorizanteId()).isEqualTo(99);
        assertThat(pedido.getRechazoMotivo()).isEqualTo("Falta la cotización");
        assertThat(pedido.esEditable()).isTrue();
    }

    @Test
    void rechazarRequiereMotivo() {
        CompraPedido pedido = pendienteDeEnvio();

        assertThatThrownBy(() -> pedido.rechazar(99, null)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void unRechazadoPuedeVolverAPresentarseYLimpiaElMotivo() {
        CompraPedido pedido = pendienteDeEnvio();
        pedido.rechazar(99, "Falta la cotización");

        pedido.enviar("PC-2026-000001");

        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.PENDIENTE_ENVIO);
        assertThat(pedido.getRechazoMotivo()).isNull();
    }

    @Test
    void descartarUnBorradorLoDejaDescartado() {
        CompraPedido pedido = borrador();

        pedido.descartar("Ya no hace falta");

        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.DESCARTADO);
        assertThat(pedido.getDescartadoMotivo()).isEqualTo("Ya no hace falta");
        assertThat(pedido.esEditable()).isFalse();
    }

    @Test
    void descartarUnRechazadoLoDejaDescartado() {
        CompraPedido pedido = pendienteDeEnvio();
        pedido.rechazar(99, "Fuera de alcance");

        pedido.descartar("Se descarta");

        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.DESCARTADO);
    }

    @Test
    void noSePuedeDescartarUnPendienteDeEnvio() {
        CompraPedido pedido = pendienteDeEnvio();

        assertThatThrownBy(() -> pedido.descartar("motivo")).isInstanceOf(IllegalStateException.class);
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
    void noSePuedeEditarDespuesDelEnvioACompras() {
        CompraPedido pedido = pendienteDeEnvio();
        pedido.aprobar(99);

        assertThatThrownBy(() -> pedido.actualizarDatos(
                CompraPedido.builder().necesidad("otra").build()))
                .isInstanceOf(IllegalStateException.class);
    }

    private CompraPedido enRevisionDeCompras() {
        CompraPedido pedido = pendienteDeEnvio();
        pedido.aprobar(99);
        return pedido;
    }

    @Test
    void estimarDesdeRevisionDeComprasCargaElMontoYHabilitaLaAutorizacion() {
        CompraPedido pedido = enRevisionDeCompras();

        pedido.estimar(new BigDecimal("4500000.00"), "Estimación de compras");

        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.PENDIENTE_AUTORIZACION_PRESUPUESTO);
        assertThat(pedido.getMontoEstimado()).isEqualByComparingTo("4500000.00");
        assertThat(pedido.getFuenteEstimacion()).isEqualTo("Estimación de compras");
        assertThat(pedido.getMontoConocido()).isTrue();
    }

    @Test
    void estimarRequiereMontoPositivo() {
        CompraPedido pedido = enRevisionDeCompras();

        assertThatThrownBy(() -> pedido.estimar(null, "x")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> pedido.estimar(BigDecimal.ZERO, "x")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void estimarSoloDesdeRevisionDeCompras() {
        assertThatThrownBy(() -> pendienteDeEnvio().estimar(new BigDecimal("1"), null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void autorizarPresupuestoLoDejaAutorizado() {
        CompraPedido pedido = enRevisionDeCompras();
        pedido.estimar(new BigDecimal("100"), null);

        pedido.autorizarPresupuesto();

        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.AUTORIZADO_PRESUPUESTO);
        assertThat(pedido.esEditable()).isFalse();
    }

    @Test
    void autorizarPresupuestoRequiereEstadoPendiente() {
        assertThatThrownBy(() -> pendienteDeEnvio().autorizarPresupuesto())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rechazarPresupuestoVuelveAlSolicitanteYRequiereMotivo() {
        CompraPedido pedido = enRevisionDeCompras();
        pedido.estimar(new BigDecimal("100"), null);

        assertThatThrownBy(() -> pedido.rechazarPresupuesto(null)).isInstanceOf(IllegalStateException.class);

        pedido.rechazarPresupuesto("Monto fuera de política");

        assertThat(pedido.getEstado()).isEqualTo(CompraPedidoEstado.RECHAZADO);
        assertThat(pedido.getRechazoMotivo()).isEqualTo("Monto fuera de política");
        assertThat(pedido.esEditable()).isTrue();
    }

}

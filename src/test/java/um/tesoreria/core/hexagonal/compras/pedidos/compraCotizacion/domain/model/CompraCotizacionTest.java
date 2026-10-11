package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompraCotizacionTest {

    private CompraCotizacionProveedor proveedor(Integer proveedorId) {
        return CompraCotizacionProveedor.builder().proveedorId(proveedorId)
                .estado(CompraCotizacionProveedorEstado.PENDIENTE).build();
    }

    private CompraCotizacionItem item(Integer pedidoItemId) {
        return CompraCotizacionItem.builder().compraPedidoItemId(pedidoItemId).orden(1)
                .cantidad(new BigDecimal("1.00")).build();
    }

    private CompraCotizacion borrador(int proveedores, Integer cantidadMinima, String excepcion) {
        CompraCotizacion proceso = CompraCotizacion.builder()
                .estado(CompraCotizacionEstado.BORRADOR)
                .cantidadMinima(cantidadMinima)
                .excepcionMotivo(excepcion)
                .build();
        proceso.setItems(List.of(item(1)));
        proceso.setProveedores(java.util.stream.IntStream.range(0, proveedores)
                .mapToObj(this::proveedor)
                .collect(java.util.stream.Collectors.toList()));
        return proceso;
    }

    @Test
    void enviarFijaNumeroEstadoYDocumentos() {
        CompraCotizacion proceso = borrador(3, 3, null);

        proceso.enviar("SC-2026-000001");

        assertThat(proceso.getNumero()).isEqualTo("SC-2026-000001");
        assertThat(proceso.getEstado()).isEqualTo(CompraCotizacionEstado.ENVIADA);
        assertThat(proceso.getFechaEnvio()).isNotNull();
        assertThat(proceso.getProveedores())
                .allMatch(p -> p.getEstado() == CompraCotizacionProveedorEstado.ENVIADA);
    }

    @Test
    void enviarFallaSiFaltaElMinimoSinExcepcion() {
        CompraCotizacion proceso = borrador(2, 3, null);

        assertThatThrownBy(() -> proceso.enviar("SC-2026-000001"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void enviarAdmiteMenosDelMinimoConExcepcion() {
        CompraCotizacion proceso = borrador(2, 3, "Proveedor único habilitado");

        proceso.enviar("SC-2026-000002");

        assertThat(proceso.getEstado()).isEqualTo(CompraCotizacionEstado.ENVIADA);
    }

    @Test
    void enviarRequiereItemsYProveedores() {
        CompraCotizacion sinItems = CompraCotizacion.builder().estado(CompraCotizacionEstado.BORRADOR).build();
        sinItems.setProveedores(List.of(proveedor(1)));
        assertThatThrownBy(() -> sinItems.enviar("SC-1")).isInstanceOf(IllegalStateException.class);

        CompraCotizacion sinProveedores = CompraCotizacion.builder().estado(CompraCotizacionEstado.BORRADOR).build();
        sinProveedores.setItems(List.of(item(1)));
        assertThatThrownBy(() -> sinProveedores.enviar("SC-1")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void soloElBorradorEsEditable() {
        assertThat(borrador(1, 1, null).esEditable()).isTrue();
        CompraCotizacion enviada = borrador(1, 1, null);
        enviada.enviar("SC-1");
        assertThat(enviada.esEditable()).isFalse();
    }

    @Test
    void anularPasaAAnuladaYNoSeReanuda() {
        CompraCotizacion proceso = borrador(1, 1, null);

        proceso.anular("desistida");

        assertThat(proceso.getEstado()).isEqualTo(CompraCotizacionEstado.ANULADA);
        assertThatThrownBy(() -> proceso.anular("otra")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void registrarRespuestaYNoCotizaSobreElProveedor() {
        CompraCotizacionProveedor proveedor = proveedor(7);
        proveedor.registrarRespuesta(null, "ARS", new BigDecimal("100.00"), "10 días", "30 días", "12 meses", "15 días");
        assertThat(proveedor.esRespondida()).isTrue();
        assertThat(proveedor.getFechaRespuesta()).isNotNull();
        assertThat(proveedor.getTotal()).isEqualByComparingTo("100.00");

        CompraCotizacionProveedor otro = proveedor(8);
        otro.marcarNoCotiza("sin stock");
        assertThat(otro.getEstado()).isEqualTo(CompraCotizacionProveedorEstado.NO_COTIZA);
        assertThat(otro.getObservacion()).isEqualTo("sin stock");
    }

    @Test
    void recomendarSoloDesdeEnviadaYConElMinimo() {
        CompraCotizacion proceso = borrador(2, 2, null);
        proceso.enviar("SC-2026-000001");
        proceso.getProveedores().get(0).registrarRespuesta(null, "ARS", new BigDecimal("100.00"), null, null, null, null);
        proceso.getProveedores().get(1).registrarRespuesta(null, "ARS", new BigDecimal("120.00"), null, null, null, null);

        proceso.recomendar(5L, "mejor precio", 2);

        assertThat(proceso.getEstado()).isEqualTo(CompraCotizacionEstado.RECOMENDADA);
        assertThat(proceso.getProveedorRecomendadoId()).isEqualTo(5L);
        assertThat(proceso.getFechaRecomendacion()).isNotNull();

        proceso.cerrar();
        assertThat(proceso.getEstado()).isEqualTo(CompraCotizacionEstado.CERRADA);
    }

    @Test
    void recomendarSinRespuestasSuficientesFalla() {
        CompraCotizacion proceso = borrador(2, 2, null);
        proceso.enviar("SC-2026-000001");

        assertThatThrownBy(() -> proceso.recomendar(5L, "x", 1)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void recomendarRequiereEnviadaYProveedor() {
        assertThatThrownBy(() -> borrador(1, 1, null).recomendar(5L, "x", 1))
                .isInstanceOf(IllegalStateException.class);
    }
}

package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pedido de compra: cabecera del agregado. El detalle vive en el slice
 * {@code compraPedidoItem} y se compone en la fachada de aplicación.
 *
 * <p>El número ({@code PC-AAAA-NNNNNN}) se asigna recién al enviar, de modo que un
 * borrador no consume correlativo. La edición se permite hasta la primera autorización.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraPedido {

    private Integer compraPedidoId;
    private String numero;
    private Integer ejercicioId;
    private LocalDateTime fecha;
    private CompraPedidoEstado estado;
    private Integer autorizanteId;
    private Integer solicitanteId;
    private Integer dependenciaId;
    private Integer facultadId;
    private Integer geograficaId;
    private String necesidad;
    private LocalDateTime fechaRequerida;
    private Boolean urgente;
    private String urgenciaMotivo;
    private Boolean montoConocido;
    private BigDecimal montoEstimado;
    private String fuenteEstimacion;

    public boolean esEditable() {
        return estado != null && estado.esEditable();
    }

    /**
     * Aplica los datos de negocio editables. No toca número, estado, ejercicio ni los
     * identificadores de solicitante/dependencia/facultad/sede.
     */
    public void actualizarDatos(CompraPedido datos) {
        if (!esEditable()) {
            throw new IllegalStateException("El pedido no puede editarse en el estado " + estado);
        }
        if (datos == null) {
            return;
        }
        if (datos.getNecesidad() != null) this.necesidad = datos.getNecesidad();
        if (datos.getFechaRequerida() != null) this.fechaRequerida = datos.getFechaRequerida();
        if (datos.getUrgente() != null) this.urgente = datos.getUrgente();
        if (datos.getUrgenciaMotivo() != null) this.urgenciaMotivo = datos.getUrgenciaMotivo();
        if (datos.getMontoConocido() != null) this.montoConocido = datos.getMontoConocido();
        if (datos.getMontoEstimado() != null) this.montoEstimado = datos.getMontoEstimado();
        if (datos.getFuenteEstimacion() != null) this.fuenteEstimacion = datos.getFuenteEstimacion();
    }

    /**
     * Envía el borrador: fija el número ya reservado y pasa a estimación (si no se conoce
     * el monto) o a autorización.
     */
    public void enviar(String numero) {
        if (estado != CompraPedidoEstado.BORRADOR) {
            throw new IllegalStateException("Sólo un borrador puede enviarse (estado actual: " + estado + ")");
        }
        if (numero == null || numero.isBlank()) {
            throw new IllegalStateException("El envío requiere el número asignado");
        }
        this.numero = numero;
        this.estado = Boolean.FALSE.equals(montoConocido)
                ? CompraPedidoEstado.PENDIENTE_ESTIMACION
                : CompraPedidoEstado.PENDIENTE_AUTORIZACION;
    }

    public void autorizar(Integer autorizanteId) {
        if (estado != CompraPedidoEstado.PENDIENTE_AUTORIZACION) {
            throw new IllegalStateException("El pedido no está pendiente de autorización (estado actual: " + estado + ")");
        }
        if (autorizanteId == null) {
            throw new IllegalStateException("Falta el autorizante");
        }
        this.autorizanteId = autorizanteId;
        this.estado = CompraPedidoEstado.AUTORIZADA;
    }

    public void rechazar() {
        if (estado != CompraPedidoEstado.PENDIENTE_AUTORIZACION) {
            throw new IllegalStateException("El pedido no está pendiente de autorización (estado actual: " + estado + ")");
        }
        this.estado = CompraPedidoEstado.RECHAZADA;
    }

}

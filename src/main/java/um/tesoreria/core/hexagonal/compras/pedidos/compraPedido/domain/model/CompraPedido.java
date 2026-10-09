package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pedido de compra: cabecera del agregado. El detalle vive en el slice
 * {@code compraPedidoItem} y se compone en la fachada de aplicación.
 *
 * <p>El número ({@code PC-AAAA-NNNNNN}) se asigna recién al presentar, de modo que un
 * borrador no consume correlativo. La edición se permite hasta el envío a compras.</p>
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
    private LocalDateTime fechaEnvio;
    private String rechazoMotivo;
    private String descartadoMotivo;

    public boolean esEditable() {
        return estado != null && estado.esEditable();
    }

    public boolean esPresentable() {
        return estado == CompraPedidoEstado.BORRADOR || estado == CompraPedidoEstado.RECHAZADO;
    }

    public boolean esDescartable() {
        return estado == CompraPedidoEstado.BORRADOR || estado == CompraPedidoEstado.RECHAZADO;
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
     * Presenta el pedido: fija el número ya reservado y pasa a estimación (si no se conoce
     * el monto) o a la bandeja del autorizante. Se admite desde un borrador o desde un
     * pedido rechazado que se corrige.
     */
    public void enviar(String numero) {
        if (!esPresentable()) {
            throw new IllegalStateException("Sólo un borrador o un rechazado puede presentarse (estado actual: " + estado + ")");
        }
        if (numero == null || numero.isBlank()) {
            throw new IllegalStateException("La presentación requiere el número asignado");
        }
        this.numero = numero;
        this.rechazoMotivo = null;
        this.estado = Boolean.FALSE.equals(montoConocido)
                ? CompraPedidoEstado.PENDIENTE_ESTIMACION
                : CompraPedidoEstado.PENDIENTE_ENVIO;
    }

    /**
     * Aprueba el envío a compras: fija el autorizante, la fecha de envío y pasa a
     * {@link CompraPedidoEstado#ENVIADO}.
     */
    public void aprobar(Integer autorizanteId) {
        if (estado != CompraPedidoEstado.PENDIENTE_ENVIO) {
            throw new IllegalStateException("El pedido no está pendiente de envío (estado actual: " + estado + ")");
        }
        if (autorizanteId == null) {
            throw new IllegalStateException("Falta el autorizante");
        }
        this.autorizanteId = autorizanteId;
        this.fechaEnvio = LocalDateTime.now();
        this.estado = CompraPedidoEstado.ENVIADO;
    }

    /**
     * Rechaza el envío: el pedido vuelve al solicitante para revisar o descartar. El motivo
     * es obligatorio.
     */
    public void rechazar(Integer autorizanteId, String motivo) {
        if (estado != CompraPedidoEstado.PENDIENTE_ENVIO) {
            throw new IllegalStateException("El pedido no está pendiente de envío (estado actual: " + estado + ")");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalStateException("El rechazo requiere un motivo");
        }
        this.autorizanteId = autorizanteId;
        this.rechazoMotivo = motivo;
        this.estado = CompraPedidoEstado.RECHAZADO;
    }

    /**
     * Descarta el pedido: queda como no presentado a compras. Sólo desde un borrador o un
     * rechazado.
     */
    public void descartar(String motivo) {
        if (!esDescartable()) {
            throw new IllegalStateException("Sólo puede descartarse un borrador o un rechazado (estado actual: " + estado + ")");
        }
        this.descartadoMotivo = motivo;
        this.estado = CompraPedidoEstado.DESCARTADO;
    }

}

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
     * Aprueba el envío: fija el autorizante, la fecha de envío y deja el pedido a cargo del
     * dpto. de compras para su revisión ({@link CompraPedidoEstado#EN_REVISION_COMPRAS}).
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
        this.estado = CompraPedidoEstado.EN_REVISION_COMPRAS;
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

    /**
     * Revisión del dpto. de compras: carga/confirma el valor estimado y habilita la autorización
     * del proceso de pedido de presupuesto ({@code -> PENDIENTE_AUTORIZACION_PRESUPUESTO}).
     */
    public void estimar(BigDecimal monto, String fuente) {
        if (estado != CompraPedidoEstado.EN_REVISION_COMPRAS) {
            throw new IllegalStateException("El pedido no está en revisión de compras (estado actual: " + estado + ")");
        }
        if (monto == null || monto.signum() <= 0) {
            throw new IllegalStateException("El valor estimado debe ser mayor a cero");
        }
        this.montoEstimado = monto;
        if (fuente != null) {
            this.fuenteEstimacion = fuente;
        }
        this.montoConocido = Boolean.TRUE;
        this.estado = CompraPedidoEstado.PENDIENTE_AUTORIZACION_PRESUPUESTO;
    }

    /**
     * Autoriza el inicio del proceso de pedido de presupuesto. El límite por monto no se valida
     * acá: lo resuelve la fachada contra el perfil de autoridad del usuario.
     */
    public void autorizarPresupuesto() {
        if (estado != CompraPedidoEstado.PENDIENTE_AUTORIZACION_PRESUPUESTO) {
            throw new IllegalStateException(
                    "El pedido no está pendiente de autorización de presupuesto (estado actual: " + estado + ")");
        }
        this.estado = CompraPedidoEstado.AUTORIZADO_PRESUPUESTO;
    }

    /**
     * Rechaza la autorización de presupuesto: el pedido vuelve al solicitante. El motivo es
     * obligatorio.
     */
    public void rechazarPresupuesto(String motivo) {
        if (estado != CompraPedidoEstado.PENDIENTE_AUTORIZACION_PRESUPUESTO) {
            throw new IllegalStateException(
                    "El pedido no está pendiente de autorización de presupuesto (estado actual: " + estado + ")");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalStateException("El rechazo requiere un motivo");
        }
        this.rechazoMotivo = motivo;
        this.estado = CompraPedidoEstado.RECHAZADO;
    }

}

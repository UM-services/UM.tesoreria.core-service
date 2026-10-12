package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Proceso de cotización: agrupador de ítems combinados de uno o varios pedidos y de los pedidos
 * de cotización dirigidos a cada proveedor.
 *
 * <p>El número {@code SC-AAAA-NNNNNN} se reserva al enviar. La recomendación (proveedor elegido
 * + fundamento) la completa la etapa 6C.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraCotizacion {

    private Long compraCotizacionId;
    private String numero;
    private Integer ejercicioId;
    private LocalDateTime fecha;
    private CompraCotizacionEstado estado;
    private String observacion;
    private LocalDateTime fechaLimiteRespuesta;
    private BigDecimal montoEstimado;
    private Integer cantidadMinima;
    private String excepcionMotivo;
    private LocalDateTime fechaEnvio;
    private Long proveedorRecomendadoId;
    private String fundamentoRecomendacion;
    private LocalDateTime fechaRecomendacion;
    private Integer usuarioId;
    @Builder.Default
    private List<CompraCotizacionItem> items = new ArrayList<>();
    @Builder.Default
    private List<CompraCotizacionProveedor> proveedores = new ArrayList<>();

    public boolean esEditable() {
        return estado != null && estado.esEditable();
    }

    /**
     * Valida el envío: al menos un ítem y un proveedor, y cantidad de proveedores ≥ mínimo
     * (salvo excepción con motivo).
     */
    public void validarEnvio() {
        if (estado != CompraCotizacionEstado.BORRADOR) {
            throw new IllegalStateException("Sólo un borrador puede enviarse (estado actual: " + estado + ")");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("La solicitud requiere al menos un ítem");
        }
        long proveedoresActivos = proveedores == null ? 0
                : proveedores.stream()
                        .filter(p -> p.getEstado() != CompraCotizacionProveedorEstado.ANULADA)
                        .count();
        if (proveedoresActivos == 0) {
            throw new IllegalStateException("La solicitud requiere al menos un proveedor");
        }
        int requeridas = cantidadMinima != null ? cantidadMinima : 1;
        boolean excepcion = excepcionMotivo != null && !excepcionMotivo.isBlank();
        if (proveedoresActivos < requeridas && !excepcion) {
            throw new IllegalStateException("Se requieren " + requeridas
                    + " cotizaciones; indique el motivo de la excepción para solicitar menos");
        }
    }

    /** Fija el número reservado, sella la fecha de envío y pasa el proceso y sus documentos. */
    public void enviar(String numero) {
        validarEnvio();
        if (numero == null || numero.isBlank()) {
            throw new IllegalStateException("El envío requiere el número asignado");
        }
        this.numero = numero;
        this.fechaEnvio = LocalDateTime.now();
        this.estado = CompraCotizacionEstado.ENVIADA;
        proveedores.stream()
                .filter(p -> p.getEstado() != CompraCotizacionProveedorEstado.ANULADA)
                .forEach(p -> {
                    p.setEstado(CompraCotizacionProveedorEstado.ENVIADA);
                    p.setFechaEnvio(this.fechaEnvio);
                });
    }

    public void anular(String motivo) {
        if (estado == null || !estado.esAnulable()) {
            throw new IllegalStateException("No se puede anular el proceso en estado " + estado);
        }
        this.estado = CompraCotizacionEstado.ANULADA;
        if (motivo != null && !motivo.isBlank()) {
            this.observacion = (observacion == null || observacion.isBlank()) ? motivo : observacion + " | " + motivo;
        }
    }

    public boolean estaEnviada() {
        return estado == CompraCotizacionEstado.ENVIADA;
    }

    /**
     * Registra la recomendación (etapa 6C): exige un proceso enviado y al menos el mínimo de
     * respuestas (o la excepción con motivo).
     */
    public void recomendar(Long proveedorRecomendadoId, String fundamento, long respuestas) {
        if (estado != CompraCotizacionEstado.ENVIADA) {
            throw new IllegalStateException("Sólo un proceso enviado puede recomendarse (estado actual: " + estado + ")");
        }
        if (proveedorRecomendadoId == null) {
            throw new IllegalStateException("La recomendación requiere el proveedor elegido");
        }
        int requeridas = cantidadMinima != null ? cantidadMinima : 1;
        boolean excepcion = excepcionMotivo != null && !excepcionMotivo.isBlank();
        if (respuestas < requeridas && !excepcion) {
            throw new IllegalStateException("Se requieren " + requeridas
                    + " respuestas para recomendar (hay " + respuestas + ")");
        }
        this.proveedorRecomendadoId = proveedorRecomendadoId;
        this.fundamentoRecomendacion = fundamento;
        this.fechaRecomendacion = LocalDateTime.now();
        this.estado = CompraCotizacionEstado.RECOMENDADA;
    }

    public void cerrar() {
        if (estado != CompraCotizacionEstado.RECOMENDADA) {
            throw new IllegalStateException("Sólo un proceso recomendado puede cerrarse (estado actual: " + estado + ")");
        }
        this.estado = CompraCotizacionEstado.CERRADA;
    }

    /**
     * Resuelve la adjudicación del proceso (etapa 7): exige un proceso recomendado y aplica el
     * estado resultante de la decisión de la autoridad ({@code ADJUDICADA},
     * {@code DEVUELTA_A_COMPRAS} o {@code NO_ADJUDICADA}).
     */
    public void resolverAdjudicacion(CompraCotizacionEstado estadoAdjudicacion) {
        if (estado != CompraCotizacionEstado.RECOMENDADA) {
            throw new IllegalStateException(
                    "Sólo un proceso recomendado puede adjudicarse (estado actual: " + estado + ")");
        }
        if (estadoAdjudicacion != CompraCotizacionEstado.ADJUDICADA
                && estadoAdjudicacion != CompraCotizacionEstado.DEVUELTA_A_COMPRAS
                && estadoAdjudicacion != CompraCotizacionEstado.NO_ADJUDICADA) {
            throw new IllegalStateException("Estado de adjudicación inválido: " + estadoAdjudicacion);
        }
        this.estado = estadoAdjudicacion;
    }

    /**
     * Marca el proceso como formalizado (etapa 8): el proceso adjudicado ya emitió su Orden de
     * Compra. Sólo se permite desde {@code ADJUDICADA}.
     */
    public void formalizar() {
        if (estado != CompraCotizacionEstado.ADJUDICADA) {
            throw new IllegalStateException(
                    "Sólo un proceso adjudicado puede formalizarse (estado actual: " + estado + ")");
        }
        this.estado = CompraCotizacionEstado.FORMALIZADA;
    }
}

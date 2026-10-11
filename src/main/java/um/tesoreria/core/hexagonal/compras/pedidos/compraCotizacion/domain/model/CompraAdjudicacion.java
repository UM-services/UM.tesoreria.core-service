package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Acto de adjudicación de un proceso de cotización (etapa 7): registra la decisión de la autoridad
 * competente sobre la cotización recomendada, con el monto evaluado, la autoridad, el límite
 * aplicado ({@code null} = ilimitado) y el fundamento. Es un registro histórico del acto.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraAdjudicacion {

    private Long compraAdjudicacionId;
    private Long compraCotizacionId;
    private Integer ejercicioId;
    /** Documento de proveedor recomendado de origen (si se adjudica la recomendada). */
    private Long compraCotizacionProveedorId;
    /** Proveedor efectivamente adjudicado (puede diferir de la recomendación). */
    private Integer proveedorAdjudicadoId;
    /** Total cotizado evaluado. */
    private BigDecimal monto;
    private CompraAdjudicacionDecision decision;
    private String fundamento;
    private Long autoridadUsuarioId;
    /** Snapshot del límite al decidir ({@code null} = ilimitado). */
    private BigDecimal limiteAplicado;
    private LocalDateTime fecha;
}

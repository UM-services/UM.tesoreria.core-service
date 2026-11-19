package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alternativa de pago del diseño (lectivo_alternativa).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlternativaEmision {

    private Integer alternativaId;
    private Integer productoId;
    private String titulo;
    private Integer cuotas;
    /**
     * Descuento por pago en término, expresado en puntos porcentuales (0..100).
     */
    private Integer descuentoPagoTermino;
}

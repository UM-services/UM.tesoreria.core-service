package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Pago que participa del asiento de ingreso. Es una copia acotada de {@code PagoDto}: el dominio
 * del slice no depende del DTO de la capa legacy.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoIngreso {

    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer productoId;
    private Integer alternativaId;
    private Integer cuotaId;
    private Integer orden;
    private OffsetDateTime fecha;
    private BigDecimal importe;
    private Integer tipoPagoId;
    private Long chequeraPagoId;
    private Long chequeraPagoReemplazoId;

}

package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Vista mínima de {@code chequera_cuota_reemplazo}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReemplazoCuota {

    private Long clave;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer productoId;
    private Integer alternativaId;
    private Integer cuotaId;
    private Integer mes;
    private Integer anho;
    private OffsetDateTime vencimiento1;
    private BigDecimal importe1;
    private BigDecimal importe2;
    private BigDecimal importe3;
    private Byte pagado;
    private Byte baja;

}

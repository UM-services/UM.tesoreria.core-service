package um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifyable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectivoCuota implements Jsonifyable {
    private Long lectivoCuotaId;
    private Integer facultadId;
    private Integer lectivoId;
    private Integer tipoChequeraId;
    private Integer productoId;
    private Integer alternativaId;
    private Integer cuotaId;
    private Integer mes;
    private Integer anho;
    private OffsetDateTime vencimiento1;
    private BigDecimal importe1;
    private OffsetDateTime vencimiento2;
    private BigDecimal importe2;
    private OffsetDateTime vencimiento3;
    private BigDecimal importe3;
    private Integer tramoId;
}

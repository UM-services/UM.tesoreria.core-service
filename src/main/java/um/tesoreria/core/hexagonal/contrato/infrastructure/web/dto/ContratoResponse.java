package um.tesoreria.core.hexagonal.contrato.infrastructure.web.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContratoResponse {
    private Long contratoId;
    private BigDecimal personaId;
    private Integer documentoId;
    private OffsetDateTime desde;
    private Integer facultadId;
    private Integer planId;
    private String materiaId;
    private Integer geograficaId;
    private Integer cargoMateriaId;
    private OffsetDateTime primerVencimiento;
    private String cargo;
    private BigDecimal montoFijo;
    private BigDecimal canonMensual;
    private BigDecimal canonMensualSinAjuste;
    private OffsetDateTime hasta;
    private String canonMensualLetras;
    private BigDecimal canonTotal;
    private String canonTotalLetras;
    private Integer meses;
    private String mesesLetras;
    private Byte ajuste;
}

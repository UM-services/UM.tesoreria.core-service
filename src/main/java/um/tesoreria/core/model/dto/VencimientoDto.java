package um.tesoreria.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VencimientoDto {

    private Long chequeraCuotaId;
    private Long mercadoPagoContextId;
    private String producto;
    private String periodo;
    private String vencimiento;
    private BigDecimal importe;
    private String initPoint;

}

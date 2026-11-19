package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeudaEmision {

    private Integer cuotas;
    private BigDecimal importe;
    private Integer cuotasPermitidas;
    private boolean excedida;
}

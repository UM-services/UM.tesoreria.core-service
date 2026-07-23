package um.tesoreria.core.hexagonal.setup.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setup {
    private Integer setupId;
    private Integer cuotasPermitidas;
    private BigDecimal cuentaHonorariosPagar;
}

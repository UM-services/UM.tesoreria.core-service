package um.tesoreria.core.hexagonal.setup.infrastructure.web.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetupResponse {
    private Integer setupId;
    private Integer cuotasPermitidas;
    private BigDecimal cuentaHonorariosPagar;
}

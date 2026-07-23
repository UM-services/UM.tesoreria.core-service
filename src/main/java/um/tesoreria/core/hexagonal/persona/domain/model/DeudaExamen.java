package um.tesoreria.core.hexagonal.persona.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeudaExamen {

    private Boolean autorizadoRendir;
    private Boolean matriculaPagada;
    private Integer cuotasAdeudadas;
    private BigDecimal importeAdeudado;
    private Boolean habilitadoTesoreria;

}

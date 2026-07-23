package um.tesoreria.core.hexagonal.persona.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeudaExamen {

    private Boolean autorizadoRendir;
    private Boolean matriculaPagada;
    private Integer cuotasAdeudadas;

}

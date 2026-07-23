package um.tesoreria.core.hexagonal.persona.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeudaExamenResponse {

    @JsonProperty("autorizado_rendir")
    private Boolean autorizadoRendir;

    @JsonProperty("matricula_pagada")
    private Boolean matriculaPagada;

    @JsonProperty("cuotas_adeudadas")
    private Integer cuotasAdeudadas;

}

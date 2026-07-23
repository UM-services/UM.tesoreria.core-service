package um.tesoreria.core.hexagonal.persona.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

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

    @JsonProperty("importe_adeudado")
    private BigDecimal importeAdeudado;

    @JsonProperty("habilitado_tesoreria")
    private Boolean habilitadoTesoreria;

}

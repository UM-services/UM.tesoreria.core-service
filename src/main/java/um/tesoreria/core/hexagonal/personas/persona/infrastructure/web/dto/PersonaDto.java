package um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDto {

    private BigDecimal personaId;
    private Integer documentoId;

    @Builder.Default
    private String apellido = "";

    @Builder.Default
    private String nombre = "";
}

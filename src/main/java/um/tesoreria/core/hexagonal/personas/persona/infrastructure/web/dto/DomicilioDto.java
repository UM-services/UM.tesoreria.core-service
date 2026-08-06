package um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomicilioDto {

    @Builder.Default
    private String emailPersonal = "";

    @Builder.Default
    private String emailInstitucional = "";
}

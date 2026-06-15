package um.tesoreria.core.hexagonal.persona.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaResponse {
    private Long uniqueId;
    private BigDecimal personaId;
    private Integer documentoId;
    private String apellido;
    private String nombre;
    private String sexo;
    private Byte primero;
    private String cuit;
    private String cbu;
    private String password;
    private Byte hpum;
}

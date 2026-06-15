package um.tesoreria.core.hexagonal.persona.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Persona {
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

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}

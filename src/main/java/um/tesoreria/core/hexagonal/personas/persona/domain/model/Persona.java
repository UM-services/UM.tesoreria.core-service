package um.tesoreria.core.hexagonal.personas.persona.domain.model;

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

    @Builder.Default
    private String numeroPrefijo = "";

    @Builder.Default
    private String numeroPosfijo = "";

    private Long guaraniPersona;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}

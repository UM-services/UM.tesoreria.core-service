package um.tesoreria.core.hexagonal.persona.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaKey {

    private String unified;
    private Long uniqueId;
    private BigDecimal personaId;
    private Integer documentoId;

    @Builder.Default
    private String apellido = "";

    @Builder.Default
    private String nombre = "";

    @Builder.Default
    private String sexo = "";

    @Builder.Default
    private Byte primero = 0;

    @Builder.Default
    private String cuit = "";

    @Builder.Default
    private String cbu = "";

    @Builder.Default
    private String password = "";

    @Builder.Default
    private String search = "";

    @Builder.Default
    private Boolean mark_facultad = false;

    public String getApellidoNombre() {
        return apellido + ", " + nombre;
    }
}

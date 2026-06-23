package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoGuarani {

    private Long alumnoId;
    private String legajo;
    private Long persona;
    private Integer propuesta;
    private Integer planVersion;
    private Integer ubicacion;
    private String modalidad;
    private String division;
    private Integer anioCursada;
    private Integer cantidadReadmisiones;
    private String regular;
    private String calidad;
    private BigDecimal coeficiente;
    private PersonaGuarani personaRel;
    private PropuestaGuarani propuestaRel;
    private UbicacionGuarani ubicacionRel;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}

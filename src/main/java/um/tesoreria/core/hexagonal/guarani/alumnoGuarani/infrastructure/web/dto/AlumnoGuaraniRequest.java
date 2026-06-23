package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlumnoGuaraniRequest {

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

}

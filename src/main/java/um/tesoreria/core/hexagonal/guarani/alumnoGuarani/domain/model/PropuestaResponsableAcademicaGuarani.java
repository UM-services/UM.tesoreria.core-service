package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropuestaResponsableAcademicaGuarani {
    private Integer propuesta;
    private PropuestaGuarani propuestaRel;
    private Integer responsableAcademica;
    private String informaAraucanoCodigoUa;
}

package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoDeteccionRequest {

    private Integer alumno;
    private Integer ubicacion;
    private Integer propuesta;
    private String nroDocumento;
    private Short tipoDocumento;
    private Boolean pendiente;

}

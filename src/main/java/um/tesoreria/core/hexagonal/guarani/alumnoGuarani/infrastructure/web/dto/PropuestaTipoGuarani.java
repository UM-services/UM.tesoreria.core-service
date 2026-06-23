package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropuestaTipoGuarani {

    private Integer propuestaTipoId;
    private String descripcion;
    private String otorgaTitulo;
    private String reportaAraucano;
    private String permiteMatricular;
    private String permiteInscribir;
    private String controlRegularidad;
    private String disponibleEnAutogestion;
    private String informarMovilidadInternacional;

}

package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropuestaTipoGuarani {

    private Integer propuestaTipo;
    private String descripcion;
    private String otorgaTitulo;
    private String reportaAraucano;
    private String permiteMatricular;
    private String permiteInscribir;
    private String controlRegularidad;
    private String disponibleEnAutogestion;
    private String informarMovilidadInternacional;

    public String jsonify() {
        return um.tesoreria.core.util.Jsonifier.builder(this).build();
    }

}

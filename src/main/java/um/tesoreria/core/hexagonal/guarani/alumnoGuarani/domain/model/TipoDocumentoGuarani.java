package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoDocumentoGuarani {

    private Integer tipoDocumento;
    private String descripcion;
    private String descAbreviada;
    private Integer ordenPrincipal;
    private String habilitaInscripcion;
    private String tipoDeDato;
    private String puedeEliminarse;
    private String expRegularValidacion;
    private String expRegularMensaje;

    public String jsonify() {
        return um.tesoreria.core.util.Jsonifier.builder(this).build();
    }

}

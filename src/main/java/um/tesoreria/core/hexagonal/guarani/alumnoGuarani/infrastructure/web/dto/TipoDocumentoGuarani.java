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

}

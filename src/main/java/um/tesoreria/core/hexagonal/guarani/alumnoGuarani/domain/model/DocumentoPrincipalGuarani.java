package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoPrincipalGuarani {

    private Long documento;
    private Long persona;
    private Integer paisDocumento;
    private Integer tipoDocumento;
    private TipoDocumentoGuarani tipoDocumentoRel;
    private String nroDocumento;
    private LocalDate fechaOtorgamiento;
    private LocalDate fechaVencimiento;
    private String validadoConRenaper;

    public String jsonify() {
        return um.tesoreria.core.util.Jsonifier.builder(this).build();
    }

}

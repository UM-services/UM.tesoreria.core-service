package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropuestaGuarani {

    private Integer propuestaId;
    private String nombre;
    private String nombreAbreviado;
    private String codigo;
    private Integer propuestaTipo;
    private PropuestaTipoGuarani propuestaTipoRel;
    private String publica;
    private String documentoAlta;
    private LocalDate fechaAlta;
    private String campoDisciplinar;
    private String escalaCumplimiento;
    private String documentoBaja;
    private LocalDate fechaBaja;
    private String aTermino;
    private Long entidad;
    private String estado;

    public String jsonify() {
        return um.tesoreria.core.util.Jsonifier.builder(this).build();
    }

}

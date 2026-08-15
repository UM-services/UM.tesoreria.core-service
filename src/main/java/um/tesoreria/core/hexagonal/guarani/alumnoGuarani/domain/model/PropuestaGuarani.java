package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import lombok.*;
import um.tesoreria.core.util.Jsonifyable;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropuestaGuarani implements Jsonifyable {

    private Integer propuesta;
    private String nombre;
    private String nombreAbreviado;
    private String codigo;
    private Integer propuestaTipo;
    private PropuestaTipoGuarani propuestaTipoRel;
    private List<PropuestaResponsableAcademicaGuarani> responsablesAcademicas;
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

}

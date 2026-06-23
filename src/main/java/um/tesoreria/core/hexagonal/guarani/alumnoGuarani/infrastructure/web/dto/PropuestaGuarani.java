package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropuestaGuarani {

    private Integer propuestaId;
    private String nombre;
    private String nombreAbreviado;
    private String codigo;
    private Integer propuestaTipo;
    private PropuestaTipoGuarani propuestaTipoRel;
    private String publica;
    private String documentoAlta;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaAlta;

    private String campoDisciplinar;
    private String escalaCumplimiento;
    private String documentoBaja;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaBaja;

    private String aTermino;
    private Long entidad;
    private String estado;

}

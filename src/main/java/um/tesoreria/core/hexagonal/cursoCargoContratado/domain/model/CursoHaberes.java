package um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.Facultad;
import um.tesoreria.core.kotlin.model.Geografica;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoHaberes {

    private Long cursoId;
    private String nombre;
    private Byte anual;
    private Byte semestre1;
    private Byte semestre2;
    private Byte adicionalCargaHoraria;
    private Facultad facultad;
    private Geografica geografica;

}

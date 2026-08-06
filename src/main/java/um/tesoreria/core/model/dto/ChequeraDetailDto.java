package um.tesoreria.core.model.dto;

import lombok.Builder;
import lombok.Data;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto.ChequeraSerieDto;
import um.tesoreria.core.kotlin.model.dto.FacultadDto;
import um.tesoreria.core.kotlin.model.dto.LectivoDto;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.PersonaDto;

import java.util.List;

@Data
@Builder
public class ChequeraDetailDto {

    private PersonaDto persona;
    private LectivoDto lectivo;
    private FacultadDto facultad;
    private List<ChequeraSerieDto> chequeraSeries;

}

package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.persona.infrastructure.web.dto.DomicilioDto;
import um.tesoreria.core.hexagonal.persona.infrastructure.web.dto.PersonaDto;
import um.tesoreria.core.kotlin.model.dto.ArancelTipoDto;
import um.tesoreria.core.kotlin.model.dto.ChequeraCuotaDto;
import um.tesoreria.core.kotlin.model.dto.FacultadDto;
import um.tesoreria.core.kotlin.model.dto.GeograficaDto;
import um.tesoreria.core.kotlin.model.dto.LectivoDto;
import um.tesoreria.core.kotlin.model.dto.TipoChequeraDto;
import um.tesoreria.core.util.Jsonifier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraSerieDto {

    private Long chequeraSerieId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fecha;

    @Builder.Default
    private String observaciones = "";

    @Builder.Default
    private Integer alternativaId = 0;

    private FacultadDto facultad;
    private TipoChequeraDto tipoChequera;
    private PersonaDto persona;
    private DomicilioDto mails;
    private LectivoDto lectivo;
    private ArancelTipoDto arancelTipo;
    private GeograficaDto geografica;
    private List<ChequeraCuotaDto> chequeraCuotas;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}

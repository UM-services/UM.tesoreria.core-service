package um.tesoreria.core.hexagonal.persona.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.persona.infrastructure.web.dto.DomicilioDto;
import um.tesoreria.core.hexagonal.persona.infrastructure.web.dto.PersonaDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionFullDto {

    private InscripcionDto inscripcion;
    private InscripcionPagoDto inscripcionPago;
    private PersonaDto personaPago;
    private DomicilioDto domicilioPago;

}

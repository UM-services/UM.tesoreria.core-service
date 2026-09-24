package um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaSugerencia;

import java.math.BigDecimal;

public record PersonaSugerenciaResponse(BigDecimal personaId, Integer documentoId, String documento,
                                       String apellido, String nombre) {
    public static PersonaSugerenciaResponse from(PersonaSugerencia persona) {
        return new PersonaSugerenciaResponse(persona.personaId(), persona.documentoId(), persona.documento(),
                persona.apellido(), persona.nombre());
    }
}

package um.tesoreria.core.hexagonal.personas.persona.domain.model;

import java.math.BigDecimal;

public record PersonaSugerencia(BigDecimal personaId, Integer documentoId, String documento,
                               String apellido, String nombre) {
}

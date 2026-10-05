package um.tesoreria.core.hexagonal.personas.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;

import java.math.BigDecimal;
import java.util.List;

/**
 * Busca todas las filas de persona registradas bajo un mismo numero de documento,
 * sin exigir el tipo de documento (varios tipos pueden compartir numero: LE/LC y DNI).
 */
public interface GetPersonasByNumeroUseCase {

    List<Persona> findAllByNumeroDocumento(BigDecimal numeroDocumento);
}

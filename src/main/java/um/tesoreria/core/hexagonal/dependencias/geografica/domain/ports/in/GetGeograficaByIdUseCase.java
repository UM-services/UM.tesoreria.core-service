package um.tesoreria.core.hexagonal.dependencias.geografica.domain.ports.in;

import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;

import java.util.Optional;

public interface GetGeograficaByIdUseCase {
    Optional<Geografica> getGeograficaById(Integer geograficaId);
}

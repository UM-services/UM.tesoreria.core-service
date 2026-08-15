package um.tesoreria.core.hexagonal.dependencias.geografica.domain.ports.out;

import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;

import java.util.List;
import java.util.Optional;

public interface GeograficaRepository {

    List<Geografica> findAll();
    List<Geografica> findAllByGeograficaId(Integer geograficaId);
    Optional<Geografica> findByGeograficaId(Integer geograficaId);

}

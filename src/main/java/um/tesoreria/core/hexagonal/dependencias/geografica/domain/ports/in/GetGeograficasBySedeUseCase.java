package um.tesoreria.core.hexagonal.dependencias.geografica.domain.ports.in;

import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;

import java.util.List;

public interface GetGeograficasBySedeUseCase {

    List<Geografica> getGeograficasBySede(Integer geograficaId);

}

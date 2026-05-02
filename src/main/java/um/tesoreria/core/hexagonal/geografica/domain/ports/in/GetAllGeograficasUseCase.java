package um.tesoreria.core.hexagonal.geografica.domain.ports.in;

import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;

import java.util.List;

public interface GetAllGeograficasUseCase {
    List<Geografica> getAllGeograficas();
}

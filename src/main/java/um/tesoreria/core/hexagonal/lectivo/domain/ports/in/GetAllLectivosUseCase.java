package um.tesoreria.core.hexagonal.lectivo.domain.ports.in;

import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import java.util.List;

public interface GetAllLectivosUseCase {
    List<Lectivo> getAllLectivos();
}

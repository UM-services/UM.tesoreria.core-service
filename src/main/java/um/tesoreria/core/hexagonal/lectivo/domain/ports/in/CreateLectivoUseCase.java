package um.tesoreria.core.hexagonal.lectivo.domain.ports.in;

import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;

public interface CreateLectivoUseCase {
    Lectivo createLectivo(Lectivo lectivo);
}

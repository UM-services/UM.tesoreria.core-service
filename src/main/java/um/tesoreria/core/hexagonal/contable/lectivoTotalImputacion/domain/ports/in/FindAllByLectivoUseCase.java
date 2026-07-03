package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;

import java.util.List;

public interface FindAllByLectivoUseCase {
    List<LectivoTotalImputacion> findAllByLectivo(Integer lectivoId);
}

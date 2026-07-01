package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in;
import java.util.Optional;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
public interface UpdateLectivoTotalImputacionUseCase {
    Optional<LectivoTotalImputacion> update(Long id, LectivoTotalImputacion lectivoTotalImputacion);
}

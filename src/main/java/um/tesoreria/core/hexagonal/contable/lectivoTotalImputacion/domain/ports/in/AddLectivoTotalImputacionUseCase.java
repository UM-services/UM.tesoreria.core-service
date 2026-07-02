package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
public interface AddLectivoTotalImputacionUseCase {
    LectivoTotalImputacion add(LectivoTotalImputacion lectivoTotalImputacion);
}

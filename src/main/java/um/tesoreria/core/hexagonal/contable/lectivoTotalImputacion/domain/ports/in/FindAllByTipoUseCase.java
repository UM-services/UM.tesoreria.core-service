package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in;
import java.util.List;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
public interface FindAllByTipoUseCase {
    List<LectivoTotalImputacion> findAllByTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId);
}

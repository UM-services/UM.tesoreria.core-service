package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in;
import java.util.Optional;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
public interface FindByProductoUseCase {
    Optional<LectivoTotalImputacion> findByProducto(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId);
}

package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import java.util.List;
import java.util.Optional;

public interface LectivoTotalImputacionRepository {
    List<LectivoTotalImputacion> findAllByTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId);
    Optional<LectivoTotalImputacion> findByProducto(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId);
    LectivoTotalImputacion add(LectivoTotalImputacion lectivoTotalImputacion);
    Optional<LectivoTotalImputacion> update(Long id, LectivoTotalImputacion lectivoTotalImputacion);
    Optional<LectivoTotalImputacion> findById(Long id);
}

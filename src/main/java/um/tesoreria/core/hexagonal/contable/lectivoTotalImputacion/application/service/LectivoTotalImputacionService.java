package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LectivoTotalImputacionService {

    private final FindAllByTipoUseCase findAllByTipoUseCase;
    private final FindByProductoUseCase findByProductoUseCase;
    private final AddLectivoTotalImputacionUseCase addLectivoTotalImputacionUseCase;
    private final UpdateLectivoTotalImputacionUseCase updateLectivoTotalImputacionUseCase;

    public List<LectivoTotalImputacion> findAllByTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
        return findAllByTipoUseCase.findAllByTipo(facultadId, lectivoId, tipoChequeraId);
    }

    public Optional<LectivoTotalImputacion> findByProducto(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId) {
        return findByProductoUseCase.findByProducto(facultadId, lectivoId, tipoChequeraId, productoId);
    }

    public LectivoTotalImputacion add(LectivoTotalImputacion lectivoTotalImputacion) {
        return addLectivoTotalImputacionUseCase.add(lectivoTotalImputacion);
    }

    public Optional<LectivoTotalImputacion> update(Long id, LectivoTotalImputacion lectivoTotalImputacion) {
        return updateLectivoTotalImputacionUseCase.update(id, lectivoTotalImputacion);
    }

}

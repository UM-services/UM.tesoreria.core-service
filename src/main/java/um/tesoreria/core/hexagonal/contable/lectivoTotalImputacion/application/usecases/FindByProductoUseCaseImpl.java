package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in.FindByProductoUseCase;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.out.LectivoTotalImputacionRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindByProductoUseCaseImpl implements FindByProductoUseCase {
    private final LectivoTotalImputacionRepository repository;
    @Override
    public Optional<LectivoTotalImputacion> findByProducto(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId) {
        return repository.findByProducto(facultadId, lectivoId, tipoChequeraId, productoId);
    }
}

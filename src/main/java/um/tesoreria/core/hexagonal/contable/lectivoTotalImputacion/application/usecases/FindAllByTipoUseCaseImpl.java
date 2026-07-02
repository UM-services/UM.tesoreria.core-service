package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in.FindAllByTipoUseCase;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.out.LectivoTotalImputacionRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllByTipoUseCaseImpl implements FindAllByTipoUseCase {
    private final LectivoTotalImputacionRepository repository;
    @Override
    public List<LectivoTotalImputacion> findAllByTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
        return repository.findAllByTipo(facultadId, lectivoId, tipoChequeraId);
    }
}

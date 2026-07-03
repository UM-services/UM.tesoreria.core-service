package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in.FindAllByLectivoUseCase;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.out.LectivoTotalImputacionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllByLectivoUseCaseImpl implements FindAllByLectivoUseCase {

    private final LectivoTotalImputacionRepository repository;

    @Override
    public List<LectivoTotalImputacion> findAllByLectivo(Integer lectivoId) {
        return repository.findAllByLectivo(lectivoId);
    }

}

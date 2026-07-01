package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in.UpdateLectivoTotalImputacionUseCase;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.out.LectivoTotalImputacionRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateLectivoTotalImputacionUseCaseImpl implements UpdateLectivoTotalImputacionUseCase {
    private final LectivoTotalImputacionRepository repository;
    @Override
    public Optional<LectivoTotalImputacion> update(Long id, LectivoTotalImputacion lectivoTotalImputacion) {
        return repository.update(id, lectivoTotalImputacion);
    }
}

package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in.AddLectivoTotalImputacionUseCase;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.out.LectivoTotalImputacionRepository;

@Component
@RequiredArgsConstructor
public class AddLectivoTotalImputacionUseCaseImpl implements AddLectivoTotalImputacionUseCase {
    private final LectivoTotalImputacionRepository repository;
    @Override
    public LectivoTotalImputacion add(LectivoTotalImputacion lectivoTotalImputacion) {
        return repository.add(lectivoTotalImputacion);
    }
}

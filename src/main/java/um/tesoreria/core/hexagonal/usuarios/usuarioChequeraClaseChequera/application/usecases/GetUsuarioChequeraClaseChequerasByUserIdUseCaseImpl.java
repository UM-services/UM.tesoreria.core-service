package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.in.GetUsuarioChequeraClaseChequerasByUserIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.out.UsuarioChequeraClaseChequeraRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetUsuarioChequeraClaseChequerasByUserIdUseCaseImpl implements GetUsuarioChequeraClaseChequerasByUserIdUseCase {
    private final UsuarioChequeraClaseChequeraRepository repository;

    @Override
    public List<UsuarioChequeraClaseChequera> getByUserId(Long userId) {
        return repository.findAllByUserId(userId);
    }
}

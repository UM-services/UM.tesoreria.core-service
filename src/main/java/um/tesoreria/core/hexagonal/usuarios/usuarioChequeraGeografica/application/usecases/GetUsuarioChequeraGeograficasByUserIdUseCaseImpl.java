package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.in.GetUsuarioChequeraGeograficasByUserIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.out.UsuarioChequeraGeograficaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetUsuarioChequeraGeograficasByUserIdUseCaseImpl implements GetUsuarioChequeraGeograficasByUserIdUseCase {
    private final UsuarioChequeraGeograficaRepository repository;

    @Override
    public List<UsuarioChequeraGeografica> getByUserId(Long userId) {
        return repository.findAllByUserId(userId);
    }
}

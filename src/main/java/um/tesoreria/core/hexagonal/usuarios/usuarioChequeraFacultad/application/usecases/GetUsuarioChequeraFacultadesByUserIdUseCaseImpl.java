package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.in.GetUsuarioChequeraFacultadesByUserIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.out.UsuarioChequeraFacultadRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetUsuarioChequeraFacultadesByUserIdUseCaseImpl implements GetUsuarioChequeraFacultadesByUserIdUseCase {
    private final UsuarioChequeraFacultadRepository repository;

    @Override
    public List<UsuarioChequeraFacultad> getByUserId(Long userId) {
        return repository.findAllByUserId(userId);
    }
}

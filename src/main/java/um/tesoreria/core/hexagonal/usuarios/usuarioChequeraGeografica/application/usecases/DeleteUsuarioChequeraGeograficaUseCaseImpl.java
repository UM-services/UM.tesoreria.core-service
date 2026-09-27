package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.exception.UsuarioChequeraGeograficaException;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.in.DeleteUsuarioChequeraGeograficaUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.out.UsuarioChequeraGeograficaRepository;

@Component
@RequiredArgsConstructor
public class DeleteUsuarioChequeraGeograficaUseCaseImpl implements DeleteUsuarioChequeraGeograficaUseCase {

    private final UsuarioChequeraGeograficaRepository repository;

    @Override
    public void deleteUsuarioChequeraGeografica(Long userId, Integer geograficaId) {
        if (userId == null || geograficaId == null) {
            throw new UsuarioChequeraGeograficaException("userId y geograficaId son requeridos");
        }
        repository.findByUserIdAndGeograficaId(userId, geograficaId)
                .orElseThrow(() -> new UsuarioChequeraGeograficaException(
                        "El usuario " + userId + " no tiene asignada la sede " + geograficaId));
        repository.deleteByUserIdAndGeograficaId(userId, geograficaId);
    }
}

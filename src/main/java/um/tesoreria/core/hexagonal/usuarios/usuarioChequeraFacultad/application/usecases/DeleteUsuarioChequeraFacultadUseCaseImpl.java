package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.exception.UsuarioChequeraFacultadException;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.in.DeleteUsuarioChequeraFacultadUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.out.UsuarioChequeraFacultadRepository;

@Component
@RequiredArgsConstructor
public class DeleteUsuarioChequeraFacultadUseCaseImpl implements DeleteUsuarioChequeraFacultadUseCase {

    private final UsuarioChequeraFacultadRepository repository;

    @Override
    public void deleteUsuarioChequeraFacultad(Long userId, Integer facultadId) {
        if (userId == null || facultadId == null) {
            throw new UsuarioChequeraFacultadException("userId y facultadId son requeridos");
        }
        repository.findByUserIdAndFacultadId(userId, facultadId)
                .orElseThrow(() -> new UsuarioChequeraFacultadException(
                        "El usuario " + userId + " no tiene asignada la facultad " + facultadId));
        repository.deleteByUserIdAndFacultadId(userId, facultadId);
    }
}

package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.exception.UsuarioChequeraClaseChequeraException;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.in.DeleteUsuarioChequeraClaseChequeraUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.out.UsuarioChequeraClaseChequeraRepository;

@Component
@RequiredArgsConstructor
public class DeleteUsuarioChequeraClaseChequeraUseCaseImpl implements DeleteUsuarioChequeraClaseChequeraUseCase {

    private final UsuarioChequeraClaseChequeraRepository repository;

    @Override
    public void deleteUsuarioChequeraClaseChequera(Long userId, Integer claseChequeraId) {
        if (userId == null || claseChequeraId == null) {
            throw new UsuarioChequeraClaseChequeraException("userId y claseChequeraId son requeridos");
        }
        repository.findByUserIdAndClaseChequeraId(userId, claseChequeraId)
                .orElseThrow(() -> new UsuarioChequeraClaseChequeraException(
                        "El usuario " + userId + " no tiene asignada la clase de chequera " + claseChequeraId));
        repository.deleteByUserIdAndClaseChequeraId(userId, claseChequeraId);
    }
}

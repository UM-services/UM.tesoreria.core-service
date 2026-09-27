package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.in.GetClaseChequeraByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.GetUsuarioByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.exception.UsuarioChequeraClaseChequeraException;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.in.CreateUsuarioChequeraClaseChequeraUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.out.UsuarioChequeraClaseChequeraRepository;

@Component
@RequiredArgsConstructor
public class CreateUsuarioChequeraClaseChequeraUseCaseImpl implements CreateUsuarioChequeraClaseChequeraUseCase {

    private final UsuarioChequeraClaseChequeraRepository repository;
    // Excepción cross-slice autorizada: valida las referencias consumiendo los puertos
    // públicos de los slices usuarios.usuario y chequera.claseChequera
    // (patrón de la ClaseChequera ya anclada en el dominio de este slice).
    private final GetUsuarioByIdUseCase getUsuarioByIdUseCase;
    private final GetClaseChequeraByIdUseCase getClaseChequeraByIdUseCase;

    @Override
    public UsuarioChequeraClaseChequera createUsuarioChequeraClaseChequera(UsuarioChequeraClaseChequera usuarioChequeraClaseChequera) {
        if (usuarioChequeraClaseChequera == null) {
            throw new UsuarioChequeraClaseChequeraException("La asignación es requerida");
        }
        Long userId = usuarioChequeraClaseChequera.getUserId();
        Integer claseChequeraId = usuarioChequeraClaseChequera.getClaseChequeraId();
        if (userId == null) {
            throw new UsuarioChequeraClaseChequeraException("userId es requerido");
        }
        if (claseChequeraId == null) {
            throw new UsuarioChequeraClaseChequeraException("claseChequeraId es requerido");
        }
        getUsuarioByIdUseCase.getUsuarioById(userId)
                .orElseThrow(() -> new UsuarioChequeraClaseChequeraException("Cannot find Usuario with id: " + userId));
        getClaseChequeraByIdUseCase.getClaseChequeraById(claseChequeraId)
                .orElseThrow(() -> new UsuarioChequeraClaseChequeraException("Cannot find ClaseChequera with id: " + claseChequeraId));
        // Idempotente: si la clase ya está asignada al usuario, se devuelve la asignación existente.
        return repository.findByUserIdAndClaseChequeraId(userId, claseChequeraId)
                .orElseGet(() -> repository.save(UsuarioChequeraClaseChequera.builder()
                        .userId(userId)
                        .claseChequeraId(claseChequeraId)
                        .build()));
    }
}

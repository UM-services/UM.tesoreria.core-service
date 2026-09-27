package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.geografica.domain.ports.in.GetGeograficaByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.GetUsuarioByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.exception.UsuarioChequeraGeograficaException;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.in.CreateUsuarioChequeraGeograficaUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.out.UsuarioChequeraGeograficaRepository;

@Component
@RequiredArgsConstructor
public class CreateUsuarioChequeraGeograficaUseCaseImpl implements CreateUsuarioChequeraGeograficaUseCase {

    private final UsuarioChequeraGeograficaRepository repository;
    // Excepción cross-slice autorizada: valida las referencias consumiendo los puertos
    // públicos de los slices usuarios.usuario y dependencias.geografica
    // (patrón de la Geografica ya anclada en el dominio de este slice).
    private final GetUsuarioByIdUseCase getUsuarioByIdUseCase;
    private final GetGeograficaByIdUseCase getGeograficaByIdUseCase;

    @Override
    public UsuarioChequeraGeografica createUsuarioChequeraGeografica(UsuarioChequeraGeografica usuarioChequeraGeografica) {
        if (usuarioChequeraGeografica == null) {
            throw new UsuarioChequeraGeograficaException("La asignación es requerida");
        }
        Long userId = usuarioChequeraGeografica.getUserId();
        Integer geograficaId = usuarioChequeraGeografica.getGeograficaId();
        if (userId == null) {
            throw new UsuarioChequeraGeograficaException("userId es requerido");
        }
        if (geograficaId == null) {
            throw new UsuarioChequeraGeograficaException("geograficaId es requerido");
        }
        getUsuarioByIdUseCase.getUsuarioById(userId)
                .orElseThrow(() -> new UsuarioChequeraGeograficaException("Cannot find Usuario with id: " + userId));
        getGeograficaByIdUseCase.getGeograficaById(geograficaId)
                .orElseThrow(() -> new UsuarioChequeraGeograficaException("Cannot find Geografica with id: " + geograficaId));
        // Idempotente: si la sede ya está asignada al usuario, se devuelve la asignación existente.
        return repository.findByUserIdAndGeograficaId(userId, geograficaId)
                .orElseGet(() -> repository.save(UsuarioChequeraGeografica.builder()
                        .userId(userId)
                        .geograficaId(geograficaId)
                        .build()));
    }
}

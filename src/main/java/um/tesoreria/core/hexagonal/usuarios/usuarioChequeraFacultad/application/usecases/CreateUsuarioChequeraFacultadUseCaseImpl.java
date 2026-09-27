package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.ports.in.GetFacultadByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.GetUsuarioByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.exception.UsuarioChequeraFacultadException;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.in.CreateUsuarioChequeraFacultadUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.out.UsuarioChequeraFacultadRepository;

@Component
@RequiredArgsConstructor
public class CreateUsuarioChequeraFacultadUseCaseImpl implements CreateUsuarioChequeraFacultadUseCase {

    private final UsuarioChequeraFacultadRepository repository;
    // Excepción cross-slice autorizada: valida las referencias consumiendo los puertos
    // públicos de los slices usuarios.usuario y dependencias.facultad
    // (patrón de Usuario y Facultad ya anclados en el dominio de este slice).
    private final GetUsuarioByIdUseCase getUsuarioByIdUseCase;
    private final GetFacultadByIdUseCase getFacultadByIdUseCase;

    @Override
    public UsuarioChequeraFacultad createUsuarioChequeraFacultad(UsuarioChequeraFacultad usuarioChequeraFacultad) {
        if (usuarioChequeraFacultad == null) {
            throw new UsuarioChequeraFacultadException("La asignación es requerida");
        }
        Long userId = usuarioChequeraFacultad.getUserId();
        Integer facultadId = usuarioChequeraFacultad.getFacultadId();
        if (userId == null) {
            throw new UsuarioChequeraFacultadException("userId es requerido");
        }
        if (facultadId == null) {
            throw new UsuarioChequeraFacultadException("facultadId es requerido");
        }
        getUsuarioByIdUseCase.getUsuarioById(userId)
                .orElseThrow(() -> new UsuarioChequeraFacultadException("Cannot find Usuario with id: " + userId));
        getFacultadByIdUseCase.getById(facultadId)
                .orElseThrow(() -> new UsuarioChequeraFacultadException("Cannot find Facultad with id: " + facultadId));
        // Idempotente: si la facultad ya está asignada al usuario, se devuelve la asignación existente.
        return repository.findByUserIdAndFacultadId(userId, facultadId)
                .orElseGet(() -> repository.save(UsuarioChequeraFacultad.builder()
                        .userId(userId)
                        .facultadId(facultadId)
                        .build()));
    }
}

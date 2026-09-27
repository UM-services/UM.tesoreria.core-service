package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.in.CreateUsuarioChequeraFacultadUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.in.DeleteUsuarioChequeraFacultadUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.in.GetUsuarioChequeraFacultadesByUserIdUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioChequeraFacultadService {

    private final GetUsuarioChequeraFacultadesByUserIdUseCase getUsuarioChequeraFacultadesByUserIdUseCase;
    private final CreateUsuarioChequeraFacultadUseCase createUsuarioChequeraFacultadUseCase;
    private final DeleteUsuarioChequeraFacultadUseCase deleteUsuarioChequeraFacultadUseCase;

    public List<UsuarioChequeraFacultad> findAllByUserId(Long userId) {
        return getUsuarioChequeraFacultadesByUserIdUseCase.getByUserId(userId);
    }

    public UsuarioChequeraFacultad add(UsuarioChequeraFacultad usuarioChequeraFacultad) {
        return createUsuarioChequeraFacultadUseCase.createUsuarioChequeraFacultad(usuarioChequeraFacultad);
    }

    public void delete(Long userId, Integer facultadId) {
        deleteUsuarioChequeraFacultadUseCase.deleteUsuarioChequeraFacultad(userId, facultadId);
    }

}

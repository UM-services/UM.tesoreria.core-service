package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;

import java.util.List;

public interface GetUsuarioChequeraFacultadesByUserIdUseCase {
    List<UsuarioChequeraFacultad> getByUserId(Long userId);
}

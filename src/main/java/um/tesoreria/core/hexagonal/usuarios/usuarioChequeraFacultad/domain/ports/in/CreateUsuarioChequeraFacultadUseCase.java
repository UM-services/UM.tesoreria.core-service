package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;

public interface CreateUsuarioChequeraFacultadUseCase {
    UsuarioChequeraFacultad createUsuarioChequeraFacultad(UsuarioChequeraFacultad usuarioChequeraFacultad);
}

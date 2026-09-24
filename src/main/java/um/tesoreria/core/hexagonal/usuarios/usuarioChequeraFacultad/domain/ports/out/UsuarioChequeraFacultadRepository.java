package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;

import java.util.List;

public interface UsuarioChequeraFacultadRepository {
    List<UsuarioChequeraFacultad> findAllByUserId(Long userId);
}

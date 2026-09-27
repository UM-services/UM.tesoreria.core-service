package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;

import java.util.List;
import java.util.Optional;

public interface UsuarioChequeraFacultadRepository {
    List<UsuarioChequeraFacultad> findAllByUserId(Long userId);

    Optional<UsuarioChequeraFacultad> findByUserIdAndFacultadId(Long userId, Integer facultadId);

    UsuarioChequeraFacultad save(UsuarioChequeraFacultad usuarioChequeraFacultad);

    void deleteByUserIdAndFacultadId(Long userId, Integer facultadId);
}

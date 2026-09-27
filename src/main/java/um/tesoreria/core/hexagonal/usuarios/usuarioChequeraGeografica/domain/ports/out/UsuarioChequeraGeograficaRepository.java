package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;

import java.util.List;
import java.util.Optional;

public interface UsuarioChequeraGeograficaRepository {
    List<UsuarioChequeraGeografica> findAllByUserId(Long userId);

    Optional<UsuarioChequeraGeografica> findByUserIdAndGeograficaId(Long userId, Integer geograficaId);

    UsuarioChequeraGeografica save(UsuarioChequeraGeografica usuarioChequeraGeografica);

    void deleteByUserIdAndGeograficaId(Long userId, Integer geograficaId);
}

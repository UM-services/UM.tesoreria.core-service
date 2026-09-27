package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;

import java.util.List;
import java.util.Optional;

public interface UsuarioChequeraClaseChequeraRepository {
    List<UsuarioChequeraClaseChequera> findAllByUserId(Long userId);

    Optional<UsuarioChequeraClaseChequera> findByUserIdAndClaseChequeraId(Long userId, Integer claseChequeraId);

    UsuarioChequeraClaseChequera save(UsuarioChequeraClaseChequera usuarioChequeraClaseChequera);

    void deleteByUserIdAndClaseChequeraId(Long userId, Integer claseChequeraId);
}

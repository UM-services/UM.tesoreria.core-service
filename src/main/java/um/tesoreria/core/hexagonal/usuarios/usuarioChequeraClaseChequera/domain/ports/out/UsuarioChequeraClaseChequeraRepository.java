package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;

import java.util.List;

public interface UsuarioChequeraClaseChequeraRepository {
    List<UsuarioChequeraClaseChequera> findAllByUserId(Long userId);
}

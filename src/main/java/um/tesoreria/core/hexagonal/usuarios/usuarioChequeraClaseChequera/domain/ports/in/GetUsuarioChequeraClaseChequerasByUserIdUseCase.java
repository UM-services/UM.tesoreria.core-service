package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;

import java.util.List;

public interface GetUsuarioChequeraClaseChequerasByUserIdUseCase {
    List<UsuarioChequeraClaseChequera> getByUserId(Long userId);
}

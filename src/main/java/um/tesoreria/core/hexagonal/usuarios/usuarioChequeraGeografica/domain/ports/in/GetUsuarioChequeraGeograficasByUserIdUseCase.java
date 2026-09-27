package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;

import java.util.List;

public interface GetUsuarioChequeraGeograficasByUserIdUseCase {
    List<UsuarioChequeraGeografica> getByUserId(Long userId);
}

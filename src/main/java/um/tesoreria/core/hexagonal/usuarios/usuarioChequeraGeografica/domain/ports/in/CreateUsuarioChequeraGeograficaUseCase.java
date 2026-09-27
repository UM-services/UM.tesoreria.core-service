package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;

public interface CreateUsuarioChequeraGeograficaUseCase {
    UsuarioChequeraGeografica createUsuarioChequeraGeografica(UsuarioChequeraGeografica usuarioChequeraGeografica);
}

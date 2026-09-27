package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;

public interface CreateUsuarioChequeraClaseChequeraUseCase {
    UsuarioChequeraClaseChequera createUsuarioChequeraClaseChequera(UsuarioChequeraClaseChequera usuarioChequeraClaseChequera);
}

package um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;

public interface GetBancariaByIdUseCase {
    /** Si la cuenta no existe falla con BancariaNoEncontradaException (capa de aplicación). */
    Bancaria getBancariaById(Long bancariaId);
}

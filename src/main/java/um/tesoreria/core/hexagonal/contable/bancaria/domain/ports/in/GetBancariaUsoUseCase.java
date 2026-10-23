package um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaUso;

public interface GetBancariaUsoUseCase {
    /** Si la cuenta no existe falla con BancariaNoEncontradaException (capa de aplicación). */
    BancariaUso getBancariaUso(Long bancariaId);
}

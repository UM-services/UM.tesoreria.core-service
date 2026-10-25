package um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaUso;

public interface BancariaUsoPort {
    /** Corta en la primera referencia (valores o libro banco). */
    boolean enUso(Long bancariaId);

    BancariaUso contar(Long bancariaId);
}

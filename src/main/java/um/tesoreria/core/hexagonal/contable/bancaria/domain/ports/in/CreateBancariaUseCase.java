package um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaCambios;

public interface CreateBancariaUseCase {
    Bancaria createBancaria(BancariaCambios pedido);
}

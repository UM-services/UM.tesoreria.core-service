package um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model.BancoMovimiento;

public interface CreateBancoMovimientoUseCase {

    BancoMovimiento createBancoMovimiento(BancoMovimiento bancoMovimiento);
}

package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import java.util.List;

public interface DeleteAllByCuentaMovimientoIdInUseCase {
    void deleteAllByCuentaMovimientoIdIn(List<Long> cuentaMovimientoIds);
}

package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

public interface DeleteByCuentaMovimientoIdUseCase {
    void deleteByCuentaMovimientoId(Long cuentaMovimientoId);
}

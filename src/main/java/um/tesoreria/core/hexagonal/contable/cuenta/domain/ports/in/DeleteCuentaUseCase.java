package um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in;
import java.math.BigDecimal;
public interface DeleteCuentaUseCase {
    void deleteCuenta(BigDecimal numeroCuenta);
}

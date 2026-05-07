package um.tesoreria.core.hexagonal.cuenta.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.in.RecalculaGradosUseCase;
import um.tesoreria.core.hexagonal.cuenta.domain.ports.out.CuentaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
@Component
@RequiredArgsConstructor
public class RecalculaGradosUseCaseImpl implements RecalculaGradosUseCase {
    private final CuentaRepository repository;
    @Override
    public String recalculaGrados() {
        List<Cuenta> cuentas = repository.findAll();
        for (Cuenta cuenta : cuentas) {
            cuenta.setGrado(5);
            BigDecimal factor = new BigDecimal(10000);
            cuenta.setGrado4(cuenta.getNumeroCuenta().divide(factor).setScale(0, RoundingMode.DOWN).multiply(factor)
                            .setScale(0, RoundingMode.DOWN));
            factor = new BigDecimal(1000000);
            cuenta.setGrado3(cuenta.getNumeroCuenta().divide(factor).setScale(0, RoundingMode.DOWN).multiply(factor)
                            .setScale(0, RoundingMode.DOWN));
            factor = new BigDecimal(100000000);
            cuenta.setGrado2(cuenta.getNumeroCuenta().divide(factor).setScale(0, RoundingMode.DOWN).multiply(factor)
                            .setScale(0, RoundingMode.DOWN));
            factor = new BigDecimal(10000000000L);
            cuenta.setGrado1(cuenta.getNumeroCuenta().divide(factor).setScale(0, RoundingMode.DOWN).multiply(factor)
                            .setScale(0, RoundingMode.DOWN));
            if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado4()) == 0) {
                    cuenta.setGrado(4);
            }
            if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado3()) == 0) {
                    cuenta.setGrado(3);
            }
            if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado2()) == 0) {
                    cuenta.setGrado(2);
            }
            if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado1()) == 0) {
                    cuenta.setGrado(1);
            }
        }
        repository.saveAll(cuentas);
        return "Ok";
    }
}

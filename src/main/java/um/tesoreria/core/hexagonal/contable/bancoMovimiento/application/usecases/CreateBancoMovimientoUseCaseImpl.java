package um.tesoreria.core.hexagonal.contable.bancoMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model.BancoMovimiento;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.ports.in.CreateBancoMovimientoUseCase;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.ports.out.BancoMovimientoRepository;

@Component
@RequiredArgsConstructor
public class CreateBancoMovimientoUseCaseImpl implements CreateBancoMovimientoUseCase {

    private final BancoMovimientoRepository bancoMovimientoRepository;

    @Override
    public BancoMovimiento createBancoMovimiento(BancoMovimiento bancoMovimiento) {
        return bancoMovimientoRepository.save(bancoMovimiento);
    }
}

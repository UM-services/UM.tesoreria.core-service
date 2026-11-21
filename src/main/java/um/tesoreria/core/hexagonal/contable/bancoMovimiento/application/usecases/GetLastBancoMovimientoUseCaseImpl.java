package um.tesoreria.core.hexagonal.contable.bancoMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model.BancoMovimiento;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.ports.in.GetLastBancoMovimientoUseCase;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.ports.out.BancoMovimientoRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetLastBancoMovimientoUseCaseImpl implements GetLastBancoMovimientoUseCase {

    private final BancoMovimientoRepository bancoMovimientoRepository;

    @Override
    public Optional<BancoMovimiento> getLastBancoMovimiento(Long bancariaId, OffsetDateTime fecha) {
        return bancoMovimientoRepository.findByBancariaIdAndFechaOrderByOrdenDesc(bancariaId, fecha);
    }
}

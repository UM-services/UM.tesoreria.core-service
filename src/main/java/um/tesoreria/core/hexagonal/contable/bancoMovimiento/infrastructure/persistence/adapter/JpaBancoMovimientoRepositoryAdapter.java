package um.tesoreria.core.hexagonal.contable.bancoMovimiento.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model.BancoMovimiento;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.ports.out.BancoMovimientoRepository;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.infrastructure.persistence.mapper.BancoMovimientoMapper;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.infrastructure.persistence.repository.JpaBancoMovimientoRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaBancoMovimientoRepositoryAdapter implements BancoMovimientoRepository {

    private final JpaBancoMovimientoRepository jpaBancoMovimientoRepository;
    private final BancoMovimientoMapper bancoMovimientoMapper;

    @Override
    public Optional<BancoMovimiento> findByBancariaIdAndFechaOrderByOrdenDesc(Long bancariaId, OffsetDateTime fecha) {
        return jpaBancoMovimientoRepository.findTopByBancariaIdAndFechaOrderByOrdenDesc(bancariaId, fecha)
                .map(bancoMovimientoMapper::toDomain);
    }

    @Override
    public BancoMovimiento save(BancoMovimiento bancoMovimiento) {
        return bancoMovimientoMapper.toDomain(
                jpaBancoMovimientoRepository.save(bancoMovimientoMapper.toEntity(bancoMovimiento)));
    }
}

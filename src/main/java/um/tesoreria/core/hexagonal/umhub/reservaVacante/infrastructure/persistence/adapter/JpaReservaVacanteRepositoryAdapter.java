package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.out.ReservaVacanteRepository;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.entity.ReservaVacanteEntity;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.mapper.ReservaVacanteMapper;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.repository.JpaReservaVacanteRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JpaReservaVacanteRepositoryAdapter implements ReservaVacanteRepository {

    private final JpaReservaVacanteRepository jpaReservaVacanteRepository;
    private final ReservaVacanteMapper reservaVacanteMapper;

    @Override
    public ReservaVacante create(ReservaVacante reservaVacante) {
        log.debug("ReservaVacante -> {}", reservaVacante.jsonify());
        ReservaVacanteEntity entity = reservaVacanteMapper.toEntity(reservaVacante);
        ReservaVacanteEntity saved = jpaReservaVacanteRepository.save(entity);
        return reservaVacanteMapper.toDomainModel(saved);
    }

    @Override
    public Optional<ReservaVacante> findByReservaVacanteId(UUID reservaVacanteId) {
        return jpaReservaVacanteRepository.findByReservaVacanteId(reservaVacanteId)
                .map(reservaVacanteMapper::toDomainModel);
    }
}

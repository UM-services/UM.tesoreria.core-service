package um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;
import um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.entity.MercadoPagoContextEntity;
import um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.mapper.MercadoPagoContextMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaMercadoPagoContextRepositoryAdapter implements MercadoPagoContextRepository {

    private final JpaMercadoPagoContextRepository repository;
    private final MercadoPagoContextMapper mapper;

    @Override
    public List<MercadoPagoContext> findAllByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo) {
        return repository.findAllByChequeraCuotaIdAndActivo(chequeraCuotaId, activo)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<MercadoPagoContext> findAllByChequeraCuotaIdInAndActivo(List<Long> chequeraCuotaIds, Byte activo) {
        return repository.findAllByChequeraCuotaIdInAndActivo(chequeraCuotaIds, activo)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<MercadoPagoContext> findAllByActivoAndFechaVencimientoBetween(Byte activo, OffsetDateTime desde, OffsetDateTime hasta) {
        return repository.findAllByActivoAndFechaVencimientoBetween(activo, desde, hasta)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MercadoPagoContext> findByMercadoPagoContextId(Long mercadoPagoContextId) {
        return repository.findByMercadoPagoContextId(mercadoPagoContextId)
                .map(mapper::toDomainModel);
    }

    @Override
    public Optional<MercadoPagoContext> findByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo) {
        return repository.findByChequeraCuotaIdAndActivo(chequeraCuotaId, activo)
                .map(mapper::toDomainModel);
    }

    @Override
    public Optional<MercadoPagoContext> findByReservaVacanteIdAndActivo(UUID reservaVacanteId, Byte activo) {
        return repository.findByReservaVacanteId(reservaVacanteId)
                .map(mapper::toDomainModel);
    }

    @Override
    public List<MercadoPagoContext> findAllByActivoOrderByMercadoPagoContextIdDesc(Byte activo) {
        return repository.findAllByActivoOrderByMercadoPagoContextIdDesc(activo)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<MercadoPagoContext> findAllByStatusAndChequeraPagoIdIsNull(String status) {
        return repository.findAllByStatusAndChequeraPagoIdIsNull(status)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public MercadoPagoContext save(MercadoPagoContext context) {
        MercadoPagoContextEntity entity = mapper.toEntity(context);
        MercadoPagoContextEntity saved = repository.save(entity);
        return mapper.toDomainModel(saved);
    }

    @Override
    public List<MercadoPagoContext> saveAll(List<MercadoPagoContext> contexts) {
        List<MercadoPagoContextEntity> entities = contexts.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        List<MercadoPagoContextEntity> saved = repository.saveAll(entities);
        return saved.stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }
}

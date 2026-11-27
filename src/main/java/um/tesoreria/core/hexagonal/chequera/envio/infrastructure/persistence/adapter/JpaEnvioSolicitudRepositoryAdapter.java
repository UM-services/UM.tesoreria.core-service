package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitud;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.EnvioSolicitudRepository;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.mapper.EnvioSolicitudMapper;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.repository.JpaEnvioSolicitudRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaEnvioSolicitudRepositoryAdapter implements EnvioSolicitudRepository {

    private final JpaEnvioSolicitudRepository repository;
    private final EnvioSolicitudMapper mapper;

    @Override
    public EnvioSolicitud save(EnvioSolicitud solicitud) {
        return mapper.toDomain(repository.save(mapper.toEntity(solicitud)));
    }

    @Override
    public Optional<EnvioSolicitud> findBySolicitudId(UUID solicitudId) {
        return repository.findBySolicitudId(solicitudId).map(mapper::toDomain);
    }

    @Override
    public Optional<EnvioSolicitud> findBySolicitudIdForUpdate(UUID solicitudId) {
        return repository.findWithLockBySolicitudId(solicitudId).map(mapper::toDomain);
    }

    @Override
    public Optional<EnvioSolicitud> findByChequeraId(Long chequeraId) {
        return repository.findByChequeraId(chequeraId).map(mapper::toDomain);
    }

    @Override
    public Optional<EnvioSolicitud> findByDedupeKey(String dedupeKey) {
        return repository.findByDedupeKey(dedupeKey).map(mapper::toDomain);
    }

}

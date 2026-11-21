package um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoSecuencia;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out.AsientoSecuenciaRepository;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoSecuenciaEntity;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.repository.JpaAsientoSecuenciaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaAsientoSecuenciaRepositoryAdapter implements AsientoSecuenciaRepository {

    private final JpaAsientoSecuenciaRepository repository;

    @Override
    public Optional<AsientoSecuencia> findWithLockByFecha(OffsetDateTime fecha) {
        return repository.findFirstByFecha(fecha).map(this::toDomain);
    }

    @Override
    public AsientoSecuencia save(AsientoSecuencia secuencia) {
        AsientoSecuenciaEntity entity = repository.findById(secuencia.fecha())
                .map(existente -> {
                    existente.setUltimoOrden(secuencia.ultimoOrden());
                    return existente;
                })
                .orElseGet(() -> AsientoSecuenciaEntity.builder()
                        .fecha(secuencia.fecha())
                        .ultimoOrden(secuencia.ultimoOrden())
                        .build());
        return toDomain(repository.saveAndFlush(entity));
    }

    private AsientoSecuencia toDomain(AsientoSecuenciaEntity entity) {
        return new AsientoSecuencia(entity.getFecha(), entity.getUltimoOrden());
    }

}

package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.mapper.UbicacionArticuloMapper;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.repository.JpaUbicacionArticuloRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaUbicacionArticuloRepositoryAdapter implements UbicacionArticuloRepository {
    private final JpaUbicacionArticuloRepository jpaUbicacionArticuloRepository;
    private final UbicacionArticuloMapper mapper;
    private final EntityManager entityManager;

    /**
     * Upsert por par. Si el vínculo existe, se bloquea su fila (y solo esa) antes de leerlo; si no existe se inserta
     * sin bloquear nada (un FOR UPDATE sobre un par ausente tomaría un bloqueo de brecha). Un choque con otra
     * inserción del mismo par sale del flush como conflicto reintentable.
     * FOR UPDATE nativo: con PESSIMISTIC_WRITE, Hibernate 7 genera "FOR UPDATE OF", que MySQL 5.7 no acepta.
     */
    @Override
    public UbicacionArticulo save(UbicacionArticulo domain) {
        try {
            var entity = jpaUbicacionArticuloRepository
                    .findByUbicacionIdAndArticuloId(domain.getUbicacionId(), domain.getArticuloId())
                    .map(existing -> {
                        entityManager.createNativeQuery(
                                        "SELECT ubicacion_articulo_id FROM ubicacion_articulo WHERE ubicacion_articulo_id = :id FOR UPDATE")
                                .setParameter("id", existing.getUbicacionArticuloId()).getResultList();
                        entityManager.refresh(existing); // el estado leído después del bloqueo
                        existing.setNumeroCuenta(domain.getNumeroCuenta());
                        return existing;
                    })
                    .orElseGet(() -> {
                        var nuevo = mapper.toEntity(domain);
                        nuevo.setUbicacionArticuloId(null);
                        entityManager.persist(nuevo);
                        return nuevo;
                    });
            entityManager.flush();
            // Las asociaciones (ubicación, artículo, cuenta) se releen: si cambió la cuenta, la cargada es la vieja
            entityManager.refresh(entity);
            return mapper.toDomainModel(entity);
        } catch (RuntimeException ex) {
            throw UbicacionArticuloRestricciones.traducir(ex, domain.getUbicacionId(), domain.getArticuloId());
        }
    }
    
    @Override
    public List<UbicacionArticulo> findAllByArticuloId(Long articuloId) {
        return jpaUbicacionArticuloRepository.findAllByArticuloId(articuloId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<UbicacionArticulo> findAll() {
        return jpaUbicacionArticuloRepository.findAll().stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<UbicacionArticulo> findByUbicacionIdAndArticuloId(Integer ubicacionId, Long articuloId) {
        return jpaUbicacionArticuloRepository.findByUbicacionIdAndArticuloId(ubicacionId, articuloId)
                .map(mapper::toDomainModel);
    }
    
}
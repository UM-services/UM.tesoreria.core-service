package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity.ArticuloEntity;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.dependencias.ubicacion.infrastructure.persistence.entity.UbicacionEntity;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.mapper.UbicacionArticuloMapper;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.repository.JpaUbicacionArticuloRepository;
import java.math.BigDecimal;
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
     * Upsert por par. Si el vínculo existe, su estado sale de la lectura con bloqueo (en REPEATABLE READ es la única
     * que ve lo último confirmado); si no existe se inserta sin bloquear nada (un FOR UPDATE sobre un par ausente
     * tomaría un bloqueo de brecha). Un choque con otra inserción del mismo par, o un vínculo borrado entre la
     * búsqueda y el bloqueo, sale como conflicto reintentable.
     * FOR UPDATE nativo: con PESSIMISTIC_WRITE, Hibernate 7 genera "FOR UPDATE OF", que MySQL 5.7 no acepta.
     */
    @Override
    public UbicacionArticulo save(UbicacionArticulo domain) {
        try {
            var id = jpaUbicacionArticuloRepository.findIdByUbicacionIdAndArticuloId(domain.getUbicacionId(), domain.getArticuloId());
            UbicacionArticuloEntity entity;
            if (id.isPresent()) {
                List<?> filas = entityManager.createNativeQuery(
                                "SELECT * FROM ubicacion_articulo WHERE ubicacion_articulo_id = :id FOR UPDATE", UbicacionArticuloEntity.class)
                        .setParameter("id", id.get())
                        .getResultList();
                if (filas.isEmpty()) {
                    throw new UbicacionArticuloConflictException(true,
                            "el vínculo " + domain.getUbicacionId() + ":" + domain.getArticuloId() + " se borró mientras se asignaba");
                }
                entity = (UbicacionArticuloEntity) filas.getFirst();
                entity.setNumeroCuenta(domain.getNumeroCuenta());
            } else {
                entity = mapper.toEntity(domain);
                entity.setUbicacionArticuloId(null);
                entityManager.persist(entity);
            }
            entityManager.flush();
            // Sin refresh: en REPEATABLE READ releería la foto vieja si no hubo UPDATE (otro ya había guardado
            // la misma cuenta). Los datos del vínculo ya son los vigentes; solo se cargan sus asociaciones.
            entity.setUbicacion(entityManager.find(UbicacionEntity.class, entity.getUbicacionId()));
            entity.setArticulo(entityManager.find(ArticuloEntity.class, entity.getArticuloId()));
            entity.setCuenta(entity.getNumeroCuenta() == null ? null : entityManager.find(CuentaEntity.class, entity.getNumeroCuenta()));
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

    /**
     * Ids con una lectura común y bloqueo por clave primaria: un FOR UPDATE por {@code articulo_id} sin filas tomaría
     * un bloqueo de brecha. El estado devuelto sale de la lectura con bloqueo y sin cargar entidades (las asociaciones
     * se cargarían por nada). Un vínculo nuevo no puede aparecer mientras tanto: su FK espera al artículo bloqueado.
     */
    @Override
    public List<UbicacionArticulo> deleteAllByArticuloId(Long articuloId) {
        try {
            var ids = jpaUbicacionArticuloRepository.findIdsByArticuloId(articuloId);
            if (ids.isEmpty()) {
                return List.of();
            }
            List<?> filas = entityManager.createNativeQuery(
                            "SELECT ubicacion_articulo_id, ubicacion_id, articulo_id, cuenta_contable FROM ubicacion_articulo"
                                    + " WHERE ubicacion_articulo_id IN (:ids) ORDER BY ubicacion_articulo_id FOR UPDATE")
                    .setParameter("ids", ids)
                    .getResultList();
            var borrados = filas.stream().map(Object[].class::cast).map(f -> UbicacionArticulo.builder()
                    .ubicacionArticuloId(((Number) f[0]).longValue())
                    .ubicacionId(f[1] == null ? null : ((Number) f[1]).intValue())
                    .articuloId(f[2] == null ? null : ((Number) f[2]).longValue())
                    .numeroCuenta((BigDecimal) f[3])
                    .build()).toList();
            if (!borrados.isEmpty()) {
                entityManager.createNativeQuery("DELETE FROM ubicacion_articulo WHERE ubicacion_articulo_id IN (:ids)")
                        .setParameter("ids", borrados.stream().map(UbicacionArticulo::getUbicacionArticuloId).toList())
                        .executeUpdate();
            }
            return borrados;
        } catch (RuntimeException ex) {
            throw UbicacionArticuloRestricciones.traducir(ex, null, articuloId);
        }
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
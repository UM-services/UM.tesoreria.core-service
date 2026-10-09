package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity.ArticuloEntity;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.dependencias.ubicacion.infrastructure.persistence.entity.UbicacionEntity;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.AsignacionGuardada;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.mapper.UbicacionArticuloMapper;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.repository.JpaUbicacionArticuloRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
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
     * búsqueda y el bloqueo (o reasignado a otro par), sale como conflicto reintentable.
     * FOR UPDATE nativo: con PESSIMISTIC_WRITE, Hibernate 7 genera "FOR UPDATE OF", que MySQL 5.7 no acepta.
     */
    @Override
    public AsignacionGuardada save(UbicacionArticulo domain) {
        try {
            var id = jpaUbicacionArticuloRepository.findIdByUbicacionIdAndArticuloId(domain.getUbicacionId(), domain.getArticuloId());
            UbicacionArticuloEntity entity;
            UbicacionArticulo anterior = null;
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
                // La búsqueda del id vio la foto de RR; el bloqueo puede devolver esa misma PK para otro par.
                // No se modifica ni registra ese vínculo: el servicio repite el pedido en una transacción nueva.
                if (!Objects.equals(entity.getUbicacionId(), domain.getUbicacionId())
                        || !Objects.equals(entity.getArticuloId(), domain.getArticuloId())) {
                    throw new UbicacionArticuloConflictException(true,
                            "el vínculo " + domain.getUbicacionId() + ":" + domain.getArticuloId() + " se reasignó mientras se asignaba");
                }
                anterior = escalares(entity);
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
            return new AsignacionGuardada(anterior, mapper.toDomainModel(entity));
        } catch (RuntimeException ex) {
            throw UbicacionArticuloRestricciones.traducir(ex, domain.getUbicacionId(), domain.getArticuloId());
        }
    }
    
    /** Copia de los campos de la fila, antes de modificar la entidad administrada. */
    private static UbicacionArticulo escalares(UbicacionArticuloEntity entity) {
        return UbicacionArticulo.builder()
                .ubicacionArticuloId(entity.getUbicacionArticuloId())
                .ubicacionId(entity.getUbicacionId())
                .articuloId(entity.getArticuloId())
                .numeroCuenta(entity.getNumeroCuenta())
                .build();
    }

    @Override
    public List<UbicacionArticulo> findAllByArticuloId(Long articuloId) {
        return jpaUbicacionArticuloRepository.findAllByArticuloId(articuloId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    /**
     * Lectura con bloqueo por {@code articulo_id}, después de que la baja bloqueó el artículo: es una lectura actual,
     * así que ve los vínculos que otro confirmó mientras la baja esperaba (la foto de REPEATABLE READ se tomó antes del
     * bloqueo) y no trae uno que otro pasó a otro artículo. Usa el índice {@code articulo_id} (EXPLAIN en dev,
     * 2026-10-08) y toma un bloqueo de brecha hasta el fin de la baja: mientras tanto también espera la inserción de un
     * vínculo de un artículo vecino en ese índice, y un escritor externo con varias escrituras en la misma transacción
     * puede chocar en un interbloqueo (la baja lo reintenta una vez). Sin cargar entidades.
     */
    @Override
    public List<UbicacionArticulo> deleteAllByArticuloId(Long articuloId) {
        try {
            List<?> filas = entityManager.createNativeQuery(
                            "SELECT ubicacion_articulo_id, ubicacion_id, articulo_id, cuenta_contable FROM ubicacion_articulo"
                                    + " WHERE articulo_id = :articuloId ORDER BY ubicacion_articulo_id FOR UPDATE")
                    .setParameter("articuloId", articuloId)
                    .getResultList();
            var borrados = filas.stream().map(Object[].class::cast).map(f -> UbicacionArticulo.builder()
                    .ubicacionArticuloId(((Number) f[0]).longValue())
                    .ubicacionId(f[1] == null ? null : ((Number) f[1]).intValue())
                    .articuloId(((Number) f[2]).longValue())
                    .numeroCuenta((BigDecimal) f[3])
                    .build()).toList();
            if (!borrados.isEmpty()) {
                entityManager.createNativeQuery("DELETE FROM ubicacion_articulo WHERE ubicacion_articulo_id IN (:ids) AND articulo_id = :articuloId")
                        .setParameter("ids", borrados.stream().map(UbicacionArticulo::getUbicacionArticuloId).toList())
                        .setParameter("articuloId", articuloId)
                        .executeUpdate();
            }
            return borrados;
        } catch (RuntimeException ex) {
            throw UbicacionArticuloRestricciones.traducirBaja(ex, articuloId);
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

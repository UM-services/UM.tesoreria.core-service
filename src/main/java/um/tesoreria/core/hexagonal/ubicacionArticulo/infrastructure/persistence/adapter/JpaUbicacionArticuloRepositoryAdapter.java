package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
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
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JpaUbicacionArticuloRepositoryAdapter implements UbicacionArticuloRepository {
    private final JpaUbicacionArticuloRepository jpaUbicacionArticuloRepository;
    private final UbicacionArticuloMapper mapper;
    private final EntityManager entityManager;
    private final SQLExceptionTranslator sqlExceptionTranslator;

    /**
     * El traductor de errores es propio del adaptador y no un bean: Spring Boot usa cualquier bean
     * {@link SQLExceptionTranslator} para traducir las excepciones de todos los repositorios JPA.
     */
    @Autowired
    public JpaUbicacionArticuloRepositoryAdapter(JpaUbicacionArticuloRepository jpaUbicacionArticuloRepository,
                                                 UbicacionArticuloMapper mapper, EntityManager entityManager,
                                                 DataSource dataSource) {
        this(jpaUbicacionArticuloRepository, mapper, entityManager, new SQLErrorCodeSQLExceptionTranslator(dataSource));
    }

    JpaUbicacionArticuloRepositoryAdapter(JpaUbicacionArticuloRepository jpaUbicacionArticuloRepository,
                                          UbicacionArticuloMapper mapper, EntityManager entityManager,
                                          SQLExceptionTranslator sqlExceptionTranslator) {
        this.jpaUbicacionArticuloRepository = jpaUbicacionArticuloRepository;
        this.mapper = mapper;
        this.entityManager = entityManager;
        this.sqlExceptionTranslator = sqlExceptionTranslator;
    }

    /**
     * Upsert por par. Si el vínculo existe, su estado sale de la lectura con bloqueo (en REPEATABLE READ es la única
     * que ve lo último confirmado); si no existe se inserta sin bloquear nada (un FOR UPDATE sobre un par ausente
     * tomaría un bloqueo de brecha). Un choque con otra inserción del mismo par, o un vínculo borrado entre la
     * búsqueda y el bloqueo (o reasignado a otro par), sale como conflicto reintentable.
     */
    @Override
    public AsignacionGuardada save(UbicacionArticulo domain) {
        try {
            var id = jpaUbicacionArticuloRepository.findIdByUbicacionIdAndArticuloId(domain.getUbicacionId(), domain.getArticuloId());
            UbicacionArticuloEntity entity;
            UbicacionArticulo anterior = null;
            if (id.isPresent()) {
                entity = jpaUbicacionArticuloRepository.findWithLockByUbicacionArticuloId(id.get().getUbicacionArticuloId())
                        .orElseThrow(() -> new UbicacionArticuloConflictException(true,
                                "el vínculo " + domain.getUbicacionId() + ":" + domain.getArticuloId() + " se borró mientras se asignaba"));
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
            throw UbicacionArticuloRestricciones.traducir(sqlExceptionTranslator, ex, domain.getUbicacionId(), domain.getArticuloId());
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
     * puede chocar en un interbloqueo (la baja lo reintenta una vez). Sin cargar entidades. Las filas leídas quedan
     * bloqueadas por esta transacción, así que se borran por id.
     */
    @Override
    public List<UbicacionArticulo> deleteAllByArticuloId(Long articuloId) {
        try {
            var borrados = jpaUbicacionArticuloRepository.findWithLockByArticuloIdOrderByUbicacionArticuloId(articuloId).stream()
                    .map(f -> UbicacionArticulo.builder()
                            .ubicacionArticuloId(f.getUbicacionArticuloId())
                            .ubicacionId(f.getUbicacionId())
                            .articuloId(f.getArticuloId())
                            .numeroCuenta(f.getNumeroCuenta())
                            .build())
                    .toList();
            if (!borrados.isEmpty()) {
                jpaUbicacionArticuloRepository.deleteAllById(borrados.stream().map(UbicacionArticulo::getUbicacionArticuloId).toList());
            }
            return borrados;
        } catch (RuntimeException ex) {
            throw UbicacionArticuloRestricciones.traducirBaja(sqlExceptionTranslator, ex, articuloId);
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

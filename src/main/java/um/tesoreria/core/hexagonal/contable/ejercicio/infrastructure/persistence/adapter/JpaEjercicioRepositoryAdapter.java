package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.adapter;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioNoEncontradoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.entity.EjercicioEntity;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.mapper.EjercicioMapper;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.repository.JpaEjercicioRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaEjercicioRepositoryAdapter implements EjercicioRepository {

    private final JpaEjercicioRepository jpaRepository;
    private final EjercicioMapper mapper;
    private final EntityManager entityManager;

    // específico de MySQL: revisar al migrar a Postgres. @Lock(PESSIMISTIC_WRITE) es JPA estándar,
    // pero depende del dialecto MySQL57Dialect (Hibernate 7 genera FOR UPDATE OF, que MySQL 5.7 rechaza).
    @Override
    public Optional<Ejercicio> findLastWithLock() {
        return jpaRepository.findTopByOrderByEjercicioIdDesc().map(mapper::toDomain);
    }

    // específico de MySQL: revisar al migrar a Postgres (ver findLastWithLock).
    @Override
    public Optional<Ejercicio> findWithLockByEjercicioId(Integer ejercicioId) {
        return jpaRepository.findWithLockByEjercicioId(ejercicioId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEjercicioId(Integer ejercicioId) {
        return jpaRepository.existsById(ejercicioId);
    }

    @Override
    public Ejercicio create(Ejercicio ejercicio) {
        EjercicioEntity entity = mapper.toEntity(ejercicio);
        try {
            // persist obliga a INSERT: save con id asignado haría merge y pisaría un ejercicio existente.
            entityManager.persist(entity);
            entityManager.flush();
        } catch (EntityExistsException ex) {
            throw duplicado(ejercicio, ex);
        } catch (PersistenceException ex) {
            // El EntityManager directo lanza excepciones de Hibernate sin traducir a Spring; solo la PK repetida es DUPLICADO.
            Throwable causa = ex instanceof ConstraintViolationException ? ex : ex.getCause();
            if (causa instanceof ConstraintViolationException cv && cv.getKind() == ConstraintViolationException.ConstraintKind.UNIQUE) {
                throw duplicado(ejercicio, ex);
            }
            throw ex;
        }
        return mapper.toDomain(entity);
    }

    private static EjercicioConflictoException duplicado(Ejercicio ejercicio, Exception causa) {
        return new EjercicioConflictoException(Codigo.DUPLICADO,
                "El ejercicio " + ejercicio.getEjercicioId() + " ya existe", causa);
    }

    @Override
    public Ejercicio update(Ejercicio ejercicio) {
        EjercicioEntity entity = jpaRepository.findById(ejercicio.getEjercicioId())
                .orElseThrow(() -> new EjercicioNoEncontradoException(ejercicio.getEjercicioId()));
        entity.setBloqueado(ejercicio.getBloqueado());
        entity.setOrdenContableResultado(ejercicio.getOrdenContableResultado());
        entity.setOrdenContableBienesUso(ejercicio.getOrdenContableBienesUso());
        jpaRepository.flush();
        return mapper.toDomain(entity);
    }

    @Override
    public void deleteByEjercicioId(Integer ejercicioId) {
        try {
            jpaRepository.deleteById(ejercicioId);
            jpaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new EjercicioConflictoException(Codigo.EN_USO,
                    "El ejercicio " + ejercicioId + " tiene registros asociados", ex);
        }
    }
}

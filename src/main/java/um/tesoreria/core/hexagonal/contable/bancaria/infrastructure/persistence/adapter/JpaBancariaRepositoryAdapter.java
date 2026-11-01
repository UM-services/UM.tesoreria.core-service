package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaNoEncontradaException;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaRepository;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.entity.BancariaEntity;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.mapper.BancariaMapper;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.repository.JpaBancariaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBancariaRepositoryAdapter implements BancariaRepository {

    private static final String REFERENCIA_CAMBIO = "Un dato referenciado cambió; volvé a intentar";

    private final JpaBancariaRepository jpaRepository;
    private final BancariaMapper mapper;
    private final EntityManager entityManager;

    @Override
    public Optional<Bancaria> findByBancariaId(Long bancariaId) {
        return jpaRepository.findById(bancariaId).map(mapper::toDomain);
    }

    // específico de MySQL: revisar al migrar a Postgres (ver JpaBancariaRepository.findWithLockByBancariaId).
    @Override
    public Optional<Bancaria> findWithLockByBancariaId(Long bancariaId) {
        return jpaRepository.findWithLockByBancariaId(bancariaId).map(mapper::toDomain);
    }

    @Override
    public Bancaria create(Bancaria bancaria) {
        BancariaEntity entity = mapper.toEntity(bancaria);
        try {
            jpaRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException | PersistenceException ex) {
            throw cambioDeReferencia(ex);
        }
        return releido(entity);
    }

    @Override
    public Bancaria update(Bancaria bancaria) {
        BancariaEntity entity = jpaRepository.findById(bancaria.getBancariaId())
                .orElseThrow(() -> new BancariaNoEncontradaException(bancaria.getBancariaId()));
        entity.setBanco(bancaria.getBanco());
        entity.setNumero(bancaria.getNumero());
        entity.setCbu(bancaria.getCbu());
        entity.setTitular(bancaria.getTitular());
        entity.setCuit(bancaria.getCuit());
        entity.setNumeroCuenta(bancaria.getNumeroCuenta());
        entity.setProveedorId(bancaria.getProveedorId());
        try {
            jpaRepository.flush();
        } catch (DataIntegrityViolationException | PersistenceException ex) {
            throw cambioDeReferencia(ex);
        }
        return releido(entity);
    }

    @Override
    public void deleteByBancariaId(Long bancariaId) {
        try {
            jpaRepository.deleteById(bancariaId);
            jpaRepository.flush();
        } catch (DataIntegrityViolationException | PersistenceException ex) {
            if (ViolacionDeIntegridad.esClaveForanea(ex)) {
                throw new BancariaConflictoException(Codigo.EN_USO,
                        "La cuenta bancaria " + bancariaId + " tiene valores o movimientos de libro banco", ex);
            }
            throw ex;
        }
    }

    /** updated lo asigna MySQL al escribir: se relee para devolver el valor real. */
    private Bancaria releido(BancariaEntity entity) {
        entityManager.refresh(entity);
        return mapper.toDomain(entity);
    }

    private static RuntimeException cambioDeReferencia(RuntimeException ex) {
        if (ViolacionDeIntegridad.esClaveForanea(ex)) {
            return new BancariaConflictoException(Codigo.CONFLICTO, REFERENCIA_CAMBIO, ex);
        }
        return ex;
    }
}

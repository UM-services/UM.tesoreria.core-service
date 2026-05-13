/**
 * 
 */
package um.tesoreria.core.hexagonal.contrato.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.hexagonal.contrato.infrastructure.persistence.entity.ContratoEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaContratoRepository extends JpaRepository<ContratoEntity, Long> {

	List<ContratoEntity> findAllByFacultadIdAndGeograficaId(Integer facultadId, Integer geograficaId);

	List<ContratoEntity> findAllByAjusteAndHastaGreaterThanEqual(Byte ajuste, OffsetDateTime referencia);

	List<ContratoEntity> findAllByHastaGreaterThanEqual(OffsetDateTime referencia);

	List<ContratoEntity> findAllByPersonaIdAndDocumentoIdOrderByDesdeDesc(BigDecimal personaId, Integer documentoId);

	Optional<ContratoEntity> findByContratoId(Long contratoId);

	@Modifying
	void deleteByContratoId(Long contratoId);

    boolean existsByContratoId(Long contratoId);
}

/**
 * 
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.Contrato;

/**
 * @author daniel
 *
 */
@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

	List<Contrato> findAllByFacultadIdAndGeograficaId(Integer facultadId, Integer geograficaId);

	List<Contrato> findAllByAjusteAndHastaGreaterThanEqual(Byte ajuste, OffsetDateTime referencia);

	List<Contrato> findAllByHastaGreaterThanEqual(OffsetDateTime referencia);

	List<Contrato> findAllByPersonaIdAndDocumentoIdOrderByDesdeDesc(BigDecimal personaId, Integer documentoId);

	Optional<Contrato> findByContratoId(Long contratoId);

	@Modifying
	void deleteByContratoId(Long contratoId);

}

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

	public List<Contrato> findAllByFacultadIdAndGeograficaId(Integer facultadId, Integer geograficaId);

	public List<Contrato> findAllByAjusteAndHastaGreaterThanEqual(Byte ajuste, OffsetDateTime referencia);

	public List<Contrato> findAllByHastaGreaterThanEqual(OffsetDateTime referencia);

	public List<Contrato> findAllByPersonaIdAndDocumentoIdOrderByDesdeDesc(BigDecimal personaId, Integer documentoId);

	public List<Contrato> findAllByContratadoId(Long contratadoId);

	public Optional<Contrato> findByContratoId(Long contratoId);

	@Modifying
	public void deleteByContratoId(Long contratoId);

}

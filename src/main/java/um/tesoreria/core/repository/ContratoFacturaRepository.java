/**
 * 
 */
package um.tesoreria.core.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.ContratoFactura;

/**
 * @author daniel
 *
 */
@Repository
public interface ContratoFacturaRepository extends JpaRepository<ContratoFactura, Long> {

	public List<ContratoFactura> findAllByPendienteAndContratoIdIn(Byte pendiente, List<Long> contratoIds);

	public List<ContratoFactura> findAllByExcluidoAndContratoIdIn(Byte excluido, List<Long> contratoIds);

	public List<ContratoFactura> findAllByContratoIdOrderByFechaDesc(Long contratoId);

	public List<ContratoFactura> findAllByAcreditacionAndEnvio(OffsetDateTime fecha, Integer envio);

	public Optional<ContratoFactura> findByContratofacturaId(Long contratofacturaId);

	public Optional<ContratoFactura> findFirstByContratoIdInAndCbuNotOrderByFechaDesc(List<Long> contratoIds,
			String cbu);

	@Modifying
	public void deleteByContratofacturaId(Long contratofacturaId);

}

/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.ContratoPeriodo;

/**
 * @author daniel
 *
 */
@Repository
public interface IContratoPeriodoRepository extends JpaRepository<ContratoPeriodo, Long> {

	public List<ContratoPeriodo> findAllByContratoId(Long contratoId, Sort sort);

	public List<ContratoPeriodo> findAllByContratoFacturaId(Long contratoFacturaId);

	public List<ContratoPeriodo> findAllByContratoIdAndContratoFacturaIdIsNullAndContratoChequeIdIsNull(
			Long contratoId);

	public List<ContratoPeriodo> findAllByContratoIdAndMarcaTemporal(Long contratoId, Byte marcaTemporal);

	public List<ContratoPeriodo> findAllByAnhoAndMes(Integer anho, Integer mes);

	public Optional<ContratoPeriodo> findByContratoIdAndAnhoAndMes(Long contratoId, Integer anho, Integer mes);

	public Optional<ContratoPeriodo> findByContratoPeriodoId(Long contratoPeriodoId);

	@Modifying
	public void deleteByContratoPeriodoId(Long contratoPeriodoId);

}

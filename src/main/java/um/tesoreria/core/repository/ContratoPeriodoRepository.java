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
public interface ContratoPeriodoRepository extends JpaRepository<ContratoPeriodo, Long> {

	List<ContratoPeriodo> findAllByContratoId(Long contratoId, Sort sort);

	List<ContratoPeriodo> findAllByContratoFacturaId(Long contratoFacturaId);

	List<ContratoPeriodo> findAllByContratoIdAndContratoFacturaIdIsNullAndContratoChequeIdIsNull(
			Long contratoId);

	List<ContratoPeriodo> findAllByContratoIdAndMarcaTemporal(Long contratoId, Byte marcaTemporal);

	List<ContratoPeriodo> findAllByAnhoAndMes(Integer anho, Integer mes);

	Optional<ContratoPeriodo> findByContratoIdAndAnhoAndMes(Long contratoId, Integer anho, Integer mes);

	Optional<ContratoPeriodo> findByContratoPeriodoId(Long contratoPeriodoId);

	@Modifying
	void deleteByContratoPeriodoId(Long contratoPeriodoId);

}

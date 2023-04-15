/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.Baja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

/**
 * @author daniel
 *
 */
public interface IBajaRepository extends JpaRepository<Baja, Long> {

	public List<Baja> findAllByChequeraIdIn(List<Long> chequeraIds);

	public Optional<Baja> findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
			Integer tipoChequeraId, Long chequeraSerieId);

	public Optional<Baja> findByBajaId(Long bajaId);

	@Modifying
	public void deleteByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}

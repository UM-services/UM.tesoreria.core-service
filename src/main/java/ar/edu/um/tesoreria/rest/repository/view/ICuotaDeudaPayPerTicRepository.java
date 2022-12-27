/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.CuotaDeudaPayPerTic;

/**
 * @author daniel
 *
 */
@Repository
public interface ICuotaDeudaPayPerTicRepository extends JpaRepository<CuotaDeudaPayPerTic, Long> {
	
	public List<CuotaDeudaPayPerTic> findAllByVencimiento1Between(OffsetDateTime desde, OffsetDateTime hasta);

}

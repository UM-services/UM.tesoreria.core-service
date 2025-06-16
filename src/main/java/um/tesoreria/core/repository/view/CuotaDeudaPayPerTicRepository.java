/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.CuotaDeudaPayPerTic;

/**
 * @author daniel
 *
 */
@Repository
public interface CuotaDeudaPayPerTicRepository extends JpaRepository<CuotaDeudaPayPerTic, Long> {
	
	public List<CuotaDeudaPayPerTic> findAllByVencimiento1Between(OffsetDateTime desde, OffsetDateTime hasta);

}

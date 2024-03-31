/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import um.tesoreria.core.model.view.CuotaDeuda;

/**
 * @author daniel
 *
 */
public interface ICuotaDeudaRepository extends JpaRepository<CuotaDeuda, Long> {
	
	public List<CuotaDeuda> findAllByVencimiento1Between(OffsetDateTime desde, OffsetDateTime hasta);

}

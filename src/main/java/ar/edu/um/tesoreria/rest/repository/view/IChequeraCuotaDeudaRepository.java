/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.time.OffsetDateTime;
import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.view.ChequeraCuotaDeuda;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraCuotaDeudaRepository extends JpaRepository<ChequeraCuotaDeuda, Long> {

	public List<ChequeraCuotaDeuda> findAllByVencimiento1Between(OffsetDateTime desde, OffsetDateTime hasta,
			Pageable pageable);

}

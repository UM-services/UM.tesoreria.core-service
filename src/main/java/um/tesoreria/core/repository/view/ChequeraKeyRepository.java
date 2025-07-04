/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.ChequeraKey;

/**
 * @author daniel
 *
 */
@Repository
public interface ChequeraKeyRepository extends JpaRepository<ChequeraKey, Long> {

	public List<ChequeraKey> findAllByChequeraKeyInOrderByLectivoIdDesc(List<String> chequeraKeys);

}

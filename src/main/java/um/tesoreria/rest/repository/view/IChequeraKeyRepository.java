/**
 * 
 */
package um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.view.ChequeraKey;
import um.tesoreria.rest.model.view.ChequeraKey;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraKeyRepository extends JpaRepository<ChequeraKey, Long> {

	public List<ChequeraKey> findAllByChequeraKeyInOrderByLectivoIdDesc(List<String> chequeraKeys);

}

/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.ProductoTipoChequera;

/**
 * @author daniel
 *
 */
@Repository
public interface ProductoTipoChequeraRepository extends JpaRepository<ProductoTipoChequera, Long> {

	public List<ProductoTipoChequera> findAllByFacultadIdAndLectivoIdAndTipochequeraId(Integer facultadId, Integer lectivoId,
			Integer tipochequeraId);

}

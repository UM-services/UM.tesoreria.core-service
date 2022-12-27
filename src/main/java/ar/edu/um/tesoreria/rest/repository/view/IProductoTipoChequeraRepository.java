/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.ProductoTipoChequera;

/**
 * @author daniel
 *
 */
@Repository
public interface IProductoTipoChequeraRepository extends JpaRepository<ProductoTipoChequera, Long> {

	public List<ProductoTipoChequera> findAllByFacultadIdAndLectivoIdAndTipochequeraId(Integer facultadId, Integer lectivoId,
			Integer tipochequeraId);

}

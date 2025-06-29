/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Producto;

/**
 * @author daniel
 *
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

	public Optional<Producto> findByProductoId(Integer productoId);

}

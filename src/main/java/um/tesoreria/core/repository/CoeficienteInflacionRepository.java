/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import um.tesoreria.core.model.CoeficienteInflacion;

/**
 * @author daniel
 *
 */
public interface CoeficienteInflacionRepository extends JpaRepository<CoeficienteInflacion, Long> {
	@Query("SELECT c FROM CoeficienteInflacion c WHERE c.anho = :anho AND c.mes = :mes")
	public Optional<CoeficienteInflacion> findByUnique(@Param("anho") Integer anho, @Param("mes") Integer mes);
}

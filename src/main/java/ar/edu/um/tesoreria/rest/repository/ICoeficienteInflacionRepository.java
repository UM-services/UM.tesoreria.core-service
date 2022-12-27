/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ar.edu.um.tesoreria.rest.model.CoeficienteInflacion;

/**
 * @author daniel
 *
 */
public interface ICoeficienteInflacionRepository extends JpaRepository<CoeficienteInflacion, Long> {
	@Query("SELECT c FROM CoeficienteInflacion c WHERE c.anho = :anho AND c.mes = :mes")
	public Optional<CoeficienteInflacion> findByUnique(@Param("anho") Integer anho, @Param("mes") Integer mes);
}

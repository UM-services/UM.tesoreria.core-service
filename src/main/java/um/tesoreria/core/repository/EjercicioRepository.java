/**
 * 
 */
package um.tesoreria.core.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.kotlin.model.Ejercicio;

/**
 * @author daniel
 *
 */
public interface EjercicioRepository extends JpaRepository<Ejercicio, Integer> {

	Optional<Ejercicio> findByEjercicioId(Integer ejercicioId);

	Optional<Ejercicio> findByFechaInicioLessThanEqualAndFechaFinalGreaterThanEqual(OffsetDateTime ref_inicio, OffsetDateTime ref_final);

	Optional<Ejercicio> findTopByOrderByEjercicioIdDesc();

}

/**
 * 
 */
package um.tesoreria.rest.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.rest.kotlin.model.Ejercicio;

/**
 * @author daniel
 *
 */
public interface IEjercicioRepository extends JpaRepository<Ejercicio, Integer> {

	public Optional<Ejercicio> findByEjercicioId(Integer ejercicioId);

	public Optional<Ejercicio> findByFechaInicioLessThanEqualAndFechaFinalGreaterThanEqual(OffsetDateTime ref_inicio, OffsetDateTime ref_final);

	public Optional<Ejercicio> findTopByOrderByEjercicioIdDesc();

}

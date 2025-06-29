/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import um.tesoreria.core.model.view.CuentaMensual;
import um.tesoreria.core.model.view.pk.CuentaMensualPk;

/**
 * @author daniel
 *
 */
public interface CuentaMensualRepository extends JpaRepository<CuentaMensual, CuentaMensualPk> {
	@Query("SELECT c FROM CuentaMensual c WHERE c.anho = :anho AND c.mes = :mes AND c.cuenta BETWEEN 30000000000 AND 39999999999")
	public List<CuentaMensual> findIngresosByMes (@Param("anho") Integer anho, @Param("mes") Integer mes);

	@Query("SELECT c FROM CuentaMensual c WHERE c.anho = :anho AND c.mes = :mes AND c.cuenta BETWEEN 40000000000 AND 49999999999")
	public List<CuentaMensual> findGastosByMes (@Param("anho") Integer anho, @Param("mes") Integer mes);

	@Query("SELECT c FROM CuentaMensual c WHERE c.anho = :anho AND c.mes = :mes AND c.cuenta BETWEEN 10400000000 AND 10499999999")
	public List<CuentaMensual> findBienesUsoByMes(@Param("anho") Integer anho, @Param("mes") Integer mes);
}

/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.ChequeraAlta;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraAltaRepository extends JpaRepository<ChequeraAlta, Long> {

	public List<ChequeraAlta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId,
			Integer geograficaId);

}

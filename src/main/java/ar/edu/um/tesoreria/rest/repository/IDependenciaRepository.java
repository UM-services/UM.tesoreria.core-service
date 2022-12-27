/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Dependencia;

/**
 * @author daniel
 *
 */
@Repository
public interface IDependenciaRepository extends JpaRepository<Dependencia, Integer> {

}

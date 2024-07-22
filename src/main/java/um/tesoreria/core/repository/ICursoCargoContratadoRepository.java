/**
 *
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.CursoCargoContratado;

/**
 * @author daniel
 *
 */
@Repository
public interface ICursoCargoContratadoRepository extends JpaRepository<CursoCargoContratado, Long> {

    List<CursoCargoContratado> findAllByContratadoIdAndAnhoAndMesAndContratoId(Long contratadoId, Integer anho,
                                                                               Integer mes, Long contratoId);

    List<CursoCargoContratado> findAllByContratadoIdAndAnhoAndMes(Long contratadoId, Integer anho, Integer mes);

    List<CursoCargoContratado> findAllByCursoIdAndAnhoAndMes(Long cursoId, Integer anho, Integer mes);

    List<CursoCargoContratado> findAllByCursoIdAndContratoId(Long cursoId, Long contratoId);

    List<CursoCargoContratado> findAllByAnhoAndMes(Integer anho, Integer mes);

    Optional<CursoCargoContratado> findByCursoCargoContratadoId(Long cursoCargoContratadoId);

    Optional<CursoCargoContratado> findByCursoIdAndAnhoAndMesAndContratadoId(Long cursoId, Integer anho,
                                                                             Integer mes, Long contratadoId);

    @Modifying
    void deleteByCursoCargoContratadoId(Long cursoCargoContratadoId);

    @Modifying
    void deleteAllByCursoIdAndContratoIdAndContratadoId(Long cursoId, Long contratoId, Long contratadoId);

}

/**
 *
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
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
public interface CursoCargoContratadoRepository extends JpaRepository<CursoCargoContratado, Long> {

    List<CursoCargoContratado> findAllByContratoIdAndAnhoAndMesAndPersonaIdAndDocumentoId(Long contratoId,
                                                                                          Integer anho,
                                                                                          Integer mes,
                                                                                          BigDecimal personaId,
                                                                                          Integer documentoId
    );

    List<CursoCargoContratado> findAllByPersonaIdAndDocumentoIdAndAnhoAndMes(BigDecimal personaId, Integer documentoId, Integer anho, Integer mes);

    List<CursoCargoContratado> findAllByCursoIdAndAnhoAndMes(Long cursoId, Integer anho, Integer mes);

    List<CursoCargoContratado> findAllByCursoIdAndContratoId(Long cursoId, Long contratoId);

    List<CursoCargoContratado> findAllByAnhoAndMes(Integer anho, Integer mes);

    Optional<CursoCargoContratado> findByCursoCargoContratadoId(Long cursoCargoContratadoId);

    Optional<CursoCargoContratado> findByCursoIdAndAnhoAndMesAndContratoId(Long cursoId, Integer anho,
                                                                             Integer mes, Long contratoId);

    @Modifying
    void deleteByCursoCargoContratadoId(Long cursoCargoContratadoId);

    @Modifying
    void deleteAllByCursoIdAndContratoId(Long cursoId, Long contratoId);

    @Modifying
    void deleteAllByContratoIdAndAnhoAndMes(Long contratoId, Integer anho, Integer mes);

}

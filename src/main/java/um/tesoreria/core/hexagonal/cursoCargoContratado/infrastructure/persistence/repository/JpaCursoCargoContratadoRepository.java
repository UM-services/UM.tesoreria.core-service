/**
 *
 */
package um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.persistence.entity.CursoCargoContratadoEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaCursoCargoContratadoRepository extends JpaRepository<CursoCargoContratadoEntity, Long> {

    List<CursoCargoContratadoEntity> findAllByContratoIdAndAnhoAndMesAndPersonaIdAndDocumentoId(Long contratoId,
                                                                                                Integer anho,
                                                                                                Integer mes,
                                                                                                BigDecimal personaId,
                                                                                                Integer documentoId
    );

    List<CursoCargoContratadoEntity> findAllByPersonaIdAndDocumentoIdAndAnhoAndMes(BigDecimal personaId, Integer documentoId, Integer anho, Integer mes);

    List<CursoCargoContratadoEntity> findAllByCursoIdAndAnhoAndMes(Long cursoId, Integer anho, Integer mes);

    List<CursoCargoContratadoEntity> findAllByCursoIdAndContratoId(Long cursoId, Long contratoId);

    List<CursoCargoContratadoEntity> findAllByAnhoAndMes(Integer anho, Integer mes);

    Optional<CursoCargoContratadoEntity> findByCursoCargoContratadoId(Long cursoCargoContratadoId);

    Optional<CursoCargoContratadoEntity> findByCursoIdAndAnhoAndMesAndContratoId(Long cursoId, Integer anho,
                                                                                 Integer mes, Long contratoId);

    @Modifying
    void deleteByCursoCargoContratadoId(Long cursoCargoContratadoId);

    @Modifying
    void deleteAllByCursoIdAndContratoId(Long cursoId, Long contratoId);

    @Modifying
    void deleteAllByContratoIdAndAnhoAndMes(Long contratoId, Integer anho, Integer mes);

}

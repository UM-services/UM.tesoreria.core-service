/**
 * 
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.ChequeraPagoReemplazo;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *
 */
@Repository
public interface ChequeraPagoReemplazoRepository extends JpaRepository<ChequeraPagoReemplazo, Long> {

    List<ChequeraPagoReemplazo> findAllByTipoPagoIdAndAcreditacion(Integer tipoPagoId, OffsetDateTime fecha);

    Optional<ChequeraPagoReemplazo> findByChequeraPagoReemplazoId(Long chequeraPagoReemplazoId);

    List<ChequeraPagoReemplazo> findAllByArchivo(String archivo);

    void deleteAllByArchivo(String archivo);

    Optional<ChequeraPagoReemplazo> findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaIdOrderByOrdenDesc(
            Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId,
            Integer alternativaId, Integer cuotaId);

    List<ChequeraPagoReemplazo> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
            Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId,
            Integer alternativaId, Integer cuotaId);

}

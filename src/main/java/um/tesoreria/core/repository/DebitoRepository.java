/**
 * 
 */
package um.tesoreria.core.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.Debito;

/**
 * @author daniel
 *
 */
@Repository
public interface DebitoRepository extends JpaRepository<Debito, Long> {

	public List<Debito> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndDebitoTipoId(Integer facultadId,
			Integer tipoChequeraId, Long chequeraSerieId, Integer debitoTipoId, Sort sort);

	public List<Debito> findAllByFechaEnvioAndDebitoTipoId(OffsetDateTime fechaEnvio, Integer debitoTipoId);

	public List<Debito> findAllByFechaVencimientoBetweenAndFechaBajaAndFechaEnvioAndDebitoTipoId(OffsetDateTime desde,
			OffsetDateTime hasta, OffsetDateTime fechaBaja, OffsetDateTime fechaEnvio, Integer debitoTipoId);

	public List<Debito> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndFechaEnvioNotNullAndDebitoTipoId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId,
			Integer debitoTipoId);

	public List<Debito> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndFechaVencimientoGreaterThanEqualAndFechaBajaIsNullAndFechaEnvioIsNull(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, OffsetDateTime fechaVencimiento);

	public List<Debito> findAllByFechaVencimientoBetweenAndFechaEnvioIsNullAndFechaBajaIsNullAndCbuLikeAndDebitoTipoId(
			OffsetDateTime desde, OffsetDateTime hasta, String cbu, Integer debitoTipoId);

	public List<Debito> findAllByFechaVencimientoBetweenAndFechaEnvioIsNullAndFechaBajaIsNullAndCbuNotLikeAndDebitoTipoId(
			OffsetDateTime desde, OffsetDateTime hasta, String cbu, Integer debitoTipoId);

	public List<Debito> findAllByFechaEnvioAndCbuLikeAndDebitoTipoId(OffsetDateTime fechaEnvio, String cbu,
			Integer debitoTipoId);

	public List<Debito> findAllByFechaEnvioAndCbuNotLikeAndDebitoTipoId(OffsetDateTime fechaEnvio, String string,
			Integer debitoTipoId);

	public List<Debito> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndDebitoTipoId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId,
			Integer debitoTipoId);

	public List<Debito> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId,
			Integer cuotaId);

	public List<Debito> findAllByCbuAndDebitoTipoId(String cbu, Integer debitoTipoId);

	public List<Debito> findAllByNumeroTarjetaAndDebitoTipoId(String numeroTarjeta, Integer debitoTipoId);

	public Optional<Debito> findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaIdAndDebitoTipoId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId,
			Integer cuotaId, Integer debitoTipoId);

	public Optional<Debito> findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdOrderByFechaVencimientoDesc(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);

	public Optional<Debito> findTop1ByCbuStartingWithAndCbuEndingWithAndFechaBajaIsNullOrderByDebitoIdDesc(String cbu1,
			String cbu2);

	public Optional<Debito> findByDebitoId(Long debitoId);

}

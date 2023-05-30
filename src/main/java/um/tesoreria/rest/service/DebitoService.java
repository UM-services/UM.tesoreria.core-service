/**
 * 
 */
package um.tesoreria.rest.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import um.tesoreria.rest.exception.DebitoException;
import um.tesoreria.rest.model.Debito;
import um.tesoreria.rest.repository.IDebitoRepository;
import um.tesoreria.rest.exception.DebitoException;
import um.tesoreria.rest.repository.IDebitoRepository;

/**
 * @author daniel
 *
 */
@Service
public class DebitoService {

	@Autowired
	private IDebitoRepository repository;

	public List<Debito> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
			Integer debitoTipoId) {
		return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndDebitoTipoId(facultadId,
				tipoChequeraId, chequeraSerieId, debitoTipoId, Sort.by("productoId").ascending()
						.and(Sort.by("alternativaId").ascending()).and(Sort.by("cuotaId").ascending()));
	}

	public List<Debito> findAllByFechaEnvio(OffsetDateTime fechaEnvio, Boolean soloSantander, Boolean soloOtrosBancos,
			Integer debitoTipoId) {
		if (soloSantander) {
			return repository.findAllByFechaEnvioAndCbuLikeAndDebitoTipoId(fechaEnvio, "072%", debitoTipoId);
		}
		if (soloOtrosBancos) {
			return repository.findAllByFechaEnvioAndCbuNotLikeAndDebitoTipoId(fechaEnvio, "072%", debitoTipoId);
		}
		return repository.findAllByFechaEnvioAndDebitoTipoId(fechaEnvio, debitoTipoId);
	}

	public List<Debito> findAllNoIncluidos(OffsetDateTime fecha, Integer debitoTipoId) {
		return repository.findAllByFechaVencimientoBetweenAndFechaBajaAndFechaEnvioAndDebitoTipoId(OffsetDateTime.now(),
				fecha, null, null, debitoTipoId);
	}

	public List<Debito> findAllNoEnviadosAltas(OffsetDateTime desde, OffsetDateTime hasta, Integer debitoTipoId) {
		return repository.findAllByFechaVencimientoBetweenAndFechaBajaAndFechaEnvioAndDebitoTipoId(desde, hasta, null,
				null, debitoTipoId);
	}

	public List<Debito> findAllByEnviados(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
			Integer productoId, Integer alternativaId, Integer debitoTipoId) {
		return repository
				.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndFechaEnvioNotNullAndDebitoTipoId(
						facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, debitoTipoId);
	}

	public List<Debito> findAllPendientesChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
			OffsetDateTime fechaVencimiento) {
		return repository
				.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndFechaVencimientoGreaterThanEqualAndFechaBajaIsNullAndFechaEnvioIsNull(
						facultadId, tipoChequeraId, chequeraSerieId, fechaVencimiento);
	}

	public List<Debito> findAllPendientes(OffsetDateTime desde, OffsetDateTime hasta, Boolean soloSantander,
			Boolean soloOtrosBancos, Integer debitoTipoId) {
		if (soloSantander) {
			return repository
					.findAllByFechaVencimientoBetweenAndFechaEnvioIsNullAndFechaBajaIsNullAndCbuLikeAndDebitoTipoId(
							desde, hasta, "072%", debitoTipoId);
		}
		if (soloOtrosBancos) {
			return repository
					.findAllByFechaVencimientoBetweenAndFechaEnvioIsNullAndFechaBajaIsNullAndCbuNotLikeAndDebitoTipoId(
							desde, hasta, "072%", debitoTipoId);
		}
		return repository.findAllByFechaVencimientoBetweenAndFechaBajaAndFechaEnvioAndDebitoTipoId(desde, hasta, null,
				null, debitoTipoId);
	}

	public List<Debito> findAllByAlternativa(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
			Integer alternativaId, Integer debitoTipoId) {
		return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndDebitoTipoId(
				facultadId, tipoChequeraId, chequeraSerieId, alternativaId, debitoTipoId);
	}

	public List<Debito> findAllByCbu(String cbu, Integer debitoTipoId) {
		return repository.findAllByCbuAndDebitoTipoId(cbu, debitoTipoId);
	}

	public List<Debito> findAllByVisa(String numeroTarjeta, Integer debitoTipoId) {
		return repository.findAllByNumeroTarjetaAndDebitoTipoId(numeroTarjeta, debitoTipoId);
	}

	public List<Debito> findAllAsociados(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
			Integer productoId, Integer alternativaId, Integer cuotaId) {
		return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
				facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
	}

	public Debito findByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId,
			Integer alternativaId, Integer cuotaId, Integer debitoTipoId) {
		return repository
				.findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaIdAndDebitoTipoId(
						facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId, debitoTipoId)
				.orElseThrow(() -> new DebitoException(facultadId, tipoChequeraId, chequeraSerieId, productoId,
						alternativaId, cuotaId, debitoTipoId));
	}

	public Debito findLastByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
			Integer alternativaId) {
		return repository
				.findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdOrderByFechaVencimientoDesc(
						facultadId, tipoChequeraId, chequeraSerieId, alternativaId)
				.orElseThrow(
						() -> new DebitoException(facultadId, tipoChequeraId, chequeraSerieId, alternativaId));

	}

	public Debito findLastActiveByCbu(String cbu1, String cbu2) {
		return repository.findTop1ByCbuStartingWithAndCbuEndingWithAndFechaBajaIsNullOrderByDebitoIdDesc(cbu1, cbu2)
				.orElseThrow(() -> new DebitoException(cbu1, cbu2));
	}

	public Debito findByDebitoId(Long debitoId) {
		return repository.findByDebitoId(debitoId).orElseThrow(() -> new DebitoException(debitoId));
	}

	public Debito add(Debito debito) {
		debito = repository.save(debito);
		return debito;
	}

	public Debito update(Debito newDebito, Long debitoId) {
		return repository.findByDebitoId(debitoId).map(debito -> {
			debito = new Debito(debitoId, newDebito.getFacultadId(), newDebito.getTipoChequeraId(),
					newDebito.getChequeraSerieId(), newDebito.getChequeraId(), newDebito.getProductoId(),
					newDebito.getAlternativaId(), newDebito.getCuotaId(), newDebito.getFechaVencimiento(),
					newDebito.getFechaBaja(), newDebito.getDebitoTipoId(), newDebito.getCbu(),
					newDebito.getNumeroTarjeta(), newDebito.getObservaciones(), newDebito.getFechaEnvio(),
					newDebito.getRechazado(), newDebito.getMotivoRechazo(), newDebito.getArchivoBancoId(), null, null);
			debito = repository.save(debito);
			return debito;
		}).orElseThrow(() -> new DebitoException(debitoId));
	}

	@Transactional
	public List<Debito> saveAll(List<Debito> debitos) {
		return repository.saveAll(debitos);
	}

}

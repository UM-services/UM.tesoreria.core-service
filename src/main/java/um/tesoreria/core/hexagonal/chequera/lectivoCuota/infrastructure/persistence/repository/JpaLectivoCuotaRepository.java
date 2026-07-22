package um.tesoreria.core.hexagonal.chequera.lectivoCuota.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.hexagonal.chequera.lectivoCuota.infrastructure.persistence.entity.LectivoCuotaEntity;

@Repository
public interface JpaLectivoCuotaRepository extends JpaRepository<LectivoCuotaEntity, Long> {

	List<LectivoCuotaEntity> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId,
	                                                                                 Integer tipoChequeraId);

	List<LectivoCuotaEntity> findAllByFacultadIdAndLectivoIdAndTipoChequeraIdAndAlternativaId(Integer facultadId,
	                                                                                                 Integer lectivoId, Integer tipoChequeraId, Integer alternativaId);

	Optional<LectivoCuotaEntity> findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaIdAndCuotaId(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, Integer cuotaId);

	List<LectivoCuotaEntity> findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaIdAndVencimiento2GreaterThanEqualAndImporte2GreaterThan(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, OffsetDateTime fechaReferencia, BigDecimal importeReferencia);

}

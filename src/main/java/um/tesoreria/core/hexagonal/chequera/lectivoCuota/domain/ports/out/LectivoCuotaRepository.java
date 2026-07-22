package um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LectivoCuotaRepository {
    List<LectivoCuota> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId, Integer tipoChequeraId);
    List<LectivoCuota> findAllByFacultadIdAndLectivoIdAndTipoChequeraIdAndAlternativaId(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer alternativaId);
    Optional<LectivoCuota> findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaIdAndCuotaId(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, Integer cuotaId);
    List<LectivoCuota> findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaId(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, OffsetDateTime fechaReferencia);
}

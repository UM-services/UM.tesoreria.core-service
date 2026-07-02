package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.DeudaData;
import um.tesoreria.core.model.internal.CuotaPeriodoDto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ChequeraCuotaRepository {

    DeudaData findDeudaData(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);

    Optional<ChequeraCuota> findCuota1(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);

    List<ChequeraCuota> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

    List<ChequeraCuota> findAllByChequeraAlternativa(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);

    List<ChequeraCuota> findAllByChequeraAlternativaConImporte(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);

    List<ChequeraCuota> findAllByCuotasActivas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Byte baja);

    List<ChequeraCuota> findAllByCuotasPagadas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Byte pagado);

    List<ChequeraCuota> findAllDebidas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, OffsetDateTime referencia);

    List<ChequeraCuota> findAllPendientes(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);

    List<ChequeraCuota> findAllPendientesBaja(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);

    Optional<ChequeraCuota> findByChequeraCuotaId(Long chequeraCuotaId);

    Optional<ChequeraCuota> findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId);

    Optional<ChequeraCuota> findOneActivaImpagaPrevia(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, OffsetDateTime referencia);

    List<ChequeraCuota> saveAll(List<ChequeraCuota> chequeraCuotas);

    ChequeraCuota save(ChequeraCuota chequeraCuota);

    void deleteAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

    List<CuotaPeriodoDto> findCuotaPeriodosByLectivoId(Integer lectivoId);

    List<ChequeraCuota> findAllByChequeraIds(List<Long> chequeraIds);

    List<ChequeraCuota> findAllByFacultadTipoChequeraSerieIds(Integer facultadId, Integer tipoChequeraId, List<Long> chequeraSerieIds);

}

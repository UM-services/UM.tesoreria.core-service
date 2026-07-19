package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.exception.ChequeraCuotaException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.*;
import um.tesoreria.core.model.internal.CuotaPeriodoDto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChequeraCuotaService {

    private final FindAllCuotasByChequeraUseCase findAllByChequeraUseCase;
    private final FindAllByChequeraAlternativaUseCase findAllByChequeraAlternativaUseCase;
    private final FindAllByChequeraAlternativaConImporteUseCase findAllByChequeraAlternativaConImporteUseCase;
    private final FindAllDebidasUseCase findAllDebidasUseCase;
    private final FindAllInconsistenciasUseCase findAllInconsistenciasUseCase;
    private final FindAllPendientesUseCase findAllPendientesUseCase;
    private final FindAllPendientesBajaUseCase findAllPendientesBajaUseCase;
    private final GetChequeraCuotaByIdUseCase getChequeraCuotaByIdUseCase;
    private final GetChequeraCuotaByUniqueUseCase getChequeraCuotaByUniqueUseCase;
    private final GetOneActivaImpagaPreviaUseCase getOneActivaImpagaPreviaUseCase;
    private final SaveAllChequeraCuotasUseCase saveAllChequeraCuotasUseCase;
    private final UpdateBarrasUseCase updateBarrasUseCase;
    private final DeleteAllByChequeraUseCase deleteAllByChequeraUseCase;
    private final UpdateChequeraCuotaUseCase updateChequeraCuotaUseCase;
    private final CalculateTotalCuotasActivasUseCase calculateTotalCuotasActivasUseCase;
    private final CalculateTotalCuotasPagadasUseCase calculateTotalCuotasPagadasUseCase;
    private final FindAllPeriodosLectivoUseCase findAllPeriodosLectivoUseCase;
    private final FindAllByChequeraIdsUseCase findAllByChequeraIdsUseCase;
    private final FindAllByFacultadTipoChequeraSerieIdsUseCase findAllByFacultadTipoChequeraSerieIdsUseCase;
    private final GetCuotaActualUseCase getCuotaActualUseCase;

    public List<ChequeraCuota> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return findAllByChequeraUseCase.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }

    public List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(
            Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return findAllByChequeraAlternativaUseCase.findAllByChequeraAlternativa(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
    }

    public List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdConImporte(
            Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return findAllByChequeraAlternativaConImporteUseCase.findAllByChequeraAlternativaConImporte(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
    }

    public List<ChequeraCuota> findAllDebidas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return findAllDebidasUseCase.findAllDebidas(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
    }

    public List<ChequeraCuota> findAllInconsistencias(OffsetDateTime desde, OffsetDateTime hasta, Boolean reduced) {
        return findAllInconsistenciasUseCase.findAllInconsistencias(desde, hasta, reduced);
    }

    public List<ChequeraCuota> findAllPendientes(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return findAllPendientesUseCase.findAllPendientes(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
    }

    public List<ChequeraCuota> findAllPendientesBaja(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return findAllPendientesBajaUseCase.findAllPendientesBaja(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
    }

    public ChequeraCuota findByChequeraCuotaId(Long chequeraCuotaId) {
        return getChequeraCuotaByIdUseCase.getChequeraCuotaById(chequeraCuotaId)
                .orElseThrow(() -> new ChequeraCuotaException(chequeraCuotaId));
    }

    public ChequeraCuota findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                            Integer productoId, Integer alternativaId, Integer cuotaId) {
        return getChequeraCuotaByUniqueUseCase.getChequeraCuotaByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId)
                .orElseThrow(() -> new ChequeraCuotaException(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId));
    }

    public ChequeraCuota findOneActivaImpagaPrevia(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                                          Integer alternativaId, OffsetDateTime referencia) {
        return getOneActivaImpagaPreviaUseCase.getOneActivaImpagaPrevia(facultadId, tipoChequeraId, chequeraSerieId, alternativaId, referencia)
                .orElseThrow(() -> new ChequeraCuotaException(facultadId, tipoChequeraId, chequeraSerieId, alternativaId));
    }

    public List<ChequeraCuota> saveAll(List<ChequeraCuota> chequeraCuotas) {
        return saveAllChequeraCuotasUseCase.saveAll(chequeraCuotas);
    }

    public List<ChequeraCuota> updateBarras(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return updateBarrasUseCase.updateBarras(facultadId, tipoChequeraId, chequeraSerieId);
    }

    public String calculateCodigoBarras(ChequeraCuota chequeraCuota) {
        return updateBarrasUseCase.calculateCodigoBarras(chequeraCuota);
    }

    public void deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(Integer facultadId, Integer tipoChequeraId,
            Long chequeraSerieId) {
        deleteAllByChequeraUseCase.deleteAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }

    public ChequeraCuota update(ChequeraCuota newChequeraCuota, Long chequeraCuotaId) {
        return updateChequeraCuotaUseCase.update(newChequeraCuota, chequeraCuotaId);
    }

    public BigDecimal calculateTotalCuotasActivas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
            Integer productoId, Integer alternativaId) {
        return calculateTotalCuotasActivasUseCase.calculateTotalCuotasActivas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId);
    }

    public BigDecimal calculateTotalCuotasPagadas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
            Integer productoId, Integer alternativaId) {
        return calculateTotalCuotasPagadasUseCase.calculateTotalCuotasPagadas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId);
    }

    public List<CuotaPeriodoDto> findAllPeriodosLectivo(Integer lectivoId) {
        return findAllPeriodosLectivoUseCase.findAllPeriodosLectivo(lectivoId);
    }

    public List<ChequeraCuota> findAllByChequeraIds(List<Long> chequeraIds) {
        return findAllByChequeraIdsUseCase.findAllByChequeraIds(chequeraIds);
    }

    public List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIds(Integer facultadId, Integer tipoChequeraId, List<Long> chequeraSerieIds) {
        return findAllByFacultadTipoChequeraSerieIdsUseCase.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIds(facultadId, tipoChequeraId, chequeraSerieIds);
    }

    public ChequeraCuota getCuotaActual(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                         Integer productoId, Integer alternativaId, OffsetDateTime fechaActual) {
        return getCuotaActualUseCase.getCuotaActual(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, fechaActual)
                .orElseThrow(() -> new ChequeraCuotaException(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId));
    }

}

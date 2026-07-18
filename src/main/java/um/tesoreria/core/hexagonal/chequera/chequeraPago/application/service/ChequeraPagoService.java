package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.exception.ChequeraPagoException;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChequeraPagoService {

    private final CreateChequeraPagoUseCase createChequeraPagoUseCase;
    private final DeletePagosByChequeraUseCase deletePagosByChequeraUseCase;
    private final FindPagosByTipoPagoAndFechaAcreditacionUseCase findPagosByTipoPagoAndFechaAcreditacionUseCase;
    private final FindPagosByTipoPagoAndFechaPagoUseCase findPagosByTipoPagoAndFechaPagoUseCase;
    private final FindPagosByChequeraUseCase findPagosByChequeraUseCase;
    private final FindPagosByCuotaUseCase findPagosByCuotaUseCase;
    private final FindPagosPendientesFacturaUseCase findPagosPendientesFacturaUseCase;
    private final FindPagosByFacultadAndTipoChequeraAndLectivoUseCase findPagosByFacultadAndTipoChequeraAndLectivoUseCase;
    private final GetChequeraPagoByIdUseCase getChequeraPagoByIdUseCase;
    private final GetChequeraPagoByIdMercadoPagoUseCase getChequeraPagoByIdMercadoPagoUseCase;
    private final IsPagadoUseCase isPagadoUseCase;
    private final GetNextOrdenUseCase getNextOrdenUseCase;

    public ChequeraPago create(ChequeraPago chequeraPago) {
        return createChequeraPagoUseCase.createChequeraPago(chequeraPago);
    }

    public void deleteByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        deletePagosByChequeraUseCase.deletePagosByChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }

    public List<ChequeraPago> findAllByTipoPagoIdAndFechaAcreditacion(Integer tipoPagoId, OffsetDateTime fechaAcreditacion) {
        return findPagosByTipoPagoAndFechaAcreditacionUseCase.findPagosByTipoPagoAndFechaAcreditacion(tipoPagoId, fechaAcreditacion);
    }

    public List<ChequeraPago> findAllByTipoPagoIdAndFechaPago(Integer tipoPagoId, OffsetDateTime fechaPago) {
        return findPagosByTipoPagoAndFechaPagoUseCase.findPagosByTipoPagoAndFechaPago(tipoPagoId, fechaPago);
    }

    public List<ChequeraPago> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return findPagosByChequeraUseCase.findPagosByChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }

    public List<ChequeraPago> findAllByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                              Integer productoId, Integer alternativaId, Integer cuotaId) {
        return findPagosByCuotaUseCase.findPagosByCuota(facultadId, tipoChequeraId, chequeraSerieId,
                productoId, alternativaId, cuotaId);
    }

    public List<ChequeraPago> pendientesFactura(OffsetDateTime fechaPago) {
        return findPagosPendientesFacturaUseCase.findPagosPendientesFactura(fechaPago);
    }

    public List<ChequeraPago> findAllByFacultadIdAndTipoChequeraIdAndLectivoId(Integer facultadId, Integer tipoChequeraId, Integer lectivoId) {
        return findPagosByFacultadAndTipoChequeraAndLectivoUseCase.findPagosByFacultadAndTipoChequeraAndLectivo(facultadId, tipoChequeraId, lectivoId);
    }

    public ChequeraPago findByChequeraPagoId(Long chequeraPagoId) {
        return getChequeraPagoByIdUseCase.getChequeraPagoById(chequeraPagoId)
                .orElseThrow(() -> new ChequeraPagoException(chequeraPagoId));
    }

    public ChequeraPago findByIdMercadoPago(String idMercadoPago) {
        return getChequeraPagoByIdMercadoPagoUseCase.getChequeraPagoByIdMercadoPago(idMercadoPago)
                .orElseThrow(() -> new ChequeraPagoException(idMercadoPago));
    }

    public boolean isPagado(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                            Integer productoId, Integer alternativaId, Integer cuotaId) {
        return isPagadoUseCase.isPagado(facultadId, tipoChequeraId, chequeraSerieId,
                productoId, alternativaId, cuotaId);
    }

    public Integer nextOrden(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                             Integer productoId, Integer alternativaId, Integer cuotaId) {
        return getNextOrdenUseCase.getNextOrden(facultadId, tipoChequeraId, chequeraSerieId,
                productoId, alternativaId, cuotaId);
    }

}

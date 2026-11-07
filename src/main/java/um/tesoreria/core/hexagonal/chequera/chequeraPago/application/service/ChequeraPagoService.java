package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.exception.ChequeraPagoException;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.*;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.exception.ChequeraTotalException;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
    private final FindPagosByArchivoUseCase findPagosByArchivoUseCase;
    private final DeletePagosByArchivoUseCase deletePagosByArchivoUseCase;
    private final UpdateChequeraPagoUseCase updateChequeraPagoUseCase;
    private final DeleteChequeraPagoUseCase deleteChequeraPagoUseCase;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraTotalService chequeraTotalService;

    public ChequeraPago create(ChequeraPago chequeraPago) {
        return createChequeraPagoUseCase.createChequeraPago(chequeraPago);
    }

    public ChequeraPago update(ChequeraPago chequeraPago) {
        return updateChequeraPagoUseCase.updateChequeraPago(chequeraPago);
    }

    public void delete(Long chequeraPagoId) {
        deleteChequeraPagoUseCase.deleteChequeraPago(chequeraPagoId);
    }

    /**
     * Marca la cuota como pagada (si hay algún pago) y recalcula los totales de la chequera.
     * Es la implementación compartida por la carga manual, las importaciones y el legacy.
     */
    public void marcarPago(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                           Integer productoId, Integer alternativaId, Integer cuotaId) {
        boolean pagado = isPagado(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
        ChequeraCuota cuota = chequeraCuotaService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId,
                productoId, alternativaId, cuotaId);
        cuota.setPagado((byte) (pagado ? 1 : 0));
        chequeraCuotaService.update(cuota, cuota.getChequeraCuotaId());
        recalcularTotal(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId);
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

    public List<ChequeraPago> findAllByArchivo(String archivo) {
        return findPagosByArchivoUseCase.findPagosByArchivo(archivo);
    }

    public void deleteAllByArchivo(String archivo) {
        deletePagosByArchivoUseCase.deletePagosByArchivo(archivo);
    }

    private void recalcularTotal(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                 Integer productoId, Integer alternativaId) {
        try {
            ChequeraTotal total = chequeraTotalService.findByUnique(facultadId, tipoChequeraId,
                    chequeraSerieId, productoId);
            total.setTotal(chequeraCuotaService.calculateTotalCuotasActivas(facultadId, tipoChequeraId,
                    chequeraSerieId, productoId, alternativaId));
            total.setPagado(chequeraCuotaService.calculateTotalCuotasPagadas(facultadId, tipoChequeraId,
                    chequeraSerieId, productoId, alternativaId));
            chequeraTotalService.update(total, total.getChequeraTotalId());
        } catch (ChequeraTotalException e) {
            log.warn("No existe chequera_total para {}.{}.{}.{}; se omite el recálculo",
                    facultadId, tipoChequeraId, chequeraSerieId, productoId);
        }
    }

}

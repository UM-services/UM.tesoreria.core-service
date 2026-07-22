package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.exception.ChequeraCuotaException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.exception.LectivoCuotaException;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.domain.ports.in.RecalculateCuotaByUniqueIndexUseCase;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.util.Tool;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecalculateCuotaByUniqueIndexUseCaseImpl implements RecalculateCuotaByUniqueIndexUseCase {

    private final ChequeraCuotaService chequeraCuotaService;
    private final LectivoCuotaService lectivoCuotaService;
    private final LectivoService lectivoService;

    @Override
    public ChequeraCuota recalculateCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId, Integer plazo) {
        log.debug("\n\nProcessing RecalculateCuotaByUniqueIndexUseCaseImpl.recalculateCuota\n\n");
        var ahora = OffsetDateTime.now();
        var cuotaEnRevision = findCuotaEnRevision(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId, ahora);
        if (cuotaEnRevision.getVencimiento3().isAfter(ahora)) {
            return cuotaEnRevision;
        }
        var cuotaReferencia = resolveCuotaReferencia(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId, ahora);
        var importeReferencia = resolveImporteReferencia(cuotaEnRevision, cuotaReferencia);
        var fechaReferencia = Tool.firstTime(ahora).plusDays(plazo);
        cuotaEnRevision.setImporte3(importeReferencia);
        cuotaEnRevision.setVencimiento3(fechaReferencia);
        log.debug("Cuota nueva: {}", cuotaEnRevision.jsonify());
        return cuotaEnRevision;
    }

    private ChequeraCuota findCuotaEnRevision(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId, OffsetDateTime ahora) {
        log.debug("\n\nProcessing RecalculateCuotaByUniqueIndexUseCaseImpl.findCuotaEnRevision\n\n");
        try {
            var cuotaEnRevision = chequeraCuotaService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
            log.debug("Cuota en revision: {}", cuotaEnRevision.jsonify());
            return cuotaEnRevision;
        } catch (ChequeraCuotaException e) {
            log.error(e.getMessage());
            return findCuotaEnRevisionFromLectivo(facultadId, tipoChequeraId, productoId, alternativaId, cuotaId, ahora);
        }
    }

    private ChequeraCuota findCuotaEnRevisionFromLectivo(Integer facultadId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, Integer cuotaId, OffsetDateTime ahora) {
        log.debug("\n\nProcessing RecalculateCuotaByUniqueIndexUseCaseImpl.findCuotaEnRevisionFromLectivo\n\n");
        var lectivo = lectivoService.findByFecha(ahora);
        var lectivoCuota = lectivoCuotaService.findCuotaByFecha(facultadId, lectivo.getLectivoId(), tipoChequeraId, productoId, alternativaId, ahora);
        log.debug("LectivoCuota: {}", lectivoCuota.jsonify());
        return buildChequeraCuotaFromLectivo(facultadId, tipoChequeraId, productoId, alternativaId, cuotaId, lectivoCuota);
    }

    private ChequeraCuota resolveCuotaReferencia(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId, OffsetDateTime ahora) {
        log.debug("\n\nProcessing RecalculateCuotaByUniqueIndexUseCaseImpl.resolveCuotaReferencia\n\n");
        try {
            var cuotaReferencia = chequeraCuotaService.getCuotaActual(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, ahora);
            log.debug("Cuota referencia: {}", cuotaReferencia.jsonify());
            return cuotaReferencia;
        } catch (ChequeraCuotaException e) {
            var lectivo = lectivoService.findByFecha(ahora);
            LectivoCuota lectivoCuota;
            try {
                lectivoCuota = lectivoCuotaService.findCuotaByFecha(facultadId, lectivo.getLectivoId(), tipoChequeraId, productoId, alternativaId, ahora);
                log.debug("LectivoCuota: {}", lectivoCuota.jsonify());
            } catch (LectivoCuotaException ex) {
                log.error("No hay LectivoCuota de referencia");
                lectivoCuota = LectivoCuota.builder()
                        .importe1(BigDecimal.ZERO)
                        .importe2(BigDecimal.ZERO)
                        .importe3(BigDecimal.ZERO)
                        .build();
            }
            return buildChequeraCuotaFromLectivo(facultadId, tipoChequeraId, productoId, alternativaId, cuotaId, lectivoCuota);
        }
    }

    private BigDecimal resolveImporteReferencia(ChequeraCuota cuotaEnRevision, ChequeraCuota cuotaReferencia) {
        log.debug("\n\nProcessing RecalculateCuotaByUniqueIndexUseCaseImpl.resolveImporteReferencia\n\n");
        var importeBase = cuotaReferencia.getImporte3();
        if (cuotaEnRevision.getImporte3().compareTo(importeBase) > 0) {
            return cuotaEnRevision.getImporte3();
        }
        return importeBase;
    }

    private ChequeraCuota buildChequeraCuotaFromLectivo(Integer facultadId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, Integer cuotaId, LectivoCuota lectivoCuota) {
        log.debug("\n\nProcessing RecalculateCuotaByUniqueIndexUseCaseImpl.buildChequeraCuotaFromLectivo\n\n");
        var cuotaGenerada = ChequeraCuota.builder()
                .facultadId(facultadId)
                .tipoChequeraId(tipoChequeraId)
                .productoId(productoId)
                .alternativaId(alternativaId)
                .cuotaId(cuotaId)
                .importe1(lectivoCuota.getImporte1())
                .importe1Original(lectivoCuota.getImporte1())
                .vencimiento1(lectivoCuota.getVencimiento1())
                .importe2(lectivoCuota.getImporte2())
                .importe2Original(lectivoCuota.getImporte2())
                .vencimiento2(lectivoCuota.getVencimiento2())
                .importe3(lectivoCuota.getImporte3())
                .importe3Original(lectivoCuota.getImporte3())
                .vencimiento3(lectivoCuota.getVencimiento3())
                .build();
        log.debug("CuotaGenerada: {}", cuotaGenerada.jsonify());
        return cuotaGenerada;
    }

}

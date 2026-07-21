package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.exception.ChequeraCuotaException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
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
        var cuotaEnRevision = findCuotaEnRevision(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
        // Si todavía no pasa la segunda fecha de mora no hay nada que hacer
        if (cuotaEnRevision.getVencimiento3().isAfter(OffsetDateTime.now())) {
            return cuotaEnRevision;
        }
        var cuotaReferencia = resolveCuotaReferencia(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaEnRevision);
        var importeReferencia = resolveImporteReferencia(cuotaEnRevision, cuotaReferencia);
        var fechaReferencia = Tool.firstTime(OffsetDateTime.now()).plusDays(plazo);
        cuotaEnRevision.setImporte3(importeReferencia);
        cuotaEnRevision.setVencimiento3(fechaReferencia);
        log.debug("Cuota nueva: {}", cuotaEnRevision.jsonify());
        return cuotaEnRevision;
    }

    private ChequeraCuota findCuotaEnRevision(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId) {
        try {
            var cuotaEnRevision = chequeraCuotaService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
            log.debug("Cuota en revision: {}", cuotaEnRevision.jsonify());
            return cuotaEnRevision;
        } catch (ChequeraCuotaException e) {
            log.error(e.getMessage());
            return findCuotaEnRevisionFromLectivo(facultadId, tipoChequeraId, productoId, alternativaId, cuotaId);
        }
    }

    private ChequeraCuota findCuotaEnRevisionFromLectivo(Integer facultadId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, Integer cuotaId) {
        var lectivo = lectivoService.findByFecha(OffsetDateTime.now());
        var lectivoCuota = lectivoCuotaService.findByUniqueKey(facultadId, lectivo.getLectivoId(), tipoChequeraId, productoId, alternativaId, cuotaId);
        log.debug("LectivoCuota: {}", lectivoCuota.jsonify());
        return buildChequeraCuotaFromLectivo(facultadId, tipoChequeraId, productoId, alternativaId, cuotaId, lectivoCuota);
    }

    private ChequeraCuota resolveCuotaReferencia(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, ChequeraCuota cuotaEnRevision) {
        try {
            var cuotaReferencia = chequeraCuotaService.getCuotaActual(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, OffsetDateTime.now());
            log.debug("Cuota referencia: {}", cuotaReferencia.jsonify());
            return cuotaReferencia;
        } catch (ChequeraCuotaException e) {
            return ChequeraCuota.builder()
                    .importe1(cuotaEnRevision.getImporte3())
                    .build();
        }
    }

    private BigDecimal resolveImporteReferencia(ChequeraCuota cuotaEnRevision, ChequeraCuota cuotaReferencia) {
        var importeReferencia = cuotaReferencia.getImporte1();
        if (cuotaEnRevision.getImporte3().compareTo(importeReferencia) > 0) {
            importeReferencia = cuotaReferencia.getImporte3();
        }
        return importeReferencia;
    }

    private ChequeraCuota buildChequeraCuotaFromLectivo(Integer facultadId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, Integer cuotaId, LectivoCuota lectivoCuota) {
        return ChequeraCuota.builder()
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
    }

}

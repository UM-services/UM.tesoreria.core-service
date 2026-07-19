package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.exception.ChequeraCuotaException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.domain.ports.in.RecalculateCuotaByUniqueIndexUseCase;
import um.tesoreria.core.util.Tool;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecalculateCuotaByUniqueIndexUseCaseImpl implements RecalculateCuotaByUniqueIndexUseCase {

    private final ChequeraCuotaService chequeraCuotaService;

    @Override
    public ChequeraCuota recalculateCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId, Integer plazo) {
        var cuotaEnRevision = chequeraCuotaService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
        log.debug("Cuota en revision: {}", cuotaEnRevision.jsonify());
        // Si todavía no pasa la segunda fecha de mora no hay nada que hacer
        if (cuotaEnRevision.getVencimiento3().isAfter(OffsetDateTime.now())) {
            return cuotaEnRevision;
        }
        ChequeraCuota cuotaReferencia;
        try {
            cuotaReferencia = chequeraCuotaService.getCuotaActual(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, OffsetDateTime.now());
        } catch (ChequeraCuotaException e) {
            cuotaReferencia = ChequeraCuota.builder()
                    .importe1(cuotaEnRevision.getImporte3())
                    .build();
        }
        log.debug("Cuota referencia: {}", cuotaReferencia.jsonify());
        var importeReferencia = cuotaReferencia.getImporte1();
        if (cuotaEnRevision.getImporte3().compareTo(importeReferencia) > 0) {
            importeReferencia = cuotaReferencia.getImporte3();
        }
        var fechaReferencia = Tool.firstTime(OffsetDateTime.now()).plusDays(plazo);
        cuotaEnRevision.setImporte3(importeReferencia);
        cuotaEnRevision.setVencimiento3(fechaReferencia);
        log.debug("Cuota nueva: {}", cuotaEnRevision.jsonify());
        return cuotaEnRevision;
    }

}

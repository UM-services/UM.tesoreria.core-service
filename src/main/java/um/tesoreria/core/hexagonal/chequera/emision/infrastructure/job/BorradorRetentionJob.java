package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;

import java.time.OffsetDateTime;

/**
 * Retención de borradores: los ABIERTO sin actividad durante
 * {@link ReglasEmision#DIAS_RETENCION_BORRADOR} días pasan a DESCARTADO.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BorradorRetentionJob {

    private final BorradorChequeraRepository borradorRepository;

    @Scheduled(cron = "0 30 3 * * *")
    public void descartarVencidos() {
        try {
            int descartados = borradorRepository.descartarAnterioresA(
                    OffsetDateTime.now().minusDays(ReglasEmision.DIAS_RETENCION_BORRADOR));
            if (descartados > 0) {
                log.info("Retención de borradores: {} descartados", descartados);
            }
        } catch (RuntimeException e) {
            log.error("Error en la retención de borradores de emisión", e);
        }
    }
}

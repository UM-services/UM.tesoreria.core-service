package um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.repository.JpaChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Completa el {@code chequeraId} de cuotas legacy que quedó nulo.
 *
 * <p>Antes esta corrección se hacía dentro del camino de lectura
 * ({@code findAllByChequeraAlternativa}, {@code findDebidas}, {@code deudaextended}) con un
 * {@code saveAll} en cada request. Eso convertía un GET en una escritura y generaba eventos
 * de auditoría por un dato que ya debería estar persistido.</p>
 *
 * <p>El job es idempotente y no bloquea la lectura: procesa páginas de nulos hasta
 * {@code max-pages} por corrida. Si una cuota no puede resolverse (no existe la serie) se
 * deja como está y se corta cuando una página no aporta progreso.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChequeraCuotaChequeraIdBackfillJob {

    private final JpaChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;

    @Value("${tesoreria.chequera-cuota.backfill.enabled:true}")
    private boolean enabled;

    @Value("${tesoreria.chequera-cuota.backfill.page-size:500}")
    private int pageSize;

    @Value("${tesoreria.chequera-cuota.backfill.max-pages:20}")
    private int maxPages;

    @Scheduled(fixedDelayString = "${tesoreria.chequera-cuota.backfill.delay-ms:600000}",
            initialDelayString = "${tesoreria.chequera-cuota.backfill.initial-delay-ms:60000}")
    @Transactional
    public void run() {
        if (!enabled) {
            return;
        }

        Map<String, Optional<Long>> seriesCache = new HashMap<>();
        int fixed = 0;

        for (int page = 0; page < maxPages; page++) {
            var entities = repository.findAllByChequeraIdIsNull(
                    PageRequest.of(0, pageSize, Sort.by("chequeraCuotaId")));
            if (entities.isEmpty()) {
                break;
            }

            List<ChequeraCuotaEntity> toSave = new ArrayList<>();
            for (ChequeraCuotaEntity entity : entities) {
                Long chequeraId = resolveChequeraId(entity, seriesCache);
                if (chequeraId != null && !chequeraId.equals(entity.getChequeraId())) {
                    entity.setChequeraId(chequeraId);
                    toSave.add(entity);
                }
            }

            if (toSave.isEmpty()) {
                log.warn("Backfill chequeraId: {} cuota(s) sin resolver; se reintenta en la próxima corrida",
                        entities.getNumberOfElements());
                break;
            }

            repository.saveAll(toSave);
            repository.flush();
            fixed += toSave.size();

            if (entities.getNumberOfElements() < pageSize) {
                break;
            }
        }

        if (fixed > 0) {
            log.info("Backfill chequeraId: {} cuota(s) corregidas", fixed);
        }
    }

    private Long resolveChequeraId(ChequeraCuotaEntity entity, Map<String, Optional<Long>> cache) {
        String key = entity.getFacultadId() + "-" + entity.getTipoChequeraId() + "-" + entity.getChequeraSerieId();
        return cache.computeIfAbsent(key, ignored -> {
            try {
                return Optional.ofNullable(chequeraSerieService
                        .findByUnique(entity.getFacultadId(), entity.getTipoChequeraId(), entity.getChequeraSerieId())
                        .getChequeraId());
            } catch (RuntimeException e) {
                return Optional.empty();
            }
        }).orElse(null);
    }

}

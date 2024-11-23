package um.tesoreria.core.service.facade;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.ChequeraCuotaException;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.kotlin.model.ChequeraCuota;
import um.tesoreria.core.kotlin.model.dto.UMPreferenceMPDto;
import um.tesoreria.core.model.MercadoPagoContext;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.service.MercadoPagoContextService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class MercadoPagoCoreService {

    private final ChequeraCuotaService chequeraCuotaService;
    private final MercadoPagoContextService mercadoPagoContextService;

    private record VencimientoMP(OffsetDateTime fechaVencimiento, BigDecimal importe) {}

    public MercadoPagoCoreService(ChequeraCuotaService chequeraCuotaService,
                                  MercadoPagoContextService mercadoPagoContextService) {
        this.chequeraCuotaService = chequeraCuotaService;
        this.mercadoPagoContextService = mercadoPagoContextService;
    }

    @Transactional
    public UMPreferenceMPDto makeContext(Long chequeraCuotaId) {
        log.debug("Processing makeContext");
        var today = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        ChequeraCuota chequeraCuota = getChequeraCuota(chequeraCuotaId);
        if (chequeraCuota == null || !isCuotaAvailable(chequeraCuota)) return null;

        MercadoPagoContext existingContext = findExistingContext(chequeraCuotaId, chequeraCuota);
        if (existingContext != null && existingContext.getPreferenceId() != null && isContextUnchanged(chequeraCuota, existingContext)) {
            return buildResponse(existingContext, chequeraCuota);
        }

        deactivateExistingContexts(chequeraCuotaId);
        if (today.isAfter(chequeraCuota.getVencimiento3())) return null;

        VencimientoMP vencimiento = determineVencimientoMP(chequeraCuota, today);
        if (vencimiento.importe().compareTo(BigDecimal.ZERO) == 0) return null;

        existingContext = findExistingContext(chequeraCuotaId, chequeraCuota);
        MercadoPagoContext newContext = createOrUpdateContext(existingContext, chequeraCuotaId, vencimiento);
        return buildResponse(newContext, chequeraCuota);
    }

    private boolean isCuotaAvailable(ChequeraCuota chequeraCuota) {
        log.debug("Processing isCuotaAvailable");
        return chequeraCuota.getPagado() != 1 && chequeraCuota.getBaja() != 1 && chequeraCuota.getCompensada() != 1;
    }

    private MercadoPagoContext findExistingContext(Long chequeraCuotaId, ChequeraCuota chequeraCuota) {
        log.debug("Processing findExistingContext");
        try {
            return mercadoPagoContextService.findActiveByChequeraCuotaId(chequeraCuotaId);
        } catch (MercadoPagoContextException e) {
            log.debug("MercadoPagoContext Error -> {}", e.getMessage());
            return null;
        }
    }

    private boolean isContextUnchanged(ChequeraCuota chequeraCuota, MercadoPagoContext existingContext) {
        log.debug("Processing isContextUnchanged");
        return (isEqual(chequeraCuota.getImporte1(), existingContext) && isEqual(chequeraCuota.getVencimiento1(), existingContext)) ||
               (isEqual(chequeraCuota.getImporte2(), existingContext) && isEqual(chequeraCuota.getVencimiento2(), existingContext)) ||
               (isEqual(chequeraCuota.getImporte3(), existingContext) && isEqual(chequeraCuota.getVencimiento3(), existingContext));
    }

    private boolean isEqual(BigDecimal importe, MercadoPagoContext context) {
        log.debug("Processing isEqual");
        return importe.compareTo(context.getImporte()) == 0;
    }

    private boolean isEqual(OffsetDateTime vencimiento, MercadoPagoContext context) {
        log.debug("Processing isEqual");
        return Objects.requireNonNull(vencimiento).isEqual(context.getFechaVencimiento());
    }

    private MercadoPagoContext createOrUpdateContext(MercadoPagoContext existingContext, Long chequeraCuotaId, VencimientoMP vencimiento) {
        log.debug("Processing createOrUpdateContext");
        if (existingContext == null) {
            return mercadoPagoContextService.add(MercadoPagoContext.builder()
                    .chequeraCuotaId(chequeraCuotaId)
                    .fechaVencimiento(vencimiento.fechaVencimiento())
                    .importe(vencimiento.importe())
                    .importePagado(BigDecimal.ZERO)
                    .activo((byte) 1)
                    .changed((byte) 0)
                    .build());
        } else {
            existingContext.setFechaVencimiento(vencimiento.fechaVencimiento());
            existingContext.setImporte(vencimiento.importe());
            existingContext.setChanged((byte) 1);
            return mercadoPagoContextService.update(existingContext, existingContext.getMercadoPagoContextId());
        }
    }

    private UMPreferenceMPDto buildResponse(MercadoPagoContext context, ChequeraCuota chequeraCuota) {
        log.debug("Processing buildResponse");
        return new UMPreferenceMPDto.Builder()
                .mercadoPagoContext(context)
                .chequeraCuota(chequeraCuota)
                .build();
    }

    private ChequeraCuota getChequeraCuota(Long id) {
        log.debug("Processing getChequeraCuota");
        try {
            return chequeraCuotaService.findByChequeraCuotaId(id);
        } catch (ChequeraCuotaException e) {
            log.debug("ChequeraCuota Error -> {}", e.getMessage());
            return null;
        }
    }

    private void deactivateExistingContexts(Long chequeraCuotaId) {
        log.debug("Processing deactivateExistingContexts");
        List<MercadoPagoContext> contexts = mercadoPagoContextService.findAllByChequeraCuotaIdAndActivo(chequeraCuotaId, (byte) 1);
        contexts.forEach(context -> {
            context.setActivo((byte) 0);
            context.setChanged((byte) 0);
        });
        mercadoPagoContextService.saveAll(contexts);
    }

    private VencimientoMP determineVencimientoMP(ChequeraCuota cuota, OffsetDateTime today) {
        log.debug("Processing determineVencimientoMP");
        if (today.isEqual(Objects.requireNonNull(cuota.getVencimiento1())) || today.isBefore(cuota.getVencimiento1())) {
            return new VencimientoMP(cuota.getVencimiento1(), cuota.getImporte1());
        }
        if (today.isEqual(Objects.requireNonNull(cuota.getVencimiento2())) || today.isBefore(cuota.getVencimiento2())) {
            return new VencimientoMP(cuota.getVencimiento2(), cuota.getImporte2());
        }
        return new VencimientoMP(cuota.getVencimiento3(), cuota.getImporte3());
    }

    public MercadoPagoContext updateContext(MercadoPagoContext mercadoPagoContext, Long mercadoPagoContextId) {
        log.debug("Processing updateContext");
        return mercadoPagoContextService.update(mercadoPagoContext, mercadoPagoContextId);
    }

    public MercadoPagoContext findContextByMercadoPagoContextId(Long mercadoPagoContextId) {
        log.debug("Processing findContextByMercadoPagoContextId");
        return mercadoPagoContextService.findByMercadoPagoContextId(mercadoPagoContextId);
    }

}

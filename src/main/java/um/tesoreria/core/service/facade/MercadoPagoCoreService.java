package um.tesoreria.core.service.facade;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.ChequeraCuotaException;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.kotlin.model.ChequeraCuota;
import um.tesoreria.core.kotlin.model.dto.UMPreferenceMPDto;
import um.tesoreria.core.model.MercadoPagoContext;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.service.MercadoPagoContextService;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class MercadoPagoCoreService {

    private final ChequeraCuotaService chequeraCuotaService;
    private final MercadoPagoContextService mercadoPagoContextService;

    private record VencimientoMP(OffsetDateTime fechaVencimiento, BigDecimal importe) {}

    @Transactional
    public UMPreferenceMPDto makeContext(Long chequeraCuotaId) {
        log.debug("\n\nProcessing MercadoPagoCoreService.makeContext\n\n");

        var today = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        ChequeraCuota chequeraCuota = getChequeraCuota(chequeraCuotaId);
        if (chequeraCuota == null || !isCuotaAvailable(chequeraCuota)) return null;

        var vencimiento = determineVencimientoMP(chequeraCuota, today);
        log.debug("\n\nVencimiento -> Fecha: {}, Importe: {}\n\n", vencimiento.fechaVencimiento().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), vencimiento.importe());
        if (vencimiento.importe().compareTo(BigDecimal.ZERO) == 0) return null;
        if (vencimiento.fechaVencimiento().isBefore(today)) return null;

        var mercadoPagoContext = findExistingContext(chequeraCuotaId);
        mercadoPagoContext = createOrUpdateContext(mercadoPagoContext, chequeraCuotaId, vencimiento);
        return buildResponse(mercadoPagoContext, chequeraCuota);
    }

    private boolean isCuotaAvailable(ChequeraCuota chequeraCuota) {
        log.debug("\n\nProcessing MercadoPagoCoreService.isCuotaAvailable\n\n");
        var result = chequeraCuota.getPagado() != 1 && chequeraCuota.getBaja() != 1 && chequeraCuota.getCompensada() != 1;
        log.debug("ChequeraCuota available result -> {}", result);
        return result;
    }

    private MercadoPagoContext findExistingContext(Long chequeraCuotaId) {
        log.debug("\n\nProcessing MercadoPagoCoreService.findExistingContext\n\n");
        try {
            return mercadoPagoContextService.findActiveByChequeraCuotaId(chequeraCuotaId);
        } catch (MercadoPagoContextException e) {
            log.debug("MercadoPagoContext Error -> {}", e.getMessage());
            return null;
        }
    }

    private MercadoPagoContext createOrUpdateContext(MercadoPagoContext mercadoPagoContext, Long chequeraCuotaId, VencimientoMP vencimiento) {
        log.debug("\n\nProcessing MercadoPagoCoreService.createOrUpdateContext\n\n");
        if (mercadoPagoContext == null) {
            mercadoPagoContext = mercadoPagoContextService.add(MercadoPagoContext.builder()
                    .chequeraCuotaId(chequeraCuotaId)
                    .fechaVencimiento(vencimiento.fechaVencimiento())
                    .importe(vencimiento.importe())
                    .importePagado(BigDecimal.ZERO)
                    .activo((byte) 1)
                    .changed((byte) 0)
                    .build());
            return mercadoPagoContext;
        }
        mercadoPagoContext.setFechaVencimiento(vencimiento.fechaVencimiento());
        mercadoPagoContext.setImporte(vencimiento.importe());
        mercadoPagoContext.setChanged((byte) 1);
        mercadoPagoContext.setLastVencimientoUpdated(OffsetDateTime.now(ZoneOffset.UTC));
        mercadoPagoContext = mercadoPagoContextService.update(mercadoPagoContext, mercadoPagoContext.getMercadoPagoContextId());
        return mercadoPagoContext;
    }

    private UMPreferenceMPDto buildResponse(MercadoPagoContext context, ChequeraCuota chequeraCuota) {
        log.debug("Processing MercadoPagoCoreService.buildResponse");
        return new UMPreferenceMPDto.Builder()
                .mercadoPagoContext(context)
                .chequeraCuota(chequeraCuota)
                .build();
    }

    private ChequeraCuota getChequeraCuota(Long id) {
        log.debug("\n\nProcessing MercadoPagoCoreService.getChequeraCuota\n\n");
        try {
            return chequeraCuotaService.findByChequeraCuotaId(id);
        } catch (ChequeraCuotaException e) {
            log.debug("ChequeraCuota Error -> {}", e.getMessage());
            return null;
        }
    }

    private void deactivateExistingContexts(Long chequeraCuotaId) {
        log.debug("Processing MercadoPagoCoreService.deactivateExistingContexts");
        List<MercadoPagoContext> contexts = mercadoPagoContextService.findAllByChequeraCuotaIdAndActivo(chequeraCuotaId, (byte) 1);
        contexts.forEach(context -> {
            context.setActivo((byte) 0);
            context.setChanged((byte) 0);
        });
        mercadoPagoContextService.saveAll(contexts);
    }

    private VencimientoMP determineVencimientoMP(ChequeraCuota cuota, OffsetDateTime today) {
        log.debug("\n\nProcessing MercadoPagoCoreService.determineVencimientoMP\n\n");
        if (today.isEqual(Objects.requireNonNull(cuota.getVencimiento1())) || today.isBefore(cuota.getVencimiento1())) {
            return new VencimientoMP(cuota.getVencimiento1(), cuota.getImporte1());
        }
        if (today.isEqual(Objects.requireNonNull(cuota.getVencimiento2())) || today.isBefore(cuota.getVencimiento2())) {
            return new VencimientoMP(cuota.getVencimiento2(), cuota.getImporte2());
        }
        return new VencimientoMP(cuota.getVencimiento3(), cuota.getImporte3());
    }

    public MercadoPagoContext updateContext(MercadoPagoContext mercadoPagoContext, Long mercadoPagoContextId) {
        log.debug("Processing MercadoPagoCoreService.updateContext");
        return mercadoPagoContextService.update(mercadoPagoContext, mercadoPagoContextId);
    }

    public MercadoPagoContext findContextByMercadoPagoContextId(Long mercadoPagoContextId) {
        log.debug("Processing MercadoPagoCoreService.findContextByMercadoPagoContextId");
        return mercadoPagoContextService.findByMercadoPagoContextId(mercadoPagoContextId);
    }

}

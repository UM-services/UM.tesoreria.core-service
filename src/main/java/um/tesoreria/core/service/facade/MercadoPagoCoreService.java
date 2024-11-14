package um.tesoreria.core.service.facade;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.ChequeraCuotaException;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.kotlin.model.ChequeraCuota;
import um.tesoreria.core.kotlin.model.MercadoPagoContext;
import um.tesoreria.core.kotlin.model.dto.UMPreferenceMPDto;
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

    public MercadoPagoCoreService(ChequeraCuotaService chequeraCuotaService,
                                  MercadoPagoContextService mercadoPagoContextService) {
        this.chequeraCuotaService = chequeraCuotaService;
        this.mercadoPagoContextService = mercadoPagoContextService;
    }

    @Transactional
    public UMPreferenceMPDto makeContext(Long chequeraCuotaId) {
        var today = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);

        // Obtener ChequeraCuota
        ChequeraCuota chequeraCuota = getChequeraCuota(chequeraCuotaId);
        if (chequeraCuota == null) return null;

        // Verifica que la cuota esté disponible para pagar
        if (chequeraCuota.getPagado() == 1 || chequeraCuota.getBaja() == 1 || chequeraCuota.getCompensada() == 1) return null;

        // Verificar contexto existente
        MercadoPagoContext existingContext = getExistingContext(chequeraCuotaId, today);
        if (existingContext != null) return new UMPreferenceMPDto.Builder()
                .mercadoPagoContext(existingContext)
                .chequeraCuota(chequeraCuota)
                .build();

        // Desactivar contextos anteriores
        deactivateExistingContexts(chequeraCuotaId);

        // Determinar fecha de vencimiento e importe
        if (today.isAfter(chequeraCuota.getVencimiento3())) return null;

        var fechaVencimiento = determinarFechaVencimiento(chequeraCuota, today);
        var importe = determinarImporte(chequeraCuota, fechaVencimiento);
        
        if (importe.compareTo(BigDecimal.ZERO) == 0) return null;

        // Crear nuevo contexto
        return new UMPreferenceMPDto.Builder()
                .mercadoPagoContext(mercadoPagoContextService.add(new MercadoPagoContext.Builder()
                        .chequeraCuotaId(chequeraCuotaId)
                        .fechaVencimiento(fechaVencimiento)
                        .importe(importe)
                        .activo((byte) 1)
                        .build()))
                .chequeraCuota(chequeraCuota)
                .build();
    }

    private ChequeraCuota getChequeraCuota(Long id) {
        try {
            return chequeraCuotaService.findByChequeraCuotaId(id);
        } catch (ChequeraCuotaException e) {
            log.debug("ChequeraCuota Error -> {}", e.getMessage());
            return null;
        }
    }

    private MercadoPagoContext getExistingContext(Long chequeraCuotaId, OffsetDateTime today) {
        try {
            MercadoPagoContext context = mercadoPagoContextService.findActiveByChequeraCuotaId(chequeraCuotaId);
            if (context != null && isValidContext(context, today)) {
                return context;
            }
        } catch (MercadoPagoContextException e) {
            log.debug("MercadoPagoContext Error -> {}", e.getMessage());
        }
        return null;
    }

    private boolean isValidContext(MercadoPagoContext context, OffsetDateTime today) {
        if (context.getFechaVencimiento() == null) {
            return false;
        }
        if (today.isBefore(context.getFechaVencimiento().plusDays(1)) && context.getImporte().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        return false;
    }

    private void deactivateExistingContexts(Long chequeraCuotaId) {
        List<MercadoPagoContext> contexts = mercadoPagoContextService.findAllByChequeraCuotaIdAndActivo(chequeraCuotaId, (byte) 1);
        contexts.forEach(context -> context.setActivo((byte) 0));
        mercadoPagoContextService.saveAll(contexts);
    }

    private OffsetDateTime determinarFechaVencimiento(ChequeraCuota cuota, OffsetDateTime today) {
        if (today.isEqual(Objects.requireNonNull(cuota.getVencimiento1())) || today.isBefore(cuota.getVencimiento1())) {
            return cuota.getVencimiento1();
        }
        if (today.isEqual(Objects.requireNonNull(cuota.getVencimiento2())) || today.isBefore(cuota.getVencimiento2())) {
            return cuota.getVencimiento2();
        }
        return cuota.getVencimiento3();
    }

    private BigDecimal determinarImporte(ChequeraCuota cuota, OffsetDateTime fechaVencimiento) {
        if (Objects.equals(fechaVencimiento, cuota.getVencimiento1())) return cuota.getImporte1();
        if (Objects.equals(fechaVencimiento, cuota.getVencimiento2())) return cuota.getImporte2();
        return cuota.getImporte3();
    }

    public MercadoPagoContext updateContext(MercadoPagoContext mercadoPagoContext, Long mercadoPagoContextId) {
        return mercadoPagoContextService.update(mercadoPagoContext, mercadoPagoContextId);
    }

    public MercadoPagoContext findContextByMercadoPagoContextId(Long mercadoPagoContextId) {
        return mercadoPagoContextService.findByMercadoPagoContextId(mercadoPagoContextId);
    }

}

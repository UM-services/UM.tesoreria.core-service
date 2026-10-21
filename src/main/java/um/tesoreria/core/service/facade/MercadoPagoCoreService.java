package um.tesoreria.core.service.facade;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.exception.ChequeraCuotaException;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.PreferenciaPatch;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.model.dto.UMPreferenceMPDto;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.MercadoPagoContextService;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.VencimientoMercadoPagoResolver;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.RenovacionMercadoPago;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.SanitizacionContexto;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.VencimientoMercadoPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MercadoPagoCoreService {

    private final ChequeraCuotaService chequeraCuotaService;
    private final MercadoPagoContextService mercadoPagoContextService;
    private final VencimientoMercadoPagoResolver vencimientoMercadoPagoResolver;

    @Transactional
    public List<UMPreferenceMPDto> findRenovaciones(OffsetDateTime hoy, int lookbackDias, boolean reintentos,
                                                   OffsetDateTime afterFecha, Long afterId, int size) {
        List<RenovacionMercadoPago> renovaciones = mercadoPagoContextService
                .findRenovaciones(hoy, lookbackDias, reintentos, afterFecha, afterId, size);
        return renovaciones.stream()
                .map(this::buildRenovacionResponse)
                .toList();
    }

    public List<SanitizacionContexto> sanitizeActiveContexts(boolean dryRun) {
        return mercadoPagoContextService.sanitizeActiveContexts(dryRun);
    }

    public MercadoPagoContext updatePreferencia(PreferenciaPatch datos, Long mercadoPagoContextId) {
        return mercadoPagoContextService.updatePreferencia(datos, mercadoPagoContextId);
    }

    private UMPreferenceMPDto buildRenovacionResponse(RenovacionMercadoPago renovacion) {
        var contexto = renovacion.contexto();
        // El JSON de la preferencia pesa ~6 KB y no es necesario para renovar: se omite en la respuesta.
        contexto.setPreference(null);
        return UMPreferenceMPDto.builder()
                .mercadoPagoContext(contexto)
                .chequeraCuota(renovacion.chequeraCuota())
                .fechaVencimientoDestino(renovacion.fechaVencimientoDestino())
                .importeDestino(renovacion.importeDestino())
                .build();
    }

    @Transactional
    public UMPreferenceMPDto makeContextCuota(Long chequeraCuotaId) {
        log.debug("\n\nProcessing MercadoPagoCoreService.makeContext\n\n");

        var today = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        ChequeraCuota chequeraCuota = getChequeraCuota(chequeraCuotaId);
        if (chequeraCuota == null || !isCuotaAvailable(chequeraCuota)) return null;

        var vencimiento = determineVencimientoMP(chequeraCuota, today);
        log.debug("\n\nVencimiento -> Fecha: {}, Importe: {}\n\n", vencimiento.fechaVencimiento().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), vencimiento.importe());
        if (vencimiento.importe().compareTo(BigDecimal.ZERO) == 0) return null;
        if (vencimiento.fechaVencimiento().isBefore(today)) return null;

        MercadoPagoContext mercadoPagoContext = findExistingContext(chequeraCuotaId);
        log.debug("\n\nIntentando crear MercadoPagoContext -> {}\n\n", mercadoPagoContext);
        mercadoPagoContext = createOrUpdateContext(mercadoPagoContext, chequeraCuotaId, vencimiento);
        var response = buildResponse(mercadoPagoContext, chequeraCuota);
        log.debug("\n\nUMPreferenceMPDto -> {}\n\n", response.jsonify());
        return response;
    }

    @Transactional
    public List<UMPreferenceMPDto> makeContextsChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        log.debug("\n\nProcessing MercadoPagoCoreService.makeContextsChequera\n\n");
        var today = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        List<ChequeraCuota> pendientes = chequeraCuotaService.findAllPendientes(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
        List<UMPreferenceMPDto> result = new java.util.ArrayList<>();
        
        for (var chequeraCuota : pendientes) {
            if (chequeraCuota == null || !isCuotaAvailable(chequeraCuota)) continue;

            var vencimiento = determineVencimientoMP(chequeraCuota, today);
            if (vencimiento.importe().compareTo(BigDecimal.ZERO) == 0) continue;
            if (vencimiento.fechaVencimiento().isBefore(today)) continue;

            MercadoPagoContext mercadoPagoContext = findExistingContext(chequeraCuota.getChequeraCuotaId());
            mercadoPagoContext = createOrUpdateContext(mercadoPagoContext, chequeraCuota.getChequeraCuotaId(), vencimiento);
            var response = buildResponse(mercadoPagoContext, chequeraCuota);
            result.add(response);
        }
        return result;
    }

    @Transactional
    public List<MercadoPagoContext> updateContexts(List<MercadoPagoContext> contexts) {
        log.debug("Processing MercadoPagoCoreService.updateContexts");
        return mercadoPagoContextService.saveAll(contexts);
    }

    @Transactional
    public UMPreferenceMPDto makeContextCuota(ChequeraCuota chequeraCuota, MercadoPagoContext existingContext) {
        var today = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        if (chequeraCuota == null || !isCuotaAvailable(chequeraCuota)) return null;

        var vencimiento = determineVencimientoMP(chequeraCuota, today);
        if (vencimiento.importe().compareTo(BigDecimal.ZERO) == 0) return null;
        if (vencimiento.fechaVencimiento().isBefore(today)) return null;

        var mercadoPagoContext = createOrUpdateContext(existingContext, chequeraCuota.getChequeraCuotaId(), vencimiento);
        return buildResponse(mercadoPagoContext, chequeraCuota);
    }

    private boolean isCuotaAvailable(ChequeraCuota chequeraCuota) {
        log.debug("\n\nProcessing MercadoPagoCoreService.isCuotaAvailable\n\n");
        var result = chequeraCuota.getPagado() == 0 && chequeraCuota.getBaja() == 0 && chequeraCuota.getCompensada() == 0;
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

    private MercadoPagoContext createOrUpdateContext(MercadoPagoContext mercadoPagoContext, Long chequeraCuotaId, VencimientoMercadoPago vencimiento) {
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

        if (mercadoPagoContext.getFechaVencimiento().isEqual(vencimiento.fechaVencimiento()) &&
                mercadoPagoContext.getImporte().compareTo(vencimiento.importe()) == 0) {
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
        return UMPreferenceMPDto.builder()
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

    private VencimientoMercadoPago determineVencimientoMP(ChequeraCuota cuota, OffsetDateTime today) {
        log.debug("\n\nProcessing MercadoPagoCoreService.determineVencimientoMP\n\n");
        return vencimientoMercadoPagoResolver.seleccionar(cuota, today);
    }

    public MercadoPagoContext updateContext(MercadoPagoContext mercadoPagoContext, Long mercadoPagoContextId) {
        log.debug("Processing MercadoPagoCoreService.updateContext");
        return mercadoPagoContextService.update(mercadoPagoContext, mercadoPagoContextId);
    }

    public MercadoPagoContext findContextByMercadoPagoContextId(Long mercadoPagoContextId) {
        log.debug("Processing MercadoPagoCoreService.findContextByMercadoPagoContextId");
        return mercadoPagoContextService.findByMercadoPagoContextId(mercadoPagoContextId);
    }

    @Transactional
    public UMPreferenceMPDto createContextVacante(ReservaVacante reservaVacante) {
        log.debug("\n\nProcessing MercadoPagoCoreService.makeContextVacante\n\n");
        MercadoPagoContext mercadoPagoContext = MercadoPagoContext.builder()
                .reservaVacanteId(reservaVacante.getReservaVacanteId())
                .fechaVencimiento(reservaVacante.getVencimiento())
                .importe(reservaVacante.getImporte())
                .changed((byte) 0)
                .activo((byte) 1)
                .build();
        mercadoPagoContext = mercadoPagoContextService.add(mercadoPagoContext);
        var response = buildResponseVacante(mercadoPagoContext, reservaVacante);
        log.debug("\n\nUMPreferenceMPDto -> {}\n\n", response.jsonify());
        return response;
    }

    private UMPreferenceMPDto buildResponseVacante(MercadoPagoContext context, ReservaVacante reservaVacante) {
        log.debug("\n\nProcessing MercadoPagoCoreService.buildResponseVacante\n\n");
        return UMPreferenceMPDto.builder()
                .mercadoPagoContext(context)
                .reservaVacante(reservaVacante)
                .build();
    }

}

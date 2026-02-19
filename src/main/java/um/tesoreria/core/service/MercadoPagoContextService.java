package um.tesoreria.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.event.PaymentProcessedEvent;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.model.MercadoPagoContext;
import um.tesoreria.core.repository.MercadoPagoContextRepository;
import um.tesoreria.core.service.facade.PagoService;
import um.tesoreria.core.util.Tool;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class MercadoPagoContextService {

    private final MercadoPagoContextRepository repository;
    private final PagoService pagoService;

    public MercadoPagoContextService(MercadoPagoContextRepository repository, @Lazy PagoService pagoService) {
        this.repository = repository;
        this.pagoService = pagoService;
    }

    public List<MercadoPagoContext> findAllByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo) {
        return repository.findAllByChequeraCuotaIdAndActivo(chequeraCuotaId, activo);
    }

    public List<MercadoPagoContext> findAllByChequeraCuotaIds(List<Long> chequeraCuotaIds) {
        List<MercadoPagoContext> result = repository.findAllByChequeraCuotaIdInAndActivo(chequeraCuotaIds, (byte) 1);
        return result != null ? result : List.of();
    }

    public List<Long> findAllActiveToChange() {
        var today = Tool.dateAbsoluteArgentina();
        var _90DaysAgo = today.minusDays(90);
        return Objects.requireNonNull(repository.findAllByActivoAndFechaVencimientoBetween((byte) 1, _90DaysAgo, today))
                .stream().filter(Objects::nonNull).map(MercadoPagoContext::getChequeraCuotaId).toList();
    }

    public MercadoPagoContext findByMercadoPagoContextId(Long mercadoPagoContextId) {
        return Objects.requireNonNull(repository.findByMercadoPagoContextId(mercadoPagoContextId))
                .orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para mercadoPagoContextId",
                        mercadoPagoContextId));
    }

    public MercadoPagoContext findActiveByChequeraCuotaId(Long chequeraCuotaId) {
        return Objects.requireNonNull(repository.findByChequeraCuotaIdAndActivo(chequeraCuotaId, (byte) 1))
                .orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para chequeraCuotaId",
                        chequeraCuotaId));
    }

    public List<Long> findAllActiveChequeraCuota() {
        return Objects.requireNonNull(repository.findAllByActivoOrderByMercadoPagoContextIdDesc((byte) 1)).stream()
                .filter(Objects::nonNull).map(MercadoPagoContext::getChequeraCuotaId).toList();
    }

    public List<MercadoPagoContext> findAllSinImputar() {
        return repository.findAllByStatusAndChequeraPagoIdIsNull("approved");
    }

    @Transactional
    public MercadoPagoContext add(MercadoPagoContext mercadoPagoContext) {
        return repository.save(mercadoPagoContext);
    }

    @Transactional
    public List<MercadoPagoContext> saveAll(List<MercadoPagoContext> mercadoPagoContexts) {
        return repository.saveAll(mercadoPagoContexts);
    }

    @Transactional
    public MercadoPagoContext update(MercadoPagoContext newMercadoPagoContext, Long mercadoPagoContextId) {
        if (mercadoPagoContextId == null || newMercadoPagoContext == null) {
            throw new MercadoPagoContextException("Invalid input parameters", mercadoPagoContextId);
        }
        log.debug("\n\nUpdating MercadoPagoContext with id: {}\n\n", mercadoPagoContextId);

        return Objects.requireNonNull(repository.findByMercadoPagoContextId(mercadoPagoContextId))
                .map(existing -> {
                    // Update managed entity fields (do not replace the instance)
                    existing.setChequeraCuotaId(newMercadoPagoContext.getChequeraCuotaId());
                    existing.setInitPoint(newMercadoPagoContext.getInitPoint());
                    existing.setFechaVencimiento(newMercadoPagoContext.getFechaVencimiento());
                    existing.setImporte(newMercadoPagoContext.getImporte());
                    existing.setChanged(newMercadoPagoContext.getChanged());
                    existing.setLastVencimientoUpdated(newMercadoPagoContext.getLastVencimientoUpdated());
                    existing.setPreferenceId(newMercadoPagoContext.getPreferenceId());
                    existing.setPreference(newMercadoPagoContext.getPreference());
                    existing.setActivo(newMercadoPagoContext.getActivo());
                    existing.setChequeraPagoId(newMercadoPagoContext.getChequeraPagoId());
                    existing.setIdMercadoPago(newMercadoPagoContext.getIdMercadoPago());
                    existing.setStatus(newMercadoPagoContext.getStatus());
                    existing.setFechaPago(newMercadoPagoContext.getFechaPago());
                    existing.setFechaAcreditacion(newMercadoPagoContext.getFechaAcreditacion());
                    existing.setImportePagado(newMercadoPagoContext.getImportePagado());
                    existing.setPayment(newMercadoPagoContext.getPayment());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new MercadoPagoContextException("Context not found for id", mercadoPagoContextId));
    }

    @Transactional
    public void processPaymentEvent(PaymentProcessedEvent event) {
        log.debug("\n\nProcessing payment event for contextId: {}\n\n", event.getMercadoPagoContextId());
        var context = findByMercadoPagoContextId(event.getMercadoPagoContextId());

        if (!Objects.equals(context.getChequeraCuotaId(), event.getChequeraCuotaId())) {
            log.error("Mismatch chequeraCuotaId for contextId: {}", event.getMercadoPagoContextId());
            return;
        }

        context.setIdMercadoPago(event.getPaymentId());
        context.setPayment(event.getPaymentJson());
        context.setImportePagado(event.getTransactionAmount());
        context.setFechaPago(event.getDateApproved());
        context.setFechaAcreditacion(event.getDateApproved()); // Assuming release date is same or handled elsewhere
        context.setStatus(event.getStatus());

        context = update(context, context.getMercadoPagoContextId());

        if ("approved".equals(context.getStatus())) {
            pagoService.registraPagoMP(context.getMercadoPagoContextId());
        }
    }

}

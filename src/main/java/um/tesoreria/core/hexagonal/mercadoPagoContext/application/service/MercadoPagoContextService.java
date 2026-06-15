package um.tesoreria.core.hexagonal.mercadoPagoContext.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.event.PaymentProcessedEvent;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.*;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MercadoPagoContextService {

    private final AddMercadoPagoContextUseCase addMercadoPagoContextUseCase;
    private final FindActiveByChequeraCuotaIdUseCase findActiveByChequeraCuotaIdUseCase;
    private final FindAllActiveChequeraCuotaUseCase findAllActiveChequeraCuotaUseCase;
    private final FindAllActiveToChangeUseCase findAllActiveToChangeUseCase;
    private final FindAllByChequeraCuotaIdAndActivoUseCase findAllByChequeraCuotaIdAndActivoUseCase;
    private final FindAllByChequeraCuotaIdsUseCase findAllByChequeraCuotaIdsUseCase;
    private final FindAllSinImputarUseCase findAllSinImputarUseCase;
    private final FindByMercadoPagoContextIdUseCase findByMercadoPagoContextIdUseCase;
    private final ProcessPaymentEventUseCase processPaymentEventUseCase;
    private final SaveAllMercadoPagoContextsUseCase saveAllMercadoPagoContextsUseCase;
    private final UpdateMercadoPagoContextUseCase updateMercadoPagoContextUseCase;

    public List<MercadoPagoContext> findAllByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo) {
        return findAllByChequeraCuotaIdAndActivoUseCase.findAllByChequeraCuotaIdAndActivo(chequeraCuotaId, activo);
    }

    public List<MercadoPagoContext> findAllByChequeraCuotaIds(List<Long> chequeraCuotaIds) {
        return findAllByChequeraCuotaIdsUseCase.findAllByChequeraCuotaIds(chequeraCuotaIds);
    }

    public List<Long> findAllActiveToChange() {
        return findAllActiveToChangeUseCase.findAllActiveToChange();
    }

    public MercadoPagoContext findByMercadoPagoContextId(Long mercadoPagoContextId) {
        return findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(mercadoPagoContextId);
    }

    public MercadoPagoContext findActiveByChequeraCuotaId(Long chequeraCuotaId) {
        log.debug("\n\nProcessing MercadoPagoContextService.findActiveByChequeraCuotaId\n\n");
        try {
            return findActiveByChequeraCuotaIdUseCase.findActiveByChequeraCuotaId(chequeraCuotaId);
        } catch (MercadoPagoContextException e) {
            log.debug("\n\nNo se encontró contexto activo\n\n");
            return null;
        }
    }

    public List<Long> findAllActiveChequeraCuota() {
        return findAllActiveChequeraCuotaUseCase.findAllActiveChequeraCuota();
    }

    public List<MercadoPagoContext> findAllSinImputar() {
        return findAllSinImputarUseCase.findAllSinImputar();
    }

    @Transactional
    public MercadoPagoContext add(MercadoPagoContext mercadoPagoContext) {
        return addMercadoPagoContextUseCase.add(mercadoPagoContext);
    }

    @Transactional
    public List<MercadoPagoContext> saveAll(List<MercadoPagoContext> mercadoPagoContexts) {
        return saveAllMercadoPagoContextsUseCase.saveAll(mercadoPagoContexts);
    }

    @Transactional
    public MercadoPagoContext update(MercadoPagoContext newMercadoPagoContext, Long mercadoPagoContextId) {
        return updateMercadoPagoContextUseCase.update(newMercadoPagoContext, mercadoPagoContextId);
    }

    @Transactional
    public void processPaymentEvent(PaymentProcessedEvent event) {
        processPaymentEventUseCase.processPaymentEvent(event);
    }

}

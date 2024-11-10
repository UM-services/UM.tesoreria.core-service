package um.tesoreria.core.service;

import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.kotlin.model.MercadoPagoContext;
import um.tesoreria.core.repository.MercadoPagoContextRepository;

import java.util.List;
import java.util.Objects;

@Service
public class MercadoPagoContextService {

    private final MercadoPagoContextRepository repository;

    public MercadoPagoContextService(MercadoPagoContextRepository repository) {
        this.repository = repository;
    }

    public List<MercadoPagoContext> findAllByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo) {
        return repository.findAllByChequeraCuotaIdAndActivo(chequeraCuotaId, activo);
    }

    public MercadoPagoContext findByMercadoPagoContextId(Long mercadoPagoContextId) {
        return Objects.requireNonNull(repository.findByMercadoPagoContextId(mercadoPagoContextId)).orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para mercadoPagoContextId", mercadoPagoContextId));
    }

    public MercadoPagoContext findActiveByChequeraCuotaId(Long chequeraCuotaId) {
        return Objects.requireNonNull(repository.findByChequeraCuotaIdAndActivo(chequeraCuotaId, (byte) 1)).orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para chequeraCuotaId", chequeraCuotaId));
    }

    public MercadoPagoContext add(MercadoPagoContext mercadoPagoContext) {
        return repository.save(mercadoPagoContext);
    }

    public List<MercadoPagoContext> saveAll(List<MercadoPagoContext> mercadoPagoContexts) {
        return repository.saveAll(mercadoPagoContexts);
    }

    public MercadoPagoContext update(MercadoPagoContext newMercadoPagoContext, Long mercadoPagoContextId) {
        return repository.findByMercadoPagoContextId(mercadoPagoContextId).map(mercadoPagoContext -> {
            mercadoPagoContext = new MercadoPagoContext.Builder()
                    .mercadoPagoContextId(mercadoPagoContextId)
                    .chequeraCuotaId(newMercadoPagoContext.getChequeraCuotaId())
                    .initPoint(newMercadoPagoContext.getInitPoint())
                    .fechaVencimiento(newMercadoPagoContext.getFechaVencimiento())
                    .importe(newMercadoPagoContext.getImporte())
                    .preference(newMercadoPagoContext.getPreference())
                    .activo(newMercadoPagoContext.getActivo())
                    .chequeraPagoId(newMercadoPagoContext.getChequeraPagoId())
                    .idMercadoPago(newMercadoPagoContext.getIdMercadoPago())
                    .payment(newMercadoPagoContext.getPayment())
                    .build();
            return repository.save(mercadoPagoContext);
        }).orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para mercadoPagoContextId", mercadoPagoContextId));
    }

}

package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.UpdateMercadoPagoContextUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateMercadoPagoContextUseCaseImpl implements UpdateMercadoPagoContextUseCase {

    private final MercadoPagoContextRepository repository;

    @Override
    public MercadoPagoContext update(MercadoPagoContext newMercadoPagoContext, Long mercadoPagoContextId) {
        if (mercadoPagoContextId == null || newMercadoPagoContext == null) {
            throw new MercadoPagoContextException("Invalid input parameters", mercadoPagoContextId);
        }
        log.debug("\n\nUpdating MercadoPagoContext with id: {}\n\n", mercadoPagoContextId);

        return repository.findByMercadoPagoContextId(mercadoPagoContextId)
                .map(existing -> {
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
}

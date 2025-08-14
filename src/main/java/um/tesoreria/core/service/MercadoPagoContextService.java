package um.tesoreria.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.model.MercadoPagoContext;
import um.tesoreria.core.repository.MercadoPagoContextRepository;
import um.tesoreria.core.util.Tool;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class MercadoPagoContextService {

    private final MercadoPagoContextRepository repository;

    public MercadoPagoContextService(MercadoPagoContextRepository repository) {
        this.repository = repository;
    }

    public List<MercadoPagoContext> findAllByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo) {
        return repository.findAllByChequeraCuotaIdAndActivo(chequeraCuotaId, activo);
    }

    public List<Long> findAllActiveToChange() {
        var today = Tool.dateAbsoluteArgentina();
        var _90DaysAgo = today.minusDays(90);
        return Objects.requireNonNull(repository.findAllByActivoAndFechaVencimientoBetween((byte) 1, _90DaysAgo, today)).stream().filter(Objects::nonNull).map(MercadoPagoContext::getChequeraCuotaId).toList();
    }

    public MercadoPagoContext findByMercadoPagoContextId(Long mercadoPagoContextId) {
        return Objects.requireNonNull(repository.findByMercadoPagoContextId(mercadoPagoContextId)).orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para mercadoPagoContextId", mercadoPagoContextId));
    }

    public MercadoPagoContext findActiveByChequeraCuotaId(Long chequeraCuotaId) {
        return Objects.requireNonNull(repository.findByChequeraCuotaIdAndActivo(chequeraCuotaId, (byte) 1)).orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para chequeraCuotaId", chequeraCuotaId));
    }

    public List<Long> findAllActiveChequeraCuota() {
        return Objects.requireNonNull(repository.findAllByActivoOrderByMercadoPagoContextIdDesc((byte) 1)).stream().filter(Objects::nonNull).map(MercadoPagoContext::getChequeraCuotaId).toList();
    }

    public List<MercadoPagoContext> findAllSinImputar() {
        return repository.findAllByStatusAndChequeraPagoIdIsNull("approved");
    }

    public MercadoPagoContext add(MercadoPagoContext mercadoPagoContext) {
        return repository.save(mercadoPagoContext);
    }

    public List<MercadoPagoContext> saveAll(List<MercadoPagoContext> mercadoPagoContexts) {
        return repository.saveAll(mercadoPagoContexts);
    }

    @Transactional
    public MercadoPagoContext update(MercadoPagoContext newMercadoPagoContext, Long mercadoPagoContextId) {
        if (mercadoPagoContextId == null || newMercadoPagoContext == null) {
            throw new MercadoPagoContextException("Invalid input parameters", mercadoPagoContextId);
        }

        log.debug("Updating MercadoPagoContext with id: {}", mercadoPagoContextId);
        log.debug("Incoming context data -> {}", newMercadoPagoContext.jsonify());

        return Objects.requireNonNull(repository.findByMercadoPagoContextId(mercadoPagoContextId))
                .map(existing -> {
                    log.debug("Existing before update -> {}", existing.jsonify());

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

                    log.debug("Before save (managed entity) -> {}", existing.jsonify());
                    MercadoPagoContext saved = repository.save(existing);
                    log.debug("After save -> {}", saved.jsonify());
                    return saved;
                })
                .orElseThrow(() -> new MercadoPagoContextException("Context not found for id", mercadoPagoContextId));
    }

}

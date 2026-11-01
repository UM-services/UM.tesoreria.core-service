package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindActiveByChequeraCuotaIdUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.util.Comparator;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindActiveByChequeraCuotaIdUseCaseImpl implements FindActiveByChequeraCuotaIdUseCase {

    private static final byte ACTIVO = 1;
    private static final byte NO_ACTIVO = 0;

    private final MercadoPagoContextRepository repository;

    @Override
    public MercadoPagoContext findActiveByChequeraCuotaId(Long chequeraCuotaId) {
        var activos = repository.findAllByChequeraCuotaIdAndActivo(chequeraCuotaId, ACTIVO);
        if (activos.isEmpty()) {
            throw new MercadoPagoContextException("No se encontró MPContext para chequeraCuotaId", chequeraCuotaId);
        }

        var ultimo = activos.stream()
                .max(Comparator.comparing(MercadoPagoContext::getMercadoPagoContextId))
                .orElseThrow();

        // Sanitizacion online: si la cuota quedo con mas de un contexto activo, se conserva el
        // ultimo y se desactivan los anteriores. No se borra nada: un pago tardio sobre un enlace
        // viejo se sigue registrando porque el webhook busca el contexto por id, no por activo.
        if (activos.size() > 1) {
            var anteriores = activos.stream()
                    .filter(contexto -> !Objects.equals(contexto.getMercadoPagoContextId(),
                            ultimo.getMercadoPagoContextId()))
                    .toList();
            anteriores.forEach(contexto -> {
                contexto.setActivo(NO_ACTIVO);
                contexto.setChanged((byte) 0);
            });
            repository.saveAll(anteriores);
            log.warn("Cuota {} con {} contextos activos: se conserva {} y se desactivan {}",
                    chequeraCuotaId, activos.size(), ultimo.getMercadoPagoContextId(),
                    anteriores.stream().map(MercadoPagoContext::getMercadoPagoContextId).toList());
        }

        return ultimo;
    }
}

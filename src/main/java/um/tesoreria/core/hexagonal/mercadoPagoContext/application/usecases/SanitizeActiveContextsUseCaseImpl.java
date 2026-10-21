package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.SanitizacionContexto;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.SanitizeActiveContextsUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class SanitizeActiveContextsUseCaseImpl implements SanitizeActiveContextsUseCase {

    private static final byte ACTIVO = 1;
    private static final byte NO_ACTIVO = 0;

    private final MercadoPagoContextRepository repository;

    @Override
    public List<SanitizacionContexto> sanitize(boolean dryRun) {
        var chequeraCuotaIds = repository.findChequeraCuotaIdsWithMultipleActiveContexts();
        var resultado = new ArrayList<SanitizacionContexto>();

        for (var chequeraCuotaId : chequeraCuotaIds) {
            var activos = repository.findAllByChequeraCuotaIdAndActivo(chequeraCuotaId, ACTIVO);
            if (activos.size() < 2) {
                continue;
            }

            var conservado = activos.stream()
                    .max(Comparator.comparing(MercadoPagoContext::getMercadoPagoContextId))
                    .orElseThrow();
            var aDesactivar = activos.stream()
                    .filter(contexto -> !Objects.equals(contexto.getMercadoPagoContextId(),
                            conservado.getMercadoPagoContextId()))
                    .toList();

            aDesactivar.forEach(contexto -> {
                contexto.setActivo(NO_ACTIVO);
                contexto.setChanged((byte) 0);
            });

            if (!dryRun) {
                repository.saveAll(aDesactivar);
                log.info("Contextos duplicados desactivados para cuota {}: conservado {}, desactivados {}",
                        chequeraCuotaId, conservado.getMercadoPagoContextId(),
                        aDesactivar.stream().map(MercadoPagoContext::getMercadoPagoContextId).toList());
            }

            resultado.add(new SanitizacionContexto(chequeraCuotaId,
                    conservado.getMercadoPagoContextId(),
                    aDesactivar.stream().map(MercadoPagoContext::getMercadoPagoContextId).toList()));
        }

        log.info("Sanitizacion de contextos activos (dryRun={}): {} cuotas con duplicados", dryRun, resultado.size());
        return resultado;
    }
}

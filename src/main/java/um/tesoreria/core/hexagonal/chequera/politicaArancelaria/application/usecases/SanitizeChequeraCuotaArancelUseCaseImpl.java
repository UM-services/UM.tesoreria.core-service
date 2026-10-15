package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.exception.ArancelPorcentajeException;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.service.ArancelPorcentajeService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.exception.LectivoCuotaException;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.domain.ports.in.SanitizeChequeraCuotaArancelUseCase;

import java.math.BigDecimal;

/**
 * Sanea los importes de una {@link ChequeraCuota} aplicando el porcentaje de arancel parcial y emite el
 * diagnóstico de consistencia contra el diseño ({@code lectivo_cuota}).
 *
 * <p>Este saneamiento compensa el hecho de que algunos generadores escriben el importe completo
 * ({@code importeN == importeN_original}); ver {@code docs/plan-sanitizacion-arancel-parcial-v2.md}.
 * Es idempotente: si el importe ya está parcial, el resultado no cambia.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SanitizeChequeraCuotaArancelUseCaseImpl implements SanitizeChequeraCuotaArancelUseCase {

    private static final BigDecimal PORCENTAJE_COMPLETO = new BigDecimal("100");

    private final ArancelPorcentajeService arancelPorcentajeService;
    private final LectivoCuotaService lectivoCuotaService;

    @Override
    public boolean sanitize(ChequeraCuota chequeraCuota, Integer serieArancelTipoId, Integer lectivoId) {
        if (chequeraCuota == null) {
            return false;
        }

        var arancelTipoId = chequeraCuota.getArancelTipoId() != null ? chequeraCuota.getArancelTipoId()
                : serieArancelTipoId;
        var porcentaje = resolvePorcentaje(arancelTipoId, chequeraCuota.getProductoId());

        diagnosticar(chequeraCuota, lectivoId);

        var importe1Anterior = chequeraCuota.getImporte1();
        var importe2Anterior = chequeraCuota.getImporte2();
        var importe3Anterior = chequeraCuota.getImporte3();

        chequeraCuota.aplicarPorcentajeArancel(porcentaje);

        var corregida = cambio(importe1Anterior, chequeraCuota.getImporte1())
                || cambio(importe2Anterior, chequeraCuota.getImporte2())
                || cambio(importe3Anterior, chequeraCuota.getImporte3());
        if (corregida) {
            log.warn("Sanitize - cuota {} corregida: arancelTipoId={} porcentaje={} importes=[{}, {}, {}] -> [{}, {}, {}]",
                    chequeraCuota.cuotaKey(), arancelTipoId, porcentaje,
                    importe1Anterior, importe2Anterior, importe3Anterior,
                    chequeraCuota.getImporte1(), chequeraCuota.getImporte2(), chequeraCuota.getImporte3());
        }

        return corregida;
    }

    private BigDecimal resolvePorcentaje(Integer arancelTipoId, Integer productoId) {
        if (arancelTipoId == null || productoId == null) {
            return PORCENTAJE_COMPLETO;
        }
        try {
            var arancelPorcentaje = arancelPorcentajeService.findByUnique(arancelTipoId, productoId);
            return arancelPorcentaje.getPorcentaje() != null ? arancelPorcentaje.getPorcentaje() : PORCENTAJE_COMPLETO;
        } catch (ArancelPorcentajeException e) {
            log.warn("Sanitize - inconsistencia: sin arancel_porcentaje para arancelTipoId={} productoId={} -> 100%",
                    arancelTipoId, productoId);
            return PORCENTAJE_COMPLETO;
        }
    }

    private void diagnosticar(ChequeraCuota chequeraCuota, Integer lectivoId) {
        if (lectivoId == null || chequeraCuota.getProductoId() == null || chequeraCuota.getCuotaId() == null) {
            return;
        }
        try {
            var diseno = lectivoCuotaService.findByUniqueKey(chequeraCuota.getFacultadId(),
                    lectivoId, chequeraCuota.getTipoChequeraId(), chequeraCuota.getProductoId(),
                    chequeraCuota.getAlternativaId(), chequeraCuota.getCuotaId());
            if (diseno == null) {
                return;
            }
            compararBase(chequeraCuota, 1, chequeraCuota.getImporte1Original(), diseno.getImporte1());
            compararBase(chequeraCuota, 2, chequeraCuota.getImporte2Original(), diseno.getImporte2());
            compararBase(chequeraCuota, 3, chequeraCuota.getImporte3Original(), diseno.getImporte3());
        } catch (LectivoCuotaException e) {
            log.warn("Sanitize - inconsistencia: no existe lectivo_cuota para cuota {}", chequeraCuota.cuotaKey());
        }
    }

    private void compararBase(ChequeraCuota chequeraCuota, int vencimiento, BigDecimal original, BigDecimal diseno) {
        if (original == null || diseno == null || original.compareTo(diseno) == 0) {
            return;
        }
        log.warn("Sanitize - inconsistencia de base cuota={} vencimiento={} original={} diseno(lectivo_cuota)={} diferencia={}",
                chequeraCuota.cuotaKey(), vencimiento, original, diseno, original.subtract(diseno));
    }

    private boolean cambio(BigDecimal anterior, BigDecimal actual) {
        if (anterior == null && actual == null) {
            return false;
        }
        if (anterior == null || actual == null) {
            return true;
        }
        return anterior.compareTo(actual) != 0;
    }

}

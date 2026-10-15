package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;

public interface SanitizeChequeraCuotaArancelUseCase {

    /**
     * Sanea los importes de una cuota aplicando el porcentaje de arancel correspondiente
     * ({@code importeN = importe{N}Original * porcentaje / 100}), modificando la cuota en memoria y emitiendo
     * además el diagnóstico de consistencia contra {@code lectivo_cuota}.
     *
     * @param chequeraCuota      cuota a sanear (se modifica en memoria)
     * @param serieArancelTipoId arancel tipo de la serie (fallback si la cuota no lo trae)
     * @param lectivoId          lectivo de la serie (para el cross-check con lectivo_cuota)
     * @return {@code true} si algún importe fue corregido (el cálculo no correspondía); {@code false} si ya estaba bien
     */
    boolean sanitize(ChequeraCuota chequeraCuota, Integer serieArancelTipoId, Integer lectivoId);

}

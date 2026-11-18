package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorImpresion;

public interface ObtenerBorradorImpresionUseCase {

    /**
     * Datos para imprimir el borrador. Sólo se permite sobre borradores {@code ABIERTO}: un borrador
     * emitido o descartado no se imprime como borrador.
     */
    BorradorImpresion obtener(Long borradorId);
}

package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Foto de un vínculo para el historial #404: solo los campos de la fila, sin las asociaciones. */
record UbicacionArticuloEstado(Long ubicacionArticuloId, Integer ubicacionId, Long articuloId, BigDecimal numeroCuenta) {

    static final String ENTIDAD = "ubicacion_articulo";

    static UbicacionArticuloEstado de(UbicacionArticulo vinculo) {
        var cuenta = vinculo.getNumeroCuenta();
        return new UbicacionArticuloEstado(vinculo.getUbicacionArticuloId(), vinculo.getUbicacionId(), vinculo.getArticuloId(),
                cuenta == null ? null : cuenta.setScale(0, RoundingMode.UNNECESSARY));
    }

    /** El par identifica al vínculo (índice único); el id autoincremental no lo conocen los consumidores. */
    String clave() {
        return ubicacionId + ":" + articuloId;
    }
}

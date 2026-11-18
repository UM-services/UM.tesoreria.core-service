package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.repository;

import java.math.BigDecimal;

/** Proyección cerrada de un movimiento contable: sólo lo que hace falta para sumar por cuenta. */
public interface MovimientoImporteProjection {

    BigDecimal getNumeroCuenta();

    Byte getDebita();

    BigDecimal getImporte();
}
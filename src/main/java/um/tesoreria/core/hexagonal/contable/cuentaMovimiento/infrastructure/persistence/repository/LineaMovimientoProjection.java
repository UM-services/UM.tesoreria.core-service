package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Proyección cerrada de una línea de `movcon`: sólo columnas propias, sin cargar las relaciones de la entidad. */
public interface LineaMovimientoProjection {

    Long getCuentaMovimientoId();

    OffsetDateTime getFechaContable();

    Integer getOrdenContable();

    Integer getItem();

    BigDecimal getNumeroCuenta();

    Byte getDebita();

    BigDecimal getImporte();

    String getConcepto();

    Integer getComprobanteId();

    Integer getProveedorId();

    Byte getApertura();
}

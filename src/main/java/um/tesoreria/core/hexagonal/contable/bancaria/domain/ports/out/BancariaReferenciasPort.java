package um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out;

import java.math.BigDecimal;

/** Existencia de lo que la cuenta referencia en otros slices (cuenta contable y proveedor). */
public interface BancariaReferenciasPort {
    boolean existeCuenta(BigDecimal numeroCuenta);

    boolean existeProveedor(Integer proveedorId);
}

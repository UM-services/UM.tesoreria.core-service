package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in.GetProveedorByIdUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaReferenciasPort;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentaByNumeroCuentaUseCase;

import java.math.BigDecimal;

/** Cruce con los slices de cuenta y de proveedor, solo por sus puertos in públicos. */
@Component
@RequiredArgsConstructor
public class BancariaReferenciasAdapter implements BancariaReferenciasPort {

    private final GetCuentaByNumeroCuentaUseCase cuentas;
    private final GetProveedorByIdUseCase proveedores;

    @Override
    public boolean existeCuenta(BigDecimal numeroCuenta) {
        return cuentas.getCuentaByNumeroCuenta(numeroCuenta).isPresent();
    }

    @Override
    public boolean existeProveedor(Integer proveedorId) {
        return proveedores.getProveedorById(proveedorId).isPresent();
    }
}

package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in.GetProveedorByIdUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentaByNumeroCuentaUseCase;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BancariaReferenciasAdapterTest {
    GetCuentaByNumeroCuentaUseCase cuentas = mock(GetCuentaByNumeroCuentaUseCase.class);
    GetProveedorByIdUseCase proveedores = mock(GetProveedorByIdUseCase.class);
    BancariaReferenciasAdapter adapter = new BancariaReferenciasAdapter(cuentas, proveedores);

    @Test
    void laCuentaExisteSoloSiElPlanDeCuentasLaDevuelve() {
        when(cuentas.getCuentaByNumeroCuenta(BigDecimal.ONE)).thenReturn(Optional.of(new Cuenta()));
        when(cuentas.getCuentaByNumeroCuenta(BigDecimal.TEN)).thenReturn(Optional.empty());
        assertThat(adapter.existeCuenta(BigDecimal.ONE)).isTrue();
        assertThat(adapter.existeCuenta(BigDecimal.TEN)).isFalse();
    }

    @Test
    void elProveedorExisteSoloSiElSliceLoDevuelve() {
        when(proveedores.getProveedorById(1)).thenReturn(Optional.of(new Proveedor()));
        when(proveedores.getProveedorById(2)).thenReturn(Optional.empty());
        assertThat(adapter.existeProveedor(1)).isTrue();
        assertThat(adapter.existeProveedor(2)).isFalse();
    }
}

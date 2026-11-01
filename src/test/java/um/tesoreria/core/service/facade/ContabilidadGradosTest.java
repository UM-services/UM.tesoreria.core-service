package um.tesoreria.core.service.facade;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import static org.mockito.Mockito.*;

class ContabilidadGradosTest {
    @Test
    void balanceDelegaRecalculoYConservaLimpiezaSinMergeDeCuentas() {
        CuentaService cuentas = mock(CuentaService.class);
        ContabilidadService contabilidad = new ContabilidadService(null, cuentas, null, null, null, null, null, null);
        contabilidad.recalculateGrados();
        var orden = inOrder(cuentas);
        orden.verify(cuentas).recalculaGrados();
        orden.verify(cuentas).limpiarNombres();
        verifyNoMoreInteractions(cuentas);
    }
}

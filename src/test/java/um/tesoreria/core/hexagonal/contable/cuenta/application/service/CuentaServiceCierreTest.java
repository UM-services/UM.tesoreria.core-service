package um.tesoreria.core.hexagonal.contable.cuenta.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.CreateCuentaUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.DeleteCuentaUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetAllCuentasUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentaByCuentaContableIdUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentaByNumeroCuentaUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentasImputablesUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.RecalculaGradosUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.RecalcularGradosYNombresUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.SaveAllCuentasUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.SearchCuentasUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.UpdateCuentaUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * El cierre y el balance tienen que ver el mismo grado 5: las dos selecciones del cierre salen de las cuentas
 * imputables (grado calculado desde el número) y no de la columna guardada.
 */
@ExtendWith(MockitoExtension.class)
class CuentaServiceCierreTest {

    @Mock CreateCuentaUseCase createCuentaUseCase;
    @Mock UpdateCuentaUseCase updateCuentaUseCase;
    @Mock DeleteCuentaUseCase deleteCuentaUseCase;
    @Mock GetAllCuentasUseCase getAllCuentasUseCase;
    @Mock GetCuentaByNumeroCuentaUseCase getCuentaByNumeroCuentaUseCase;
    @Mock GetCuentaByCuentaContableIdUseCase getCuentaByCuentaContableIdUseCase;
    @Mock SearchCuentasUseCase searchCuentasUseCase;
    @Mock RecalculaGradosUseCase recalculaGradosUseCase;
    @Mock RecalcularGradosYNombresUseCase recalcularGradosYNombresUseCase;
    @Mock GetCuentasImputablesUseCase getCuentasImputablesUseCase;
    @Mock SaveAllCuentasUseCase saveAllCuentasUseCase;
    @Mock CuentaRepository repositoryPort;

    CuentaService service;

    @BeforeEach
    void setUp() {
        service = new CuentaService(createCuentaUseCase, updateCuentaUseCase, deleteCuentaUseCase,
                getAllCuentasUseCase, getCuentaByNumeroCuentaUseCase, getCuentaByCuentaContableIdUseCase,
                searchCuentasUseCase, recalculaGradosUseCase, recalcularGradosYNombresUseCase,
                getCuentasImputablesUseCase, saveAllCuentasUseCase, repositoryPort);
    }

    static Cuenta cuenta(String numero, Integer gradoGuardado) {
        return Cuenta.builder().numeroCuenta(new BigDecimal(numero)).nombre("Cuenta " + numero)
                .grado(gradoGuardado).build();
    }

    @Test
    void cierreDeResultadoYActivoPasivoSalenDelGradoCalculado() {
        // El grado guardado (2 y 3) está desactualizado: la selección no debe mirarlo.
        when(getCuentasImputablesUseCase.getCuentasImputables()).thenReturn(List.of(
                cuenta("11010100001", 2), cuenta("21010100001", 3), cuenta("31010100001", 4),
                cuenta("41010100001", 4), cuenta("49010100001", 4)));

        assertThat(service.findAllByCierreResultado()).extracting(Cuenta::getNumeroCuenta)
                .extracting(BigDecimal::toPlainString)
                .containsExactly("31010100001", "41010100001", "49010100001");
        assertThat(service.findAllByCierreActivoPasivo()).extracting(Cuenta::getNumeroCuenta)
                .extracting(BigDecimal::toPlainString)
                .containsExactly("11010100001", "21010100001");
        verify(repositoryPort, org.mockito.Mockito.never()).findAllConNumeroPositivo();
    }
}

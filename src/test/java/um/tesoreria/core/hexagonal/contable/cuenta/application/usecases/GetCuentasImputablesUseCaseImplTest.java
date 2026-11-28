package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCuentasImputablesUseCaseImplTest {
    @Mock CuentaRepository repository;
    @InjectMocks GetCuentasImputablesUseCaseImpl useCase;

    // protects=el grado sale del número, no de la columna guardada, y el orden del repositorio se conserva; fails_when=se filtra por el grado guardado o se reordena; no_claim=no prueba la consulta SQL
    @Test
    void elGradoSeCalculaDesdeElNumeroAunqueElGuardadoEsteDesactualizado() {
        when(repository.findAllConNumeroPositivo()).thenReturn(List.of(
                cuenta("11010100001", "Caja", 4),
                cuenta("11010100000", "Integradora con grado 5 guardado", 5),
                cuenta("21101010001", "Proveedores", null)));

        List<Cuenta> cuentas = useCase.getCuentasImputables();

        assertThat(cuentas).extracting(Cuenta::getNombre).containsExactly("Caja", "Proveedores");
        Cuenta caja = cuentas.get(0);
        assertThat(caja.getGrado()).isEqualTo(5);
        assertThat(caja.getGrado1()).isEqualByComparingTo("10000000000");
        assertThat(caja.getGrado2()).isEqualByComparingTo("11000000000");
        assertThat(caja.getGrado3()).isEqualByComparingTo("11010000000");
        assertThat(caja.getGrado4()).isEqualByComparingTo("11010100000");
    }

    // protects=un número fuera de la estructura conserva el grado guardado, igual que recalcularGrados; fails_when=la cuenta legada desaparece o corta la consulta con una excepción; no_claim=no valida qué números son legados
    @Test
    void unNumeroFueraDeLaEstructuraUsaElGradoGuardado() {
        when(repository.findAllConNumeroPositivo()).thenReturn(List.of(
                cuenta("123", "Legada imputable", 5),
                cuenta("11010100001.5", "Legada integradora", 3)));

        assertThat(useCase.getCuentasImputables()).extracting(Cuenta::getNombre).containsExactly("Legada imputable");
    }

    // protects=el \r\n se quita sólo del objeto devuelto y no se escribe nada; fails_when=queda el salto o se llama a limpiarNombres/recalcularGrados; no_claim=no prueba otros caracteres de control
    @Test
    void quitaSaltosDeLineaDelNombreSinEscribir() {
        when(repository.findAllConNumeroPositivo()).thenReturn(List.of(
                cuenta("11010100001", "Caja\r\n chica\r\n", 5), cuenta("11010100002", null, 5)));

        assertThat(useCase.getCuentasImputables()).extracting(Cuenta::getNombre).containsExactly("Caja chica", null);
        verify(repository).findAllConNumeroPositivo();
        verifyNoMoreInteractions(repository);
    }

    private static Cuenta cuenta(String numero, String nombre, Integer grado) {
        return Cuenta.builder().numeroCuenta(new BigDecimal(numero)).nombre(nombre).grado(grado).build();
    }
}

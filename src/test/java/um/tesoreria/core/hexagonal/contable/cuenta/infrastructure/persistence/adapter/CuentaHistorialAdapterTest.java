package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.adapter;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CuentaHistorialAdapterTest {
    @Test
    void usaClaveCanonicaYCopiasPlanasConValoresNulos() {
        var puerto = mock(RegistrarEscrituraHistorialUseCase.class);
        var adapter = new CuentaHistorialAdapter(puerto);
        var anterior = Cuenta.builder().numeroCuenta(new BigDecimal("11010100000.00")).nombre("Antes").build();
        var nueva = Cuenta.builder().numeroCuenta(new BigDecimal("11010100000")).nombre("Después").build();
        adapter.registrarAlta(anterior);
        adapter.registrarEdicion(anterior, nueva);
        adapter.registrarBaja(nueva);
        ArgumentCaptor<Object> estado = ArgumentCaptor.forClass(Object.class);
        verify(puerto).registrarAlta(eq("cuenta"), eq("11010100000"), estado.capture());
        anterior.setNombre("Mutado");
        assertThat(((Map<?, ?>) estado.getValue()).containsKey("geograficaId")).isTrue();
        assertThat(((Map<?, ?>) estado.getValue()).get("nombre")).isEqualTo("Antes");
        verify(puerto).registrarEdicion(eq("cuenta"), eq("11010100000"), any(Map.class), any(Map.class));
        verify(puerto).registrarBaja(eq("cuenta"), eq("11010100000"), any(Map.class));
    }
}

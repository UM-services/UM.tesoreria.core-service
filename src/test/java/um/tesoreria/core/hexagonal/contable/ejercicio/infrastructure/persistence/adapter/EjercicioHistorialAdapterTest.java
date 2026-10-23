package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EjercicioHistorialAdapterTest {
    @Test
    void usaEntidadEjercicioClaveIdYEstadoPlano() {
        var puerto = mock(RegistrarEscrituraHistorialUseCase.class);
        var adapter = new EjercicioHistorialAdapter(puerto);
        var ejercicio = Ejercicio.builder().ejercicioId(16).nombre("Ejercicio Económico 2027")
                .fechaInicio(LocalDate.parse("2027-03-01")).fechaFinal(LocalDate.parse("2028-02-29")).bloqueado((byte) 0).build();
        adapter.registrarAlta(ejercicio);
        adapter.registrarEdicion(ejercicio, ejercicio.toBuilder().bloqueado((byte) 1).build());
        adapter.registrarBaja(ejercicio);
        ArgumentCaptor<Object> estado = ArgumentCaptor.forClass(Object.class);
        verify(puerto).registrarAlta(eq("ejercicio"), eq("16"), estado.capture());
        Map<?, ?> mapa = (Map<?, ?>) estado.getValue();
        assertThat(mapa.get("eje_id")).isEqualTo(16);
        assertThat(mapa.get("fechaFinal")).isEqualTo(LocalDate.parse("2028-02-29"));
        assertThat(mapa.containsKey("ordenContableResultado")).isTrue();
        ArgumentCaptor<Object> antes = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<Object> despues = ArgumentCaptor.forClass(Object.class);
        verify(puerto).registrarEdicion(eq("ejercicio"), eq("16"), antes.capture(), despues.capture());
        assertThat(((Map<?, ?>) antes.getValue()).get("bloqueado")).isEqualTo((byte) 0);
        assertThat(((Map<?, ?>) despues.getValue()).get("bloqueado")).isEqualTo((byte) 1);
        ArgumentCaptor<Object> baja = ArgumentCaptor.forClass(Object.class);
        verify(puerto).registrarBaja(eq("ejercicio"), eq("16"), baja.capture());
        assertThat(((Map<?, ?>) baja.getValue()).get("eje_id")).isEqualTo(16);
    }
}

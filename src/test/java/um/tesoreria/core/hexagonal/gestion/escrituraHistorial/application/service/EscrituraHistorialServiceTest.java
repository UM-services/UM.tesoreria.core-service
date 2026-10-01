package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraHistorial;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraOperacion;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out.EscrituraHistorialRepository;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization.JacksonEscrituraValorSerializer;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EscrituraHistorialServiceTest {

    @Mock
    private EscrituraHistorialRepository repository;

    private EscrituraHistorialService service;

    @BeforeEach
    void setUp() {
        service = new EscrituraHistorialService(repository, new JacksonEscrituraValorSerializer());
        lenient().when(repository.save(any())).thenAnswer(invocation -> {
            EscrituraHistorial h = invocation.getArgument(0);
            h.setEscrituraHistorialId(1L);
            return h;
        });
    }

    @Test
    void registrarAlta_dejaAnteriorVacioYPersisteNuevo() {
        Map<String, Object> nuevo = new LinkedHashMap<>();
        nuevo.put("nombre", "A");

        var result = service.registrarAlta("ejercicio", "10", nuevo);

        ArgumentCaptor<EscrituraHistorial> captor = ArgumentCaptor.forClass(EscrituraHistorial.class);
        verify(repository).save(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.getOperacion()).isEqualTo(EscrituraOperacion.ALTA);
        assertThat(saved.getEntidad()).isEqualTo("ejercicio");
        assertThat(saved.getEntidadClave()).isEqualTo("10");
        assertThat(saved.getValorAnterior()).isEmpty();
        assertThat(saved.getValorNuevo()).isEqualTo("{\"nombre\":\"A\"}");
        assertThat(saved.getFecha()).isNotNull();
        assertThat(result.getEscrituraHistorialId()).isEqualTo(1L);
        assertThat(result.resumenSeguro()).contains("ejercicio", "10", "ALTA").doesNotContain("\"nombre\"");
    }

    @Test
    void registrarEdicion_conservaAntesYDespues() {
        Map<String, Object> antes = new LinkedHashMap<>();
        antes.put("n", "old");
        Map<String, Object> despues = new LinkedHashMap<>();
        despues.put("n", "new");

        service.registrarEdicion("proveedor", "7", antes, despues);

        ArgumentCaptor<EscrituraHistorial> captor = ArgumentCaptor.forClass(EscrituraHistorial.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getOperacion()).isEqualTo(EscrituraOperacion.EDICION);
        assertThat(captor.getValue().getValorAnterior()).isEqualTo("{\"n\":\"old\"}");
        assertThat(captor.getValue().getValorNuevo()).isEqualTo("{\"n\":\"new\"}");
    }

    @Test
    void registrarBaja_conservaPrevioYDejaNuevoVacio() {
        Map<String, Object> previo = new LinkedHashMap<>();
        previo.put("activo", true);

        service.registrarBaja("bancaria", "3", previo);

        ArgumentCaptor<EscrituraHistorial> captor = ArgumentCaptor.forClass(EscrituraHistorial.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getOperacion()).isEqualTo(EscrituraOperacion.BAJA);
        assertThat(captor.getValue().getValorAnterior()).isEqualTo("{\"activo\":true}");
        assertThat(captor.getValue().getValorNuevo()).isEmpty();
    }

    @Test
    void rechazaEntidadOClaveVacias() {
        assertThatThrownBy(() -> service.registrarAlta(" ", "1", Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.registrarAlta("cuenta", " ", Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

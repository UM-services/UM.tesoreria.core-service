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
import static org.mockito.Mockito.never;
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
    void registrarAlta_dejaAnteriorNullYPersisteNuevo() {
        Map<String, Object> nuevo = new LinkedHashMap<>();
        nuevo.put("nombre", "A");

        var result = service.registrarAlta("ejercicio", "clave-secreta-123", nuevo);

        ArgumentCaptor<EscrituraHistorial> captor = ArgumentCaptor.forClass(EscrituraHistorial.class);
        verify(repository).save(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.getOperacion()).isEqualTo(EscrituraOperacion.ALTA);
        assertThat(saved.getEntidad()).isEqualTo("ejercicio");
        assertThat(saved.getEntidadClave()).isEqualTo("clave-secreta-123");
        assertThat(saved.getValorAnterior()).isNull();
        assertThat(saved.getValorNuevo()).isEqualTo("{\"nombre\":\"A\"}");
        assertThat(saved.getFecha()).as("la asigna la base").isNull();
        assertThat(result.getEscrituraHistorialId()).isEqualTo(1L);
        assertThat(result.resumenSeguro()).contains("ejercicio", "ALTA")
                .doesNotContain("clave-secreta-123", "\"nombre\"");
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
    void registrarBaja_conservaPrevioYDejaNuevoNull() {
        Map<String, Object> previo = new LinkedHashMap<>();
        previo.put("activo", true);

        service.registrarBaja("bancaria", "3", previo);

        ArgumentCaptor<EscrituraHistorial> captor = ArgumentCaptor.forClass(EscrituraHistorial.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getOperacion()).isEqualTo(EscrituraOperacion.BAJA);
        assertThat(captor.getValue().getValorAnterior()).isEqualTo("{\"activo\":true}");
        assertThat(captor.getValue().getValorNuevo()).isNull();
    }

    @Test
    void rechazaEntidadOClaveVacias() {
        // Nulas o en blanco se rechazan antes de tocar el repositorio
        assertThatThrownBy(() -> service.registrarAlta(null, "1", Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.registrarEdicion("cuenta", null, Map.of(), Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.registrarAlta(" ", "1", Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.registrarAlta("cuenta", " ", Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
        // String.valueOf(null) de un id sin asignar
        assertThatThrownBy(() -> service.registrarAlta("cuenta", "null", Map.of("n", 1)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void normalizaEntidadYClaveConTrim() {
        // " 3 " tiene que quedar ligado al mismo historial que "3"
        service.registrarBaja("  bancaria ", " 3 ", Map.of("activo", true));

        ArgumentCaptor<EscrituraHistorial> captor = ArgumentCaptor.forClass(EscrituraHistorial.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEntidad()).isEqualTo("bancaria");
        assertThat(captor.getValue().getEntidadClave()).isEqualTo("3");
    }

    @Test
    void rechazaEntidadOClaveMasLargasQueLaColumna() {
        // Sin depender del sql_mode de MySQL: no se trunca ni falla recién en el flush
        assertThatThrownBy(() -> service.registrarAlta("e".repeat(129), "1", Map.of("n", 1)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.registrarAlta("cuenta", "k".repeat(256), Map.of("n", 1)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(repository, never()).save(any());

        service.registrarAlta("e".repeat(128), " " + "k".repeat(255) + " ", Map.of("n", 1));
        verify(repository).save(any());
    }

    @Test
    void exigeLosEstadosDeCadaOperacion() {
        assertThatThrownBy(() -> service.registrarAlta("cuenta", "1", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.registrarEdicion("cuenta", "1", null, Map.of("n", 1)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.registrarEdicion("cuenta", "1", Map.of("n", 1), null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.registrarBaja("cuenta", "1", null))
                .isInstanceOf(IllegalArgumentException.class);
        verify(repository, never()).save(any());
    }
}

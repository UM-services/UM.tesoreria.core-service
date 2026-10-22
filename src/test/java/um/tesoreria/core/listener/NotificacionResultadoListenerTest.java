package um.tesoreria.core.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import um.tesoreria.core.event.NotificacionResultadoEvent;
import um.tesoreria.core.event.ReferenciaNotificacion;
import um.tesoreria.core.model.PersonaSuspendido;
import um.tesoreria.core.service.PersonaSuspendidoService;

class NotificacionResultadoListenerTest {

    private PersonaSuspendidoService personaSuspendidoService;
    private NotificacionResultadoListener listener;

    @BeforeEach
    void setUp() {
        personaSuspendidoService = mock(PersonaSuspendidoService.class);
        listener = new NotificacionResultadoListener(personaSuspendidoService);
    }

    @Test
    void marcaNotificadoSoloCuandoElEnvioFueExitoso() {
        var personaSuspendido = new PersonaSuspendido(7L, BigDecimal.ONE, 1, 2, 3, (byte) 0);
        when(personaSuspendidoService.findByPersonaSuspendidoId(7L)).thenReturn(personaSuspendido);

        listener.handleResultado(resultado(true, "7"));

        ArgumentCaptor<PersonaSuspendido> captor = ArgumentCaptor.forClass(PersonaSuspendido.class);
        verify(personaSuspendidoService).update(captor.capture(), org.mockito.ArgumentMatchers.eq(7L));
        assertThat(captor.getValue().getNotificado()).isEqualTo((byte) 1);
    }

    @Test
    void noMarcaNadaSiElEnvioFallo() {
        listener.handleResultado(resultado(false, "7"));

        verify(personaSuspendidoService, never()).update(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void ignoraReferenciasQueNoSonDePersonaSuspendido() {
        var evento = resultado(true, "7");
        evento.getReferencia().setTipo("PROVEEDOR_MOVIMIENTO");

        listener.handleResultado(evento);

        verify(personaSuspendidoService, never()).findByPersonaSuspendidoId(org.mockito.ArgumentMatchers.anyLong());
    }

    private NotificacionResultadoEvent resultado(boolean enviado, String id) {
        return NotificacionResultadoEvent.builder()
                .eventId("evt-1")
                .occurredAt(OffsetDateTime.now())
                .enviado(enviado)
                .referencia(ReferenciaNotificacion.builder().tipo("PERSONA_SUSPENDIDO").id(id).build())
                .build();
    }

}

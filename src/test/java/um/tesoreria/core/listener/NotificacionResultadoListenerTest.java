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
import um.tesoreria.core.hexagonal.chequera.envio.application.service.EnvioJobService;
import um.tesoreria.core.model.PersonaSuspendido;
import um.tesoreria.core.service.PersonaSuspendidoService;

class NotificacionResultadoListenerTest {

    private PersonaSuspendidoService personaSuspendidoService;
    private EnvioJobService envioJobService;
    private NotificacionResultadoListener listener;

    @BeforeEach
    void setUp() {
        personaSuspendidoService = mock(PersonaSuspendidoService.class);
        envioJobService = mock(EnvioJobService.class);
        listener = new NotificacionResultadoListener(personaSuspendidoService, envioJobService);
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

    @Test
    void cierraLaTareaDelEnvioMasivoCuandoLaReferenciaEsDeChequera() {
        var evento = resultado(true, "342038");
        evento.getReferencia().setTipo("CHEQUERA");
        when(envioJobService.registrarAcuse(342038L, true, null)).thenReturn(true);

        listener.handleResultado(evento);

        verify(envioJobService).registrarAcuse(342038L, true, null);
        verify(personaSuspendidoService, never()).findByPersonaSuspendidoId(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void informaElFalloCuandoElEnvioMasivoNoSalio() {
        var evento = resultado(false, "342038");
        evento.getReferencia().setTipo("CHEQUERA");
        evento.setError("SMTP timeout");

        listener.handleResultado(evento);

        verify(envioJobService).registrarAcuse(342038L, false, "SMTP timeout");
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

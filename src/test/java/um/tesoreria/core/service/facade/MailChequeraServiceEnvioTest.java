package um.tesoreria.core.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.event.SendChequeraEvent;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.envio.application.service.EnvioSolicitudService;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitud;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudCreacion;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudEstado;
import um.tesoreria.core.service.CorreoEventPublisher;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailChequeraServiceEnvioTest {

    private static final UUID SOLICITUD_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock
    private ChequeraSerieService chequeraSerieService;
    @Mock
    private EnvioSolicitudService envioSolicitudService;
    @Mock
    private CorreoEventPublisher correoEventPublisher;

    @InjectMocks
    private MailChequeraService service;

    @Test
    void publicaElEventoConLaSolicitudYLaClaveDeLaChequera() {
        var serie = serie();
        when(chequeraSerieService.findByUnique(15, 400, 1567L)).thenReturn(serie);
        when(envioSolicitudService.crear(serie, 7L, "JOB"))
                .thenReturn(new EnvioSolicitudCreacion(solicitud(EnvioSolicitudEstado.PENDIENTE), true));

        String mensaje = service.sendChequera(15, 400, 1567L, 1, false, true, false, true, true, 7L);

        assertThat(mensaje).isEqualTo(MailChequeraService.MENSAJE_ENVIO);
        var captor = ArgumentCaptor.forClass(SendChequeraEvent.class);
        verify(correoEventPublisher).publicarChequera(captor.capture());
        assertThat(captor.getValue().getSolicitudId()).isEqualTo(SOLICITUD_ID.toString());
        assertThat(captor.getValue().getChequeraId()).isEqualTo(331541L);
        assertThat(captor.getValue().getJobId()).isEqualTo(7L);
    }

    @Test
    void noPublicaSiLaChequeraYaFueEnviada() {
        var serie = serie();
        when(chequeraSerieService.findByUnique(15, 400, 1567L)).thenReturn(serie);
        when(envioSolicitudService.crear(serie, null, "MANUAL"))
                .thenReturn(new EnvioSolicitudCreacion(solicitud(EnvioSolicitudEstado.ENVIADO), false));

        String mensaje = service.sendChequera(15, 400, 1567L, 1, false, true, false);

        assertThat(mensaje).isEqualTo(MailChequeraService.MENSAJE_YA_ENVIADA);
        verifyNoInteractions(correoEventPublisher);
    }

    private static ChequeraSerie serie() {
        return ChequeraSerie.builder()
                .chequeraId(331541L)
                .facultadId(15)
                .tipoChequeraId(400)
                .chequeraSerieId(1567L)
                .lectivoId(37)
                .build();
    }

    private static EnvioSolicitud solicitud(EnvioSolicitudEstado estado) {
        return EnvioSolicitud.builder()
                .solicitudId(SOLICITUD_ID)
                .chequeraId(331541L)
                .facultadId(15)
                .tipoChequeraId(400)
                .chequeraSerieId(1567L)
                .estado(estado)
                .build();
    }

}

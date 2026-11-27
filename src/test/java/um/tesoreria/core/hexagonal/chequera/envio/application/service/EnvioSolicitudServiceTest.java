package um.tesoreria.core.hexagonal.chequera.envio.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitud;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioTareaEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.EnvioSolicitudRepository;
import um.tesoreria.core.model.ChequeraMessageCheck;
import um.tesoreria.core.service.ChequeraMessageCheckService;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvioSolicitudServiceTest {

    private static final UUID SOLICITUD_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock
    private EnvioSolicitudRepository repository;
    @Mock
    private ChequeraSerieService chequeraSerieService;
    @Mock
    private ChequeraMessageCheckService chequeraMessageCheckService;
    @Mock
    private EnvioJobService envioJobService;

    @InjectMocks
    private EnvioSolicitudService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "maxIntentos", 3);
        ReflectionTestUtils.setField(service, "lockSeconds", 60L);
    }

    @Test
    void creaPendienteCuandoLaSerieNoFueEnviada() {
        when(chequeraSerieService.findByUnique(15, 400, 1567L)).thenReturn(serie((byte) 0));
        when(repository.findByChequeraId(331541L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var creacion = service.crear(15, 400, 1567L, 7L, "JOB");

        assertThat(creacion.publicar()).isTrue();
        assertThat(creacion.solicitud().getEstado()).isEqualTo(EnvioSolicitudEstado.PENDIENTE);
        assertThat(creacion.solicitud().getChequeraId()).isEqualTo(331541L);
        assertThat(creacion.solicitud().getJobId()).isEqualTo(7L);
        assertThat(creacion.solicitud().getDedupeKey()).isEqualTo("CHEQUERA:331541");
        verify(repository).save(any());
    }

    @Test
    void creaEnviadaCuandoLaSerieYaTieneElFlag() {
        when(chequeraSerieService.findByUnique(15, 400, 1567L)).thenReturn(serie((byte) 1));
        when(repository.findByChequeraId(331541L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var creacion = service.crear(15, 400, 1567L, null, "MANUAL");

        assertThat(creacion.publicar()).isFalse();
        assertThat(creacion.solicitud().getEstado()).isEqualTo(EnvioSolicitudEstado.ENVIADO);
        assertThat(creacion.solicitud().getEnviadoAt()).isNotNull();
    }

    @Test
    void noRepublicaUnaSolicitudYaEnviada() {
        var existente = solicitud(EnvioSolicitudEstado.ENVIADO, 1, null);
        when(chequeraSerieService.findByUnique(15, 400, 1567L)).thenReturn(serie((byte) 0));
        when(repository.findByChequeraId(331541L)).thenReturn(Optional.of(existente));

        var creacion = service.crear(15, 400, 1567L, null, "MANUAL");

        assertThat(creacion.publicar()).isFalse();
        assertThat(creacion.solicitud().getEstado()).isEqualTo(EnvioSolicitudEstado.ENVIADO);
        verify(repository, never()).save(any());
    }

    @Test
    void reclamaUnaSolicitudPendiente() {
        when(repository.findBySolicitudIdForUpdate(SOLICITUD_ID))
                .thenReturn(Optional.of(solicitud(EnvioSolicitudEstado.PENDIENTE, 0, null)));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var claim = service.reclamar(SOLICITUD_ID);

        assertThat(claim.puedeEnviar()).isTrue();
        assertThat(claim.solicitud().getEstado()).isEqualTo(EnvioSolicitudEstado.ENVIANDO);
        assertThat(claim.solicitud().getIntentos()).isEqualTo(1);
        assertThat(claim.solicitud().getLockHasta()).isAfter(OffsetDateTime.now(ZoneOffset.UTC));
    }

    @Test
    void noReclamaUnaSolicitudYaEnviada() {
        when(repository.findBySolicitudIdForUpdate(SOLICITUD_ID))
                .thenReturn(Optional.of(solicitud(EnvioSolicitudEstado.ENVIADO, 1, null)));

        var claim = service.reclamar(SOLICITUD_ID);

        assertThat(claim.puedeEnviar()).isFalse();
        assertThat(claim.motivo()).isEqualTo("YA_ENVIADA");
        verify(repository, never()).save(any());
    }

    @Test
    void noReclamaMientrasElLockEstaVigentePeroSiCuandoVencio() {
        when(repository.findBySolicitudIdForUpdate(SOLICITUD_ID))
                .thenReturn(Optional.of(solicitud(EnvioSolicitudEstado.ENVIANDO, 1,
                        OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(1))));
        assertThat(service.reclamar(SOLICITUD_ID).puedeEnviar()).isFalse();

        when(repository.findBySolicitudIdForUpdate(SOLICITUD_ID))
                .thenReturn(Optional.of(solicitud(EnvioSolicitudEstado.ENVIANDO, 1,
                        OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(1))));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        assertThat(service.reclamar(SOLICITUD_ID).puedeEnviar()).isTrue();
    }

    @Test
    void noReclamaSiAgotoLosIntentos() {
        when(repository.findBySolicitudIdForUpdate(SOLICITUD_ID))
                .thenReturn(Optional.of(solicitud(EnvioSolicitudEstado.FALLIDO, 3, null)));

        var claim = service.reclamar(SOLICITUD_ID);

        assertThat(claim.puedeEnviar()).isFalse();
        assertThat(claim.motivo()).isEqualTo("MAX_INTENTOS");
    }

    @Test
    void confirmarElEnvioCierraLaSerieElCheckYLaTarea() {
        when(repository.findBySolicitudIdForUpdate(SOLICITUD_ID))
                .thenReturn(Optional.of(solicitud(EnvioSolicitudEstado.ENVIANDO, 1, null)));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraMessageCheckService.add(any(ChequeraMessageCheck.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.marcarEnviado(SOLICITUD_ID);

        verify(chequeraSerieService).markSent(15, 400, 1567L);
        verify(chequeraMessageCheckService).add(any(ChequeraMessageCheck.class));
        verify(envioJobService).registrarAcuse(7L, 331541L, EnvioTareaEstado.ENVIADA, true, null);
    }

    @Test
    void confirmarDosVecesNoDuplicaEfectos() {
        when(repository.findBySolicitudIdForUpdate(SOLICITUD_ID))
                .thenReturn(Optional.of(solicitud(EnvioSolicitudEstado.ENVIADO, 1, null)));

        service.marcarEnviado(SOLICITUD_ID);

        verifyNoInteractions(chequeraSerieService, chequeraMessageCheckService, envioJobService);
    }

    @Test
    void fallarNoCierraLaTareaHastaAgotarLosIntentos() {
        when(repository.findBySolicitudIdForUpdate(SOLICITUD_ID))
                .thenReturn(Optional.of(solicitud(EnvioSolicitudEstado.ENVIANDO, 1, null)));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.marcarFallido(SOLICITUD_ID, "SMTP caído");

        verify(envioJobService, never()).registrarAcuse(anyLong(), anyLong(), any(), anyBoolean(), any());
    }

    @Test
    void fallarEnElUltimoIntentoCierraLaTareaComoError() {
        when(repository.findBySolicitudIdForUpdate(SOLICITUD_ID))
                .thenReturn(Optional.of(solicitud(EnvioSolicitudEstado.ENVIANDO, 3, null)));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.marcarFallido(SOLICITUD_ID, "SMTP caído");

        verify(envioJobService).registrarAcuse(7L, 331541L, EnvioTareaEstado.ERROR_SMTP, false,
                "SMTP caído");
    }

    @Test
    void omitirPorSinCuotasCierraLaTareaConElEstadoLegacy() {
        when(repository.findBySolicitudIdForUpdate(SOLICITUD_ID))
                .thenReturn(Optional.of(solicitud(EnvioSolicitudEstado.ENVIANDO, 1, null)));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.marcarOmitida(SOLICITUD_ID, EnvioSolicitudEstado.SIN_CUOTAS, "Sin cuotas para enviar");

        verify(envioJobService).registrarAcuse(7L, 331541L, EnvioTareaEstado.ERROR_SMTP, false,
                "Sin cuotas para enviar");
    }

    @Test
    void omitirPorSinCorreosCierraLaTareaComoOmitida() {
        when(repository.findBySolicitudIdForUpdate(SOLICITUD_ID))
                .thenReturn(Optional.of(solicitud(EnvioSolicitudEstado.ENVIANDO, 1, null)));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.marcarOmitida(SOLICITUD_ID, EnvioSolicitudEstado.SIN_CORREOS, "Sin destinatarios");

        verify(envioJobService).registrarAcuse(7L, 331541L, EnvioTareaEstado.SIN_CORREOS, false,
                "Sin destinatarios");
    }

    private static ChequeraSerie serie(byte enviado) {
        return ChequeraSerie.builder()
                .chequeraId(331541L)
                .facultadId(15)
                .tipoChequeraId(400)
                .chequeraSerieId(1567L)
                .lectivoId(37)
                .enviado(enviado)
                .build();
    }

    private static EnvioSolicitud solicitud(EnvioSolicitudEstado estado, int intentos,
                                            OffsetDateTime lockHasta) {
        return EnvioSolicitud.builder()
                .solicitudId(SOLICITUD_ID)
                .chequeraId(331541L)
                .facultadId(15)
                .tipoChequeraId(400)
                .chequeraSerieId(1567L)
                .lectivoId(37)
                .jobId(7L)
                .origen("JOB")
                .dedupeKey("CHEQUERA:331541")
                .estado(estado)
                .intentos(intentos)
                .lockHasta(lockHasta)
                .build();
    }

}

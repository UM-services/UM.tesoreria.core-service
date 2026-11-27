package um.tesoreria.core.hexagonal.chequera.envio.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.DeudaEnvio;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobTarea;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioOperacion;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioTareaEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.ChequeraEnvioAltaReader;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.EnvioChequeraGateway;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvioChequerasServiceTest {

    private static final String RESPUESTA_ENVIO = "Solicitud de envío en proceso";

    @Mock
    private ChequeraEnvioAltaReader reader;
    @Mock
    private EnvioChequeraGateway gateway;
    @InjectMocks
    private EnvioChequerasService service;

    private static EnvioJob job() {
        return EnvioJob.builder()
                .jobId(1L).operacion(EnvioOperacion.MAILING).lectivoId(37).facultadId(2).geograficaId(1)
                .codigoBarras(true).umbralCuotasDeuda(3).build();
    }

    private static EnvioJobTarea tarea() {
        return EnvioJobTarea.builder()
                .jobId(1L).chequeraId(342038L).facultadId(2).tipoChequeraId(520).chequeraSerieId(446L)
                .personaId(new BigDecimal("1001")).documentoId(1)
                .estado(EnvioTareaEstado.PENDIENTE).build();
    }

    private static ChequeraEnvioAlta alta(boolean personalValido, boolean institucionalValido) {
        return alta(personalValido, institucionalValido, false);
    }

    private static ChequeraEnvioAlta alta(boolean personalValido, boolean institucionalValido, boolean enviado) {
        return new ChequeraEnvioAlta(342038L, 2, 520, 446L, 1, new BigDecimal("1001"), 1, 37, 1,
                "ABALLAY MURUA", "Luna Martina", "Matrícula y Arancel", "Ciclo Completo",
                "aballayluna@gmail.com", "", personalValido, institucionalValido, enviado, false);
    }

    private static DeudaEnvio deuda(int lectivoId, int cuotas) {
        return DeudaEnvio.builder().deudas(List.of(DeudaEnvio.DeudaChequeraEnvio.builder()
                .lectivoId(lectivoId).cuotas(cuotas).build())).build();
    }

    @Test
    void omiteSinPublicarCuandoNoHayCorreoValido() {
        when(reader.findByChequeraId(342038L)).thenReturn(Optional.of(alta(false, false)));

        var resultado = service.ejecutar(tarea(), job());

        assertThat(resultado.getEstado()).isEqualTo(EnvioTareaEstado.SIN_CORREOS);
        verifyNoInteractions(gateway);
    }

    @Test
    void retieneYNotificaCuandoSuperaElUmbral() {
        when(reader.findByChequeraId(342038L)).thenReturn(Optional.of(alta(true, false)));
        when(gateway.deuda(any(), anyInt())).thenReturn(deuda(37, 4));
        when(gateway.notificarDeuda(2, 520, 446L)).thenReturn(RESPUESTA_ENVIO);

        var resultado = service.ejecutar(tarea(), job());

        assertThat(resultado.getEstado()).isEqualTo(EnvioTareaEstado.RETENIDA);
        assertThat(resultado.getRetenida()).isTrue();
        verify(gateway).marcarRetenida(2, 520, 446L, true);
        verify(gateway).notificarDeuda(2, 520, 446L);
        verify(gateway, never()).enviarChequera(anyInt(), anyInt(), anyLong(), anyInt(), anyBoolean(), anyLong());
    }

    @Test
    void enviaConBulkYCodigosDeBarrasCuandoNoRetiene() {
        when(reader.findByChequeraId(342038L)).thenReturn(Optional.of(alta(true, true)));
        when(gateway.deuda(any(), anyInt())).thenReturn(deuda(37, 3));
        when(gateway.enviarChequera(2, 520, 446L, 1, true, 1L)).thenReturn(RESPUESTA_ENVIO);

        var resultado = service.ejecutar(tarea(), job());

        assertThat(resultado.getEstado()).isEqualTo(EnvioTareaEstado.ENCOLADA);
        assertThat(resultado.getRetenida()).isFalse();
        assertThat(resultado.getDetalle()).contains("342038").contains("2/520/446").contains("Retenida: No");
        verify(gateway).marcarRetenida(2, 520, 446L, false);
        verify(gateway).enviarChequera(2, 520, 446L, 1, true, 1L);
        verify(gateway, never()).notificarDeuda(anyInt(), anyInt(), anyLong());
    }

    @Test
    void omiteSinPublicarCuandoLaChequeraYaFueEnviada() {
        when(reader.findByChequeraId(342038L)).thenReturn(Optional.of(alta(true, true, true)));

        var resultado = service.ejecutar(tarea(), job());

        assertThat(resultado.getEstado()).isEqualTo(EnvioTareaEstado.OMITIDA);
        assertThat(resultado.getCodigo()).isEqualTo("YA_ENVIADA");
        verify(gateway, never()).enviarChequera(anyInt(), anyInt(), anyLong(), anyInt(), anyBoolean(), anyLong());
    }

    @Test
    void omiteCuandoLaSerieYaNoEstaEnElPadron() {
        when(reader.findByChequeraId(342038L)).thenReturn(Optional.empty());

        var resultado = service.ejecutar(tarea(), job());

        assertThat(resultado.getEstado()).isEqualTo(EnvioTareaEstado.OMITIDA);
        assertThat(resultado.getCodigo()).isEqualTo("NO_ENCONTRADA");
        verifyNoInteractions(gateway);
    }

    @Test
    void capturarCorreosRegistraElResultado() {
        when(gateway.capturarCorreos(any(), anyInt())).thenReturn(2);
        var job = EnvioJob.builder().jobId(1L).operacion(EnvioOperacion.CAPTURAR_CORREOS)
                .lectivoId(37).facultadId(2).geograficaId(1).umbralCuotasDeuda(3).build();

        var resultado = service.ejecutar(tarea(), job);

        assertThat(resultado.getEstado()).isEqualTo(EnvioTareaEstado.CORREO_CAPTURADO);
        assertThat(resultado.getMensaje()).contains("2");
    }

    @Test
    void extenderDebitosRegistraElResultado() {
        var job = EnvioJob.builder().jobId(1L).operacion(EnvioOperacion.EXTENDER_DEBITOS)
                .lectivoId(37).facultadId(2).geograficaId(1).umbralCuotasDeuda(3).build();

        var resultado = service.ejecutar(tarea(), job);

        assertThat(resultado.getEstado()).isEqualTo(EnvioTareaEstado.DEBITO_EXTENDIDO);
        verify(gateway).extenderDebito(2, 520, 446L);
    }
}

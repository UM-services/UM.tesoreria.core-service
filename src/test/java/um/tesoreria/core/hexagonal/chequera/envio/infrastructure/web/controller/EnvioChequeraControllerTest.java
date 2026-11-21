package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.hexagonal.chequera.envio.application.service.EnvioService;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobDetalle;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioOperacion;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.in.EnvioMasivoUseCase;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.mapper.ChequeraEnvioAltaDtoMapper;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.mapper.EnvioJobDtoMapper;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(EnvioChequeraController.class)
@Import({ChequeraEnvioAltaDtoMapper.class, EnvioJobDtoMapper.class})
class EnvioChequeraControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private EnvioService service;

    @MockitoBean
    private EnvioMasivoUseCase useCase;

    private static ChequeraEnvioAlta fila(boolean enviado, boolean retenida) {
        return new ChequeraEnvioAlta(342038L, 2, 520, 446L, 1, new BigDecimal("1001"), 1, 37, 1,
                "ABALLAY MURUA", "Luna Martina", "Matrícula y Arancel", "Ciclo Completo",
                "aballayluna@gmail.com", "l.aballay@alumno.um.edu.ar", true, true, enviado, retenida);
    }

    private static EnvioJob job() {
        return EnvioJob.builder()
                .jobId(9L).operacion(EnvioOperacion.MAILING).lectivoId(37).facultadId(2).geograficaId(1)
                .estado(EnvioJobEstado.PENDIENTE).totalTareas(3).tareasPendientes(3).umbralCuotasDeuda(3)
                .build();
    }

    @Test
    void devuelveElPadronConLosCamposQueUsaLaPantalla() {
        when(service.altas(37, 2, 1)).thenReturn(List.of(fila(true, false)));

        mockMvc.get().uri("/api/tesoreria/core/envio/chequera/altas/37/2/1")
                .assertThat().hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].numero").isEqualTo("2/520/446");
        mockMvc.get().uri("/api/tesoreria/core/envio/chequera/altas/37/2/1")
                .assertThat().hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].personaApellidoNombre").isEqualTo("ABALLAY MURUA, Luna Martina");
    }

    @Test
    void exponeElAliasCortoDelModulo() {
        when(service.altas(37, 2, 1)).thenReturn(List.of(fila(false, true)));

        mockMvc.get().uri("/envio/chequera/altas/37/2/1")
                .assertThat().hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].retenida").isEqualTo(true);
    }

    @Test
    void elPadronExigePermisoDeConsulta() throws NoSuchMethodException {
        Method metodo = EnvioChequeraController.class.getMethod("altas", Integer.class, Integer.class, Integer.class);
        RequierePermiso permiso = metodo.getAnnotation(RequierePermiso.class);

        assertThat(permiso).isNotNull();
        assertThat(permiso.value()).isEqualTo("chequeras.envio.consultar");
    }

    @Test
    void iniciarJobRespondeAceptado() {
        when(useCase.iniciar(any(), eq(9L))).thenReturn(job());

        mockMvc.post().uri("/api/tesoreria/core/envio/chequera/jobs")
                .header("X-User-Id", "9")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"operacion":"MAILING","lectivoId":37,"facultadId":2,"geograficaId":1,
                         "soloNoEnviadas":true,"codigoBarras":false,"umbralCuotasDeuda":3}
                        """)
                .assertThat().hasStatus(202)
                .bodyJson().extractingPath("$.jobId").isEqualTo(9);
    }

    @Test
    void consultaElDetalleDelJob() {
        when(useCase.consultar(9L)).thenReturn(EnvioJobDetalle.builder().job(job()).tareas(List.of()).build());

        mockMvc.get().uri("/api/tesoreria/core/envio/chequera/jobs/9")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.job.estado").isEqualTo("PENDIENTE");
    }

    @Test
    void reintentarAsumeSoloFallidasPorDefecto() {
        when(useCase.reintentar(9L, true)).thenReturn(job());

        mockMvc.post().uri("/api/tesoreria/core/envio/chequera/jobs/9/reintentar")
                .assertThat().hasStatus(202)
                .bodyJson().extractingPath("$.jobId").isEqualTo(9);
    }
}

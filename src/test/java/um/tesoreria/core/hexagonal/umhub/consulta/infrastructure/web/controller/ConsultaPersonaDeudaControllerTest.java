package um.tesoreria.core.hexagonal.umhub.consulta.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.umhub.consulta.application.service.ConsultaPersonaDeudaService;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaDeuda;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaDomicilio;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaPersona;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.TipoDocumentoConsulta;
import um.tesoreria.core.hexagonal.umhub.consulta.infrastructure.web.mapper.ConsultaDtoMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(ConsultaPersonaDeudaController.class)
@Import(ConsultaDtoMapper.class)
class ConsultaPersonaDeudaControllerTest {

    private static final String URL = "/api/tesoreria/core/umhub/consulta/persona/30123456";

    @Autowired
    private MockMvcTester mockMvc;
    @MockitoBean
    private ConsultaPersonaDeudaService service;

    private ConsultaPersona persona() {
        return ConsultaPersona.builder()
                .numeroDocumento(new BigDecimal("30123456"))
                .nombre("Maria")
                .apellido("GARCIA")
                .sexo("F")
                .numeroPrefijo("015")
                .numeroPosfijo("5551234")
                .documentos(List.of(TipoDocumentoConsulta.builder().documentoId(8).nombre("DNI").build()))
                .domicilio(ConsultaDomicilio.builder()
                        .calle("Av. San Martin").puerta("1234").codigoPostal("M5500")
                        .provinciaId(13).provinciaNombre("Mendoza")
                        .localidadId(255).localidadNombre("Ciudad de Mendoza")
                        .telefono("02614000000").movil("02615000000")
                        .emailPersonal("maria@example.com").emailInstitucional("mgarcia@um.edu.ar")
                        .build())
                .build();
    }

    @Test
    void exponePersonaConIdentidadTiposYContacto() {
        when(service.findPersona(new BigDecimal("30123456"))).thenReturn(Optional.of(persona()));

        var response = mockMvc.get().uri(URL).assertThat().hasStatusOk();

        response.bodyJson().extractingPath("$.numeroDocumento").isEqualTo("30123456");
        response.bodyJson().extractingPath("$.apellido").isEqualTo("GARCIA");
        response.bodyJson().extractingPath("$.numeroPrefijo").isEqualTo("015");
        response.bodyJson().extractingPath("$.numeroPosfijo").isEqualTo("5551234");
        response.bodyJson().extractingPath("$.documentos[0].documentoId").isEqualTo(8);
        response.bodyJson().extractingPath("$.documentos[0].nombre").isEqualTo("DNI");
        response.bodyJson().extractingPath("$.domicilio.emailPersonal").isEqualTo("maria@example.com");
        response.bodyJson().extractingPath("$.domicilio.movil").isEqualTo("02615000000");
        response.bodyJson().extractingPath("$.domicilio.provinciaNombre").isEqualTo("Mendoza");
        response.bodyJson().extractingPath("$.domicilio.localidadNombre").isEqualTo("Ciudad de Mendoza");
    }

    @Test
    void responde404CuandoElNumeroNoTienePersona() {
        when(service.findPersona(new BigDecimal("30123456"))).thenReturn(Optional.empty());

        mockMvc.get().uri(URL).assertThat().hasStatus(404);
    }

    @Test
    void responde400ConNumeroFueraDelRangoDeDigitos() {
        mockMvc.get().uri("/api/tesoreria/core/umhub/consulta/persona/123").assertThat().hasStatus(400);

        verify(service, never()).findPersona(any());
    }

    @Test
    void deudaAgregadaExponeTotalesYRespetaElParametroExtended() {
        ConsultaDeuda deuda = ConsultaDeuda.builder()
                .numeroDocumento(new BigDecimal("30123456"))
                .cuotas(4)
                .deuda(new BigDecimal("458.25"))
                .deudas(List.of())
                .vencimientos(List.of())
                .build();
        when(service.findDeuda(new BigDecimal("30123456"), true)).thenReturn(Optional.of(deuda));

        var response = mockMvc.get().uri(URL + "/deuda?extended=true").assertThat().hasStatusOk();

        response.bodyJson().extractingPath("$.numeroDocumento").isEqualTo("30123456");
        response.bodyJson().extractingPath("$.cuotas").isEqualTo(4);
        response.bodyJson().extractingPath("$.deuda").isEqualTo(458.25);
    }

    @Test
    void deudaPorDefectoNoEsExtendida() {
        when(service.findDeuda(new BigDecimal("30123456"), false)).thenReturn(Optional.of(
                ConsultaDeuda.builder().numeroDocumento(new BigDecimal("30123456")).cuotas(0)
                        .deuda(BigDecimal.ZERO).deudas(List.of()).vencimientos(List.of()).build()));

        mockMvc.get().uri(URL + "/deuda").assertThat().hasStatusOk();

        verify(service).findDeuda(new BigDecimal("30123456"), false);
        verify(service, never()).findDeuda(any(), org.mockito.ArgumentMatchers.eq(true));
    }
}

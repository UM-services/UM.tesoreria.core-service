package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloValidationException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.service.UbicacionArticuloService;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.mapper.UbicacionArticuloDtoMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@WebMvcTest(UbicacionArticuloController.class)
@Import(UbicacionArticuloDtoMapper.class)
class UbicacionArticuloControllerTest {

    private static final String ASIGNACION = """
            {"ubicacionId": 1, "articuloId": 2, "numeroCuenta": 51010101}""";

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private UbicacionArticuloService service;

    @Test
    void asignacion_200() {
        when(service.save(any())).thenAnswer(inv -> {
            UbicacionArticulo pedido = inv.getArgument(0);
            pedido.setUbicacionArticuloId(9L);
            return pedido;
        });

        assertThat(post(ASIGNACION)).hasStatusOk().bodyJson().extractingPath("$.ubicacionArticuloId").isEqualTo(9);
    }

    @Test
    void asignacion_campoInvalido_400ConCampo() {
        when(service.save(any())).thenThrow(new UbicacionArticuloValidationException("numeroCuenta", "La cuenta indicada no existe en el plan de cuentas."));

        var respuesta = post(ASIGNACION);

        assertThat(respuesta).hasStatus(400).hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("CAMPO_INVALIDO");
        assertThat(respuesta).bodyJson().extractingPath("$.campo").isEqualTo("numeroCuenta");
        assertThat(respuesta).bodyJson().extractingPath("$.detail").isEqualTo("La cuenta indicada no existe en el plan de cuentas.");
    }

    @Test
    void asignacion_choqueTrasReintento_409() {
        when(service.save(any())).thenThrow(new UbicacionArticuloConflictException(true, "interbloqueo"));

        var respuesta = post(ASIGNACION);

        assertThat(respuesta).hasStatus(409);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("CONFLICTO");
    }

    @Test
    void asignacion_conflictoNoReintentable_409SinInvitarAReintentar() {
        when(service.save(any())).thenThrow(new UbicacionArticuloConflictException(false, "1062 desconocido"));

        var respuesta = post(ASIGNACION);

        assertThat(respuesta).hasStatus(409);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("CONFLICTO");
        assertThat(respuesta).bodyJson().extractingPath("$.detail").asString().contains("choca con otro dato").doesNotContain("reintentá");
    }

    @Test
    void asignacion_bloqueada_409QuePideReintentarMasTarde() {
        when(service.save(any())).thenThrow(UbicacionArticuloConflictException.bloqueado("espera de bloqueo vencida"));

        var respuesta = post(ASIGNACION);

        assertThat(respuesta).hasStatus(409);
        assertThat(respuesta).bodyJson().extractingPath("$.detail").asString().contains("tiene tomada la asignación");
    }

    @Test
    void contenidoNoJson_415ConElMismoContrato() {
        var respuesta = mockMvc.post().uri("/ubicacionArticulo/").contentType(MediaType.TEXT_PLAIN).content("hola").exchange();

        assertThat(respuesta).hasStatus(415).hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("TIPO_DE_CONTENIDO_NO_SOPORTADO");
        assertThat(respuesta).bodyJson().extractingPath("$.detail").isEqualTo("El cuerpo tiene que enviarse como application/json.");
        assertThat(respuesta).headers().containsHeader("Accept");
        verifyNoInteractions(service);
    }

    @Test
    void cuerpoConTipoInvalido_400CuerpoInvalidoConCampo() {
        var respuesta = post("{\"ubicacionId\": \"x\", \"articuloId\": 2}");

        assertThat(respuesta).hasStatus(400);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("CUERPO_INVALIDO");
        assertThat(respuesta).bodyJson().extractingPath("$.campo").isEqualTo("ubicacionId");
        verifyNoInteractions(service);
    }

    @Test
    void parInexistente_404ConCuerpo() {
        when(service.getByUbicacionAndArticulo(1, 2L)).thenReturn(Optional.empty());

        var respuesta = mockMvc.get().uri("/ubicacionArticulo/1/2").exchange();

        assertThat(respuesta).hasStatus(404);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("UBICACION_ARTICULO_NO_ENCONTRADO");
    }

    @Test
    void parametroNoNumerico_400() {
        var respuesta = mockMvc.get().uri("/ubicacionArticulo/x/2").exchange();

        assertThat(respuesta).hasStatus(400);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("PARAMETRO_INVALIDO");
        assertThat(respuesta).bodyJson().extractingPath("$.campo").isEqualTo("ubicacionId");
    }

    @Test
    void errorNoMapeado_500Generico() {
        when(service.save(any())).thenThrow(new IllegalStateException("select * from ubicacion_articulo"));

        var respuesta = post(ASIGNACION);

        assertThat(respuesta).hasStatus(500);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("ERROR_INTERNO");
        assertThat(respuesta).bodyText().doesNotContain("select");
    }

    private MvcTestResult post(String json) {
        return mockMvc.post().uri("/ubicacionArticulo/").contentType(MediaType.APPLICATION_JSON).content(json).exchange();
    }
}

package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.web.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.web.mapper.CuentaDtoMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(CuentaController.class)
@Import(CuentaDtoMapper.class)
class CuentaControllerTest {
    @Autowired MockMvcTester mvc;
    @MockitoBean CuentaService service;
    static final String NUMERO = "11010100000";

    @ParameterizedTest
    @ValueSource(strings = {"/cuenta", "/api/tesoreria/core/cuenta"})
    void bajaLibreOInexistenteConserva204Vacio(String base) {
        mvc.delete().uri(base + "/" + NUMERO).assertThat().hasStatus(HttpStatus.NO_CONTENT).body().isEmpty();
    }

    @Test
    void rechazosDeAltaEdicionYBajaTienen409YJson() {
        when(service.add(any())).thenThrow(new CuentaConflictoException("Ya existe"));
        when(service.update(any(), any())).thenThrow(new CuentaConflictoException("Nombre obligatorio"));
        doThrow(new CuentaConflictoException("Tiene movimientos")).when(service).delete(any());
        var alta = mvc.post().uri("/cuenta/").contentType(MediaType.APPLICATION_JSON)
                .content("{\"numeroCuenta\":" + NUMERO + "}").assertThat().hasStatus(HttpStatus.CONFLICT);
        alta.bodyJson().extractingPath("$.status").isEqualTo(409);
        alta.bodyJson().extractingPath("$.message").isEqualTo("Ya existe");
        mvc.put().uri("/cuenta/" + NUMERO).contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"\"}")
                .assertThat().hasStatus(HttpStatus.CONFLICT).bodyJson().extractingPath("$.message").isEqualTo("Nombre obligatorio");
        mvc.delete().uri("/cuenta/" + NUMERO).assertThat().hasStatus(HttpStatus.CONFLICT)
                .bodyJson().extractingPath("$.message").isEqualTo("Tiene movimientos");
    }

    @Test
    void payloadVb6MantieneNombresDeCamposY200() {
        when(service.add(any())).thenAnswer(i -> i.getArgument(0));
        var respuesta = mvc.post().uri("/api/tesoreria/core/cuenta/").contentType(MediaType.APPLICATION_JSON)
                .content("{\"numeroCuenta\":11010100000,\"nombre\":\"Prueba\",\"grado\":null,\"visible\":1,\"grado1\":10000000000}")
                .assertThat().hasStatus(HttpStatus.OK);
        respuesta.bodyJson().extractingPath("$.numeroCuenta").isEqualTo(11010100000L);
        respuesta.bodyJson().extractingPath("$.visible").isEqualTo(1);
        respuesta.bodyJson().extractingPath("$.nombre").isEqualTo("Prueba");
    }

    @Test
    void filtroPorSedeYGradoUsaAmbosParametros() {
        when(service.findAllByGeograficaIdAndGrado(2, 4)).thenReturn(List.of(Cuenta.builder()
                .numeroCuenta(new BigDecimal(NUMERO)).grado(4).geograficaId(2).build()));
        mvc.get().uri("/cuenta/sede/2/grado/4").assertThat().hasStatus(HttpStatus.OK)
                .bodyJson().extractingPath("$[0].geograficaId").isEqualTo(2);
        verify(service).findAllByGeograficaIdAndGrado(2, 4);
    }

    @Test
    void edicionInexistenteConserva404() {
        when(service.update(any(), any())).thenReturn(Optional.empty());
        mvc.put().uri("/cuenta/" + NUMERO).contentType(MediaType.APPLICATION_JSON).content("{}")
                .assertThat().hasStatus(HttpStatus.NOT_FOUND);
    }
}

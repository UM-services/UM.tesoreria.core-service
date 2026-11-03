package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.application.service.MateriasInscriptasService;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.mapper.MateriasInscriptasDtoMapper;

import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriasEstado;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.FuenteMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.EstadoMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.CursoMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.PeriodoMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.PeriodoCursada;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriaInscripta;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(MateriasInscriptasController.class)
@Import(MateriasInscriptasDtoMapper.class)
class MateriasInscriptasControllerTest {

    private static final String URL = "/api/tesoreria/core/materiasInscriptas/1/2/12345";

    @Autowired
    private MockMvcTester mockMvc;
    @MockitoBean
    private MateriasInscriptasService service;

    // protects=el endpoint responde 200 con un arreglo JSON vacío (no objeto ni null) en la ruta publicada;
    // fails_when=cambia la ruta, el cuerpo es null u objeto, o el estado no es 200;
    // no_claim=que el service arme la lista (aquí es un mock) ni la consulta a la facultad.
    @Test
    void respondsAnEmptyJsonArray() {
        when(service.getMateriasInscriptas(1, 2, 12345L)).thenReturn(List.of());

        var response = mockMvc.get().uri(URL)
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk();

        response.bodyJson().extractingPath("$").asArray().isEmpty();
    }

    // protects=HTTP 200 expone SIUM primero y GUARANI después con el mismo contrato JSON;
    // fails_when=se envuelve la lista, cambia un nombre de campo o se pierde el grupo Guaraní;
    // no_claim=el service está simulado; no prueba B1 ni las bases.
    @Test
    void serializesBothSources() {
        var sium = new MateriasEstado(
                FuenteMaterias.SIUM,
                EstadoMaterias.SIN_INSCRIPCION,
                null, null, null, null, List.of());
        var guarani = new MateriasEstado(
                FuenteMaterias.GUARANI,
                EstadoMaterias.OK,
                2, "Versión 7", "Propuesta sintética", null, List.of(
                new CursoMaterias(1, List.of(
                new PeriodoMaterias(
                PeriodoCursada.SEGUNDO_SEMESTRE,
                List.of(new MateriaInscripta(
                "ACT-1", "Actividad sintética")))))));
        when(service.getMateriasInscriptas(1, 2, 12345L)).thenReturn(List.of(sium, guarani));
        mockMvc.get().uri(URL).accept(MediaType.APPLICATION_JSON).assertThat().hasStatusOk()
                .bodyJson().isEqualTo("""
                [{"fuente":"SIUM","estado":"SIN_INSCRIPCION","cursoInscripcion":null,"plan":null,
                  "carrera":null,"observaciones":null,"cursos":[]},
                 {"fuente":"GUARANI","estado":"OK","cursoInscripcion":2,"plan":"Versión 7",
                  "carrera":"Propuesta sintética","observaciones":null,
                  "cursos":[{"curso":1,"periodos":[{"periodo":"SEGUNDO_SEMESTRE",
                  "materias":[{"materiaId":"ACT-1","nombre":"Actividad sintética"}]}]}]}]
                """);
    }

    // protects=una chequera inexistente responde HTTP 400, igual que chequera/estado;
    // fails_when=el controller deja subir la excepción (500) o responde 200 con una lista vacía;
    // no_claim=que el use case lance la excepción con una base real (lo prueba GetMateriasInscriptasUseCaseImplTest).
    @Test
    void missingChequeraRespondsBadRequest() {
        when(service.getMateriasInscriptas(1, 2, 12345L)).thenThrow(new ChequeraSerieException(1, 2, 12345L));

        mockMvc.get().uri(URL)
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatus(HttpStatus.BAD_REQUEST);
    }
}

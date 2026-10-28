package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.MateriaCursada;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.mapper.PendienteInfoFacultadMapper;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class PendienteInfoFacultadConsumerTest {

    private static final String BASE = "http://facultad:8080";
    private static final String INFO_URL = BASE + "/pendienteinfo/info/1/37/1/30123456/2";

    @Mock
    private FacultadUrlResolver urlResolver;

    private MockRestServiceServer server;
    private RelojDePrueba reloj;
    private PendienteInfoFacultadConsumer consumer;

    @BeforeEach
    void setUp() {
        var builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        reloj = new RelojDePrueba();
        consumer = new PendienteInfoFacultadConsumer(builder.build(), urlResolver, new PendienteInfoFacultadMapper(), reloj);
    }

    // protects=AC7: un personaId con exponente va a la URL como número plano;
    // fails_when=el consumer usa toString() y manda 3.0123456E+7;
    // no_claim=el formato que guarda la base de la facultad: aquí el servidor es simulado.
    @Test
    void personaIdGoesInTheUrlWithoutScientificNotation() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andExpect(method(GET)).andRespond(withSuccess(sinMaterias(), MediaType.APPLICATION_JSON));

        consumer.findByAlumno(1, 37, 1, new BigDecimal("3.0123456E+7"), 2);

        server.verify();
    }

    // protects=AC1: con control.json devuelve curso 5 y las 13 materias con su curso y período, sin buscar nombres;
    // fails_when=el consumer no deserializa la respuesta, pierde una materia o llama a /materia/unique sin necesidad;
    // no_claim=la respuesta real de la facultad: control.json es una copia reducida y el servidor es simulado.
    @Test
    void controlFixtureGivesThirteenSubjectsWithoutSearchingNames() throws IOException {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess(fixture("/pendienteinfo/control.json"), MediaType.APPLICATION_JSON));

        var result = consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2).orElseThrow();

        assertThat(result.curso()).isEqualTo(5);
        assertThat(result.materias()).hasSize(13)
                .allSatisfy(m -> assertThat(m.nombre()).isNotBlank())
                .allSatisfy(m -> assertThat(m.curso()).isNotNull());
        server.verify();
    }

    // protects=AC3: una materia sin nombre se pide una sola vez a /materia/unique y se usa ese nombre;
    // fails_when=el consumer no pide el nombre, lo pide dos veces o ignora la respuesta;
    // no_claim=que /materia/unique responda así en la facultad real.
    @Test
    void missingNameIsRequestedOnceAndUsed() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess(respuesta(detalles(4, "X900", "X900")), MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE + "/materia/unique/1/4/X900"))
                .andRespond(withSuccess("{\"facultadId\":1,\"planId\":4,\"materiaId\":\"X900\",\"nombre\":\"BUSCADA\",\"otro\":1}",
                        MediaType.APPLICATION_JSON));

        var materias = consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2).orElseThrow().materias();

        assertThat(materias).extracting(MateriaCursada::materiaId, MateriaCursada::nombre)
                .containsExactly(tuple("X900", "BUSCADA"), tuple("X900", "BUSCADA"));
        server.verify();
    }

    // protects=si /materia/unique falla, el nombre queda null y no se hace ninguna llamada más;
    // fails_when=el consumer sigue llamando después de la primera falla, o propaga la excepción;
    // no_claim=el mensaje de log: el test no lo mira.
    @Test
    void firstFailureOfMateriaUniqueStopsTheSearches() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess(respuesta(detalles(4, "A", "B", "C")), MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE + "/materia/unique/1/4/A")).andRespond(withServerError());

        var materias = consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2).orElseThrow().materias();

        assertThat(materias).extracting(MateriaCursada::nombre).containsOnlyNulls().hasSize(3);
        server.verify();
    }

    // protects=T-c y AC4: con 12 claves sin nombre se llaman exactamente las 10 primeras, en orden de plan y materia;
    // fails_when=se llaman 11 o 12, o 10 cualesquiera, o en el orden de los detalles;
    // no_claim=la latencia real de la facultad.
    @Test
    void twelveMissingNamesCallExactlyTheFirstTenInOrder() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        // Orden de los detalles al revés del orden de búsqueda: plan 9 primero, plan 4 después.
        var detalles = new ArrayList<String>();
        IntStream.rangeClosed(1, 6).forEach(i -> detalles.add(detalle(9, "M%02d".formatted(7 - i))));
        IntStream.rangeClosed(1, 6).forEach(i -> detalles.add(detalle(4, "M%02d".formatted(7 - i))));
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess(respuesta(String.join(",", detalles)), MediaType.APPLICATION_JSON));
        List<String> esperadas = new ArrayList<>();
        for (int plan : new int[]{4, 9}) {
            for (int i = 1; i <= 6; i++) esperadas.add("%d/M%02d".formatted(plan, i));
        }
        esperadas.subList(0, 10).forEach(clave -> server.expect(requestTo(BASE + "/materia/unique/1/" + clave))
                .andRespond(withSuccess("{\"nombre\":\"N-" + clave + "\"}", MediaType.APPLICATION_JSON)));

        var materias = consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2).orElseThrow().materias();

        server.verify();
        assertThat(materias.stream().filter(m -> m.nombre() != null)).hasSize(10);
        assertThat(materias).filteredOn(m -> m.nombre() == null)
                .extracting(m -> m.planId() + "/" + m.materiaId()).containsExactlyInAnyOrder("9/M05", "9/M06");
    }

    // protects=AC4: el presupuesto de 3 s se mide con el Clock del core y corta las búsquedas siguientes;
    // fails_when=el consumer ignora el tiempo, usa otro reloj, o corta antes o después del límite;
    // no_claim=el timeout de transporte del RestClient real: se prueba contra dev.
    @Test
    void threeSecondBudgetStopsTheSearches() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess(respuesta(detalles(4, "A", "B", "C", "D", "E")), MediaType.APPLICATION_JSON));
        for (String materia : List.of("A", "B", "C")) {
            server.expect(requestTo(BASE + "/materia/unique/1/4/" + materia)).andRespond(avanzando(Duration.ofSeconds(1),
                    "{\"nombre\":\"N-" + materia + "\"}"));
        }

        var materias = consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2).orElseThrow().materias();

        server.verify();
        assertThat(materias).extracting(MateriaCursada::nombre).containsExactly("N-A", "N-B", "N-C", null, null);
    }

    // protects=materiascurso vacía o ausente no rompe y deja curso y período null (sin tocar /materia/unique);
    // fails_when=el consumer falla con secciones vacías;
    // no_claim=que la facultad mande esa forma hoy.
    @Test
    void emptySectionsGiveSubjectsWithoutCursoNorPeriodo() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess("""
                {"inscripcion":{"curso":2,"planId":9,"carreraId":2,"observaciones":" "},
                 "detalles":[{"facultadId":1,"planId":9,"materiaId":"A","materia":{"nombre":"UNA"}}],
                 "materias":[],"materiascarrera":[],"materiascurso":[]}
                """, MediaType.APPLICATION_JSON));

        var result = consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2).orElseThrow();

        assertThat(result.observaciones()).isNull();
        assertThat(result.materias()).extracting(MateriaCursada::nombre, MateriaCursada::curso, MateriaCursada::periodo)
                .containsExactly(tuple("UNA", null, null));
    }

    // protects=AC6: inscripcion null, detalles vacíos o cuerpo vacío dan Optional vacío;
    // fails_when=el consumer devuelve una inscripción sin materias o lanza una excepción;
    // no_claim=que la facultad responda 200 en estos casos: así se observó en dev.
    @Test
    void noEnrollmentOrNoDetailsGivesEmpty() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess("{\"inscripcion\":null,\"detalles\":[]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess(sinMaterias(), MediaType.APPLICATION_JSON));
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        for (int i = 0; i < 3; i++) {
            assertThat(consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2)).isEmpty();
        }
        server.verify();
    }

    // protects=AC8: HTTP 500 en /pendienteinfo sale como excepción, no como Optional vacío;
    // fails_when=el consumer traga el error y el caller lo confunde con "sin inscripción";
    // no_claim=cómo se traduce el error a NO_DISPONIBLE: eso es de #487.
    @Test
    void serverErrorOnPendienteInfoIsThrown() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withServerError());

        assertThatThrownBy(() -> consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2))
                .isInstanceOf(HttpServerErrorException.class);
        server.verify();
    }

    // protects=AC8: un JSON ilegible en /pendienteinfo sale como excepción;
    // fails_when=el consumer devuelve vacío o un resultado parcial con un cuerpo roto;
    // no_claim=la clase exacta de la excepción más allá de RestClientException.
    @Test
    void unreadableJsonOnPendienteInfoIsThrown() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess("{\"inscripcion\": [", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2))
                .isInstanceOf(RestClientException.class);
        server.verify();
    }

    // protects=un campo que el core no conoce (datos del alumno) no rompe la lectura;
    // fails_when=falta ignoreUnknown en algún DTO anidado;
    // no_claim=que el core no guarde esos campos: el test no mira el log.
    @Test
    void unknownFieldsAreIgnored() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess("""
                {"campoNuevo":1,"persona":{"x":1},
                 "inscripcion":{"curso":2,"planId":9,"carreraId":2,"observaciones":"o","factura":3},
                 "plan":{"nombre":"P","extra":1},"carrera":{"nombre":"C","extra":1},
                 "detalles":[{"facultadId":1,"planId":9,"materiaId":"A","moroso":0,"materia":{"nombre":"UNA","dias":"L"}}],
                 "materias":[{"facultadId":1,"planId":9,"materiaId":"A","nombre":"UNA","taller":0}],
                 "materiascarrera":[{"facultadId":1,"planId":9,"carreraId":2,"materiaId":"A","horas":4}],
                 "materiascurso":[{"facultadId":1,"planId":9,"carreraId":2,"materiaId":"A","curso":2,"periodo":1,"id":8}]}
                """, MediaType.APPLICATION_JSON));

        var result = consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2).orElseThrow();

        assertThat(result.materias()).extracting(MateriaCursada::curso, MateriaCursada::periodo).containsExactly(tuple(2, 1));
    }

    // protects=/materia/unique con nombre vacío deja el nombre en null;
    // fails_when=el consumer guarda "" como nombre;
    // no_claim=que la facultad responda así.
    @Test
    void blankNameFromMateriaUniqueStaysNull() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess(respuesta(detalles(4, "A")), MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE + "/materia/unique/1/4/A")).andRespond(withSuccess("{\"nombre\":\"  \"}", MediaType.APPLICATION_JSON));

        var materias = consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2).orElseThrow().materias();

        assertThat(materias).extracting(MateriaCursada::nombre).containsOnlyNulls();
    }

    // protects=un error de transporte o un cuerpo ilegible en /materia/unique deja el nombre en null, no se propaga
    //   y corta las búsquedas siguientes;
    // fails_when=el consumer atrapa solo errores HTTP, o sigue llamando después de la falla;
    // no_claim=el timeout real del RestClient: el servidor es simulado.
    @Test
    void transportAndParseFailuresOnMateriaUniqueStopTheSearchesWithoutThrowing() throws IOException {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess(respuesta(detalles(4, "A", "B")), MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE + "/materia/unique/1/4/A")).andRespond(withException(new IOException("timeout")));
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess(respuesta(detalles(4, "A", "B")), MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE + "/materia/unique/1/4/A")).andRespond(withSuccess("{\"nombre\": [", MediaType.APPLICATION_JSON));

        for (int i = 0; i < 2; i++) {
            var materias = consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2).orElseThrow().materias();
            assertThat(materias).extracting(MateriaCursada::nombre).containsOnlyNulls().hasSize(2);
        }
        server.verify();
    }

    // protects=una respuesta 200 vacía de /materia/unique deja el nombre en null y no rompe la consulta;
    // fails_when=el consumer lanza NullPointerException con cuerpo vacío;
    // no_claim=que la facultad responda así.
    @Test
    void emptyBodyFromMateriaUniqueLeavesTheNameNull() {
        when(urlResolver.getBaseUrl(1)).thenReturn(BASE);
        server.expect(requestTo(INFO_URL)).andRespond(withSuccess(respuesta(detalles(4, "A")), MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE + "/materia/unique/1/4/A")).andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        var materias = consumer.findByAlumno(1, 37, 1, new BigDecimal("30123456"), 2).orElseThrow().materias();

        assertThat(materias).extracting(MateriaCursada::nombre).containsOnlyNulls();
        server.verify();
    }

    private ResponseCreator avanzando(Duration paso, String cuerpo) {
        return request -> {
            reloj.avanzar(paso);
            return withSuccess(cuerpo, MediaType.APPLICATION_JSON).createResponse(request);
        };
    }

    private static String detalle(int planId, String materiaId) {
        return "{\"facultadId\":1,\"planId\":%d,\"materiaId\":\"%s\"}".formatted(planId, materiaId);
    }

    private static String detalles(int planId, String... materias) {
        return java.util.Arrays.stream(materias).map(m -> detalle(planId, m)).reduce((a, b) -> a + "," + b).orElse("");
    }

    private static String respuesta(String detalles) {
        return "{\"inscripcion\":{\"curso\":3,\"planId\":9,\"carreraId\":2},\"detalles\":[" + detalles + "]}";
    }

    private static String sinMaterias() {
        return "{\"inscripcion\":{\"curso\":3,\"planId\":9,\"carreraId\":2},\"detalles\":[]}";
    }

    private String fixture(String path) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            assertThat(in).as("fixture %s", path).isNotNull();
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /** Reloj que solo avanza cuando el test lo pide. */
    private static final class RelojDePrueba extends Clock {
        private Instant ahora = Instant.parse("2026-10-28T12:00:00Z");

        void avanzar(Duration d) {
            ahora = ahora.plus(d);
        }

        @Override
        public java.time.ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return ahora;
        }
    }
}

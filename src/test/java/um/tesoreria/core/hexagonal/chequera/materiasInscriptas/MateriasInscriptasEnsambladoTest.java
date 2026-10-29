package um.tesoreria.core.hexagonal.chequera.materiasInscriptas;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.application.usecases.GetMateriasInscriptasUseCaseImpl;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.application.usecases.LogCapture;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.EstadoMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriasEstado;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.mapper.MateriasInscriptasDtoMapper;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.application.service.PendienteInfoFacultadService;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.application.usecases.FindInscripcionMateriasUseCaseImpl;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.consumer.PendienteInfoFacultadConsumer;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.mapper.PendienteInfoFacultadMapper;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Une el consumer real de #485, el use case de #487, el agrupador de #486 y el mapper a DTO.
 * Solo se simulan la base (services de chequera y tipo) y, en el primer test, el servidor de la facultad.
 */
class MateriasInscriptasEnsambladoTest {

    private static final String PATH = "/pendienteinfo/info/1/37/4/30123456/2";

    private final JsonMapper json = JsonMapper.builder().build();
    private final FacultadUrlResolver urlResolver = mock(FacultadUrlResolver.class);
    private final ChequeraSerieService chequeraSerieService = mock(ChequeraSerieService.class);
    private final TipoChequeraService tipoChequeraService = mock(TipoChequeraService.class);

    @BeforeEach
    void setUp() {
        when(chequeraSerieService.findByUnique(1, 2, 12345L)).thenReturn(ChequeraSerie.builder().facultadId(1)
                .tipoChequeraId(2).chequeraSerieId(12345L).lectivoId(37).geograficaId(4)
                .personaId(new BigDecimal("30123456")).documentoId(2).build());
        when(tipoChequeraService.findByTipoChequeraId(2)).thenReturn(TipoChequera.builder().tipoChequeraId(2)
                .claseChequera(ClaseChequera.builder().claseChequeraId(2).grado((byte) 1).build()).build());
    }

    // protects=control.json pasa por el consumer, el use case, el agrupador y el mapper y da el contrato materias-control.json;
    // fails_when=cambia un nombre, el orden, un período, un campo de inscripción o el nombre de un campo JSON;
    // no_claim=la respuesta real de la facultad, la URL desde la base y los beans RestClient y Clock de Spring (el servidor es simulado).
    @Test
    void controlFixtureEndsAsTheContract() throws IOException {
        var builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        when(urlResolver.getBaseUrl(1)).thenReturn("http://facultad:8080");
        server.expect(requestTo("http://facultad:8080" + PATH)).andExpect(method(GET))
                .andRespond(withSuccess(fixture("/pendienteinfo/control.json"), MediaType.APPLICATION_JSON));

        List<MateriasEstado> result = useCase(builder.build()).getMateriasInscriptas(1, 2, 12345L);

        JsonNode actual = json.valueToTree(new MateriasInscriptasDtoMapper().toResponse(result));
        assertThat(actual).isEqualTo(json.readTree(fixture("/materiasInscriptas/materias-control.json")));
        server.verify();
    }

    // protects=una facultad que responde a los 2 s con lectura de 500 ms da NO_DISPONIBLE por un timeout real;
    // fails_when=NO_DISPONIBLE viene de otra causa (URL, conexión rechazada, JSON roto) o el servidor no recibe el pedido exacto;
    // no_claim=el timeout de 15 s del RestClient global (acá es de 500 ms) ni la red real entre servicios.
    @Test
    void slowFacultyIsNoDisponibleByTimeout() throws IOException {
        var recibido = new AtomicReference<String>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            recibido.set(exchange.getRequestMethod() + " " + exchange.getRequestURI());
            try {
                Thread.sleep(2000);
                byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
            } catch (InterruptedException | IOException e) {
                // el cliente ya cortó la conexión
            } finally {
                exchange.close();
            }
        });
        server.start();
        try (var logs = new LogCapture(GetMateriasInscriptasUseCaseImpl.class)) {
            var factory = new JdkClientHttpRequestFactory();
            factory.setReadTimeout(Duration.ofMillis(500));
            when(urlResolver.getBaseUrl(1)).thenReturn("http://127.0.0.1:" + server.getAddress().getPort());

            long inicio = System.nanoTime();
            List<MateriasEstado> result = useCase(RestClient.builder().requestFactory(factory).build())
                    .getMateriasInscriptas(1, 2, 12345L);
            Duration demora = Duration.ofNanos(System.nanoTime() - inicio);

            assertThat(result).singleElement().satisfies(m -> {
                assertThat(m.estado()).isEqualTo(EstadoMaterias.NO_DISPONIBLE);
                assertThat(m.cursos()).isEmpty();
            });
            assertThat(recibido.get()).isEqualTo("GET " + PATH);
            // gstack-timing-ok: el readTimeout de 500 ms garantiza el piso; no hay techo para no depender de la carga de la máquina
            assertThat(demora).isGreaterThanOrEqualTo(Duration.ofMillis(450));
            assertThat(logs.warnings()).singleElement().satisfies(event ->
                    assertThat(event.getFormattedMessage()).contains("ResourceAccessException", "status=null"));
        } finally {
            server.stop(0);
        }
    }

    private GetMateriasInscriptasUseCaseImpl useCase(RestClient restClient) {
        var consumer = new PendienteInfoFacultadConsumer(restClient, urlResolver, new PendienteInfoFacultadMapper(),
                Clock.systemUTC());
        return new GetMateriasInscriptasUseCaseImpl(chequeraSerieService, tipoChequeraService,
                new PendienteInfoFacultadService(new FindInscripcionMateriasUseCaseImpl(consumer)));
    }

    private String fixture(String path) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            assertThat(in).as("fixture %s", path).isNotNull();
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

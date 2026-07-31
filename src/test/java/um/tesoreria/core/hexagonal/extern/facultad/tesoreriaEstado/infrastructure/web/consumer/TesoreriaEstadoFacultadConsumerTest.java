package um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.infrastructure.web.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.infrastructure.web.mapper.TesoreriaEstadoFacultadMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class TesoreriaEstadoFacultadConsumerTest {

    private static final String UNIQUE_URL = "http://facultad:8080/tesoreriaEstado/unique/1/1001/2";

    @Mock
    private FacultadUrlResolver urlResolver;

    private MockRestServiceServer server;
    private TesoreriaEstadoFacultadConsumer consumer;

    @BeforeEach
    void setUp() {
        var builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        consumer = new TesoreriaEstadoFacultadConsumer(builder.build(), urlResolver,
                new TesoreriaEstadoFacultadMapper());
    }

    @Test
    void findByUnique_mapsResponseToDomain() {
        when(urlResolver.getBaseUrl(1)).thenReturn("http://facultad:8080");

        server.expect(requestTo(UNIQUE_URL))
                .andExpect(method(GET))
                .andRespond(withSuccess("""
                        {
                          "tesoreriaEstadoId": 10,
                          "facultadId": 1,
                          "personaId": 1001,
                          "documentoId": 2,
                          "deuda": 1500.50,
                          "manual": 1,
                          "importado": 1,
                          "observaciones": "obs",
                          "fechaTope": "2026-08-01T00:00:00Z",
                          "uuid": "abc"
                        }
                        """, MediaType.APPLICATION_JSON));

        var result = consumer.findByUnique(1, new BigDecimal("1001"), 2);

        assertThat(result).isPresent();
        var domain = result.get();
        assertThat(domain.getTesoreriaEstadoId()).isEqualTo(10L);
        assertThat(domain.getFacultadId()).isEqualTo(1);
        assertThat(domain.getPersonaId()).isEqualByComparingTo(new BigDecimal("1001"));
        assertThat(domain.getDocumentoId()).isEqualTo(2);
        assertThat(domain.getDeuda()).isEqualByComparingTo(new BigDecimal("1500.50"));
        assertThat(domain.getManual()).isEqualTo((byte) 1);
        assertThat(domain.getImportado()).isEqualTo((byte) 1);
        assertThat(domain.getObservaciones()).isEqualTo("obs");
        assertThat(domain.getFechaTope()).isEqualTo(OffsetDateTime.parse("2026-08-01T00:00:00Z"));
        assertThat(domain.getUuid()).isEqualTo("abc");
        server.verify();
    }

    @Test
    void findByUnique_whenNotFound_returnsEmpty() {
        when(urlResolver.getBaseUrl(1)).thenReturn("http://facultad:8080");

        server.expect(requestTo(UNIQUE_URL))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var result = consumer.findByUnique(1, new BigDecimal("1001"), 2);

        assertThat(result).isEmpty();
        server.verify();
    }

    @Test
    void findByUnique_whenEmptyBody_returnsEmpty() {
        when(urlResolver.getBaseUrl(1)).thenReturn("http://facultad:8080");

        server.expect(requestTo(UNIQUE_URL))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        var result = consumer.findByUnique(1, new BigDecimal("1001"), 2);

        assertThat(result).isEmpty();
        server.verify();
    }

    @Test
    void findByUnique_whenServerError_throws() {
        when(urlResolver.getBaseUrl(1)).thenReturn("http://facultad:8080");

        server.expect(requestTo(UNIQUE_URL))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> consumer.findByUnique(1, new BigDecimal("1001"), 2))
                .isInstanceOf(HttpServerErrorException.class);
        server.verify();
    }
}

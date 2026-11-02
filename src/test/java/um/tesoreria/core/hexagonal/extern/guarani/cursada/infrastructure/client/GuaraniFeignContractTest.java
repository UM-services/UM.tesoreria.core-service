package um.tesoreria.core.hexagonal.extern.guarani.cursada.infrastructure.client;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import um.tesoreria.core.client.tesoreria.guarani.GuaraniCursadaClient;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;
import static org.assertj.core.api.Assertions.assertThat;

class GuaraniFeignContractTest {
    @Configuration(proxyBeanMethods = false)
    @EnableFeignClients(clients = GuaraniCursadaClient.class)
    static class Config {}

    // protects=el bean Feign real manda documento y año por la ruta B1 y decodifica su fixture;
    // fails_when=nombre, path, parámetros o conversor JSON no permiten consumir B1;
    // no_claim=servidor B1 simulado; no prueba Consul ni bases reales.
    @Test
    void springFeignConsumesB1Fixture() throws Exception {
        var received = new AtomicReference<String>();
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        byte[] fixture;
        try (var in = getClass().getResourceAsStream("/guaraniCursada/contrato-b1.json")) {
            fixture = in.readAllBytes();
        }
        server.createContext("/", exchange -> {
            received.set(exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, fixture.length);
            try (var out = exchange.getResponseBody()) { out.write(fixture); }
        });
        server.start();
        try {
            new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(
                    JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class,
                    FeignAutoConfiguration.class)).withUserConfiguration(Config.class)
                    .withPropertyValues("spring.cloud.openfeign.client.config.guaraniCursadaClient.url=http://localhost:"
                            + server.getAddress().getPort()).run(context -> {
                        assertThat(context).hasNotFailed();
                        var adapter = new FeignCursadaGuaraniAdapter(context.getBean(GuaraniCursadaClient.class));
                        assertThat(adapter.findByDocumentoAndAnioAcademico("99999999", 2026, 12))
                                .singleElement().satisfies(i -> {
                                    assertThat(i.planVersion()).isEqualTo(7);
                                    assertThat(i.materias()).singleElement().satisfies(m ->
                                            assertThat(m.actividadCodigo()).isEqualTo("ACT-1"));
                                });
                    });
            assertThat(received).hasValue("GET /api/tesoreria/guarani/cursada/documento/99999999/anioAcademico/2026");
        } finally { server.stop(0); }
    }
}

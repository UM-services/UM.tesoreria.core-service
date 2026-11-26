package um.tesoreria.core.audit;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Las consultas POST de solo lectura de la lista no dejan evento REQUEST si no cambian filas. Todo lo demás,
 * incluidas las escrituras cuya ruta termina en "/consulta", se sigue auditando con su actor.
 */
class AuditRequestFilterConsultaTest {

    final AuditOutboxWriter writer = mock(AuditOutboxWriter.class);
    final AuditRequestFilter filter = new AuditRequestFilter(new AuditProperties(), writer);

    void ejecutar(String method, String path, FilterChain chain) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.addHeader("X-User-Id", "7");
        filter.doFilter(request, new MockHttpServletResponse(), chain);
    }

    @ParameterizedTest
    @CsvSource({
            "POST,/balance/mayor/consulta",
            "POST,/api/tesoreria/core/balance/sumasysaldos/consulta",
            "POST,/movimientoConsulta/libroDiario",
            "POST,/movimientoConsulta/noCuadrados",
            "POST,/api/tesoreria/core/movimientoConsulta/libroDiario",
            "POST,/api/tesoreria/core/movimientoConsulta/noCuadrados"})
    void consultaSinCambiosNoDejaEventoRequest(String method, String path) throws Exception {
        ejecutar(method, path, (req, res) -> { });

        verify(writer, never()).writeInNewTransaction(anyList());
    }

    @Test
    void consultaQueCambiaFilasSeAuditaConSuActor() throws Exception {
        String[] actor = new String[1];
        ejecutar("POST", "/balance/sumasysaldos/consulta", (req, res) -> {
            AuditContext.incrementChanges();
            actor[0] = AuditContext.actorType();
        });

        verify(writer).writeInNewTransaction(anyList());
        assertThat(actor[0]).isEqualTo("USER");
    }

    @ParameterizedTest
    @CsvSource({
            "DELETE,/chequera/delete/1/2/3/consulta",
            "PUT,/paypertic/consulta",
            "POST,/movimientoConsulta/otraCosa",
            "DELETE,/movimientoConsulta/libroDiario"})
    void escriturasYRutasFueraDeLaListaSeAuditan(String method, String path) throws Exception {
        String[] actor = new String[1];
        ejecutar(method, path, (req, res) -> actor[0] = AuditContext.actorType());

        verify(writer).writeInNewTransaction(anyList());
        assertThat(actor[0]).as("el contexto de auditoría existe durante el pedido").isEqualTo("USER");
    }

    @Test
    void sinEventosDeCambioNoSePuedeSaberSiCambioFilasYSeAudita() throws Exception {
        // Con change-events apagado el listener no cuenta filas: changes == 0 no prueba que fue sólo lectura.
        AuditProperties properties = new AuditProperties();
        properties.setChangeEvents(false);
        AuditOutboxWriter otroWriter = mock(AuditOutboxWriter.class);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/balance/sumasysaldos/consulta");

        new AuditRequestFilter(properties, otroWriter).doFilter(request, new MockHttpServletResponse(), (req, res) -> { });

        verify(otroWriter).writeInNewTransaction(anyList());
    }
}

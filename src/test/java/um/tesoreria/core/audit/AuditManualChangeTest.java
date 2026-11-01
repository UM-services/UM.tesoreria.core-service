package um.tesoreria.core.audit;

import jakarta.persistence.EntityManager;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Contrato de {@link AuditManualChange}: escribe un CHANGE con before/after enmascarado por la
 * misma vía que los listeners de Hibernate.
 */
class AuditManualChangeTest {

    private AuditProperties properties;
    private AuditBuffer buffer;
    private AuditOutboxWriter writer;
    private EntityManager entityManager;
    private SessionImplementor session;
    private AuditManualChange auditManual;

    @BeforeEach
    void setUp() {
        properties = new AuditProperties();
        properties.setMode(AuditProperties.Mode.SEND);
        properties.setChangeEvents(true);

        buffer = new AuditBuffer();
        writer = mock(AuditOutboxWriter.class);
        entityManager = mock(EntityManager.class);
        session = mock(SessionImplementor.class);
        when(entityManager.unwrap(SessionImplementor.class)).thenReturn(session);

        auditManual = new AuditManualChange(properties,
                new AuditDiffBuilder(properties, new ObjectMapper()),
                buffer, writer, entityManager);
    }

    @Test
    void emiteChangeConBeforeAfterYEnmascaraCamposSensibles() {
        auditManual.change(AuditManualChange.Change.of(
                "CompraPedidoSecuencia", "compra_pedido_secuencia", "1/2026",
                AuditDiffBuilder.Operation.UPDATE,
                Map.of("cbu", "1234567890123456"),
                Map.of("cbu", "1234567890123457"),
                Map.of("operacion", "reserva-correlativo")));

        ArgumentCaptor<List<AuditEventPayload>> captor = captorDeEventos();
        verify(writer).write(eq(session), captor.capture());

        AuditEventPayload payload = captor.getValue().getFirst();
        assertThat(payload.getLevel()).isEqualTo("CHANGE");
        assertThat(payload.getOperation()).isEqualTo("UPDATE");
        assertThat(payload.getEntity()).isEqualTo("CompraPedidoSecuencia");
        assertThat(payload.getEntityId()).isEqualTo("1/2026");
        assertThat(payload.getChangedFields()).containsExactly("cbu");
        assertThat(payload.getBeforeData()).containsEntry("cbu", "****3456");
        assertThat(payload.getAfterData()).containsEntry("cbu", "****3457");
        assertThat(payload.getMetadata())
                .containsEntry("source", "manual")
                .containsEntry("operacion", "reserva-correlativo");
        assertThat(buffer.size()).isZero();
    }

    @Test
    void unCambioSinCamposDistintosNoEmiteNada() {
        auditManual.change(AuditManualChange.Change.of(
                "CompraPedidoSecuencia", "compra_pedido_secuencia", "1/2026",
                AuditDiffBuilder.Operation.UPDATE, Map.of(), Map.of(), Map.of()));

        verify(writer, never()).write(eq(session), org.mockito.ArgumentMatchers.anyList());
        assertThat(buffer.size()).isZero();
    }

    @Test
    void conAuditoriaApagadaNoEmiteNada() {
        properties.setMode(AuditProperties.Mode.OFF);

        auditManual.change(AuditManualChange.Change.scalar(
                "CompraPedidoSecuencia", "compra_pedido_secuencia", "1/2026",
                "ultimoNumero", 1, 2, Map.of()));

        verify(writer, never()).write(eq(session), org.mockito.ArgumentMatchers.anyList());
        assertThat(buffer.size()).isZero();
    }

    @Test
    void elPrimerCorrelativoSeRegistraComoInsert() {
        auditManual.change(AuditManualChange.Change.inserted(
                "CompraPedidoSecuencia", "compra_pedido_secuencia", "1/2026",
                Map.of("ultimoNumero", 1), Map.of()));

        ArgumentCaptor<List<AuditEventPayload>> captor = captorDeEventos();
        verify(writer).write(eq(session), captor.capture());

        AuditEventPayload payload = captor.getValue().getFirst();
        assertThat(payload.getOperation()).isEqualTo("INSERT");
        assertThat(payload.getAfterData()).containsEntry("ultimoNumero", 1);
    }

    @Test
    void elEnmascaramientoAcotadoUsaLaEntidadDelCambio() {
        properties.setMaskFields(List.of("Usuario.clave"));

        auditManual.change(AuditManualChange.Change.inserted(
                "PermisoEntity", "permiso", 41L,
                Map.of("clave", "compras.estimar"), Map.of()));
        auditManual.change(AuditManualChange.Change.inserted(
                "UsuarioEntity", "usuario", 7L,
                Map.of("clave", "secreto"), Map.of()));

        ArgumentCaptor<List<AuditEventPayload>> captor = captorDeEventos();
        verify(writer, times(2)).write(eq(session), captor.capture());

        List<AuditEventPayload> eventos = captor.getAllValues().stream()
                .flatMap(List::stream)
                .toList();
        assertThat(eventos.get(0).getAfterData()).containsEntry("clave", "compras.estimar");
        assertThat(eventos.get(1).getAfterData()).containsEntry("clave", "****");
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<AuditEventPayload>> captorDeEventos() {
        return ArgumentCaptor.forClass(List.class);
    }
}

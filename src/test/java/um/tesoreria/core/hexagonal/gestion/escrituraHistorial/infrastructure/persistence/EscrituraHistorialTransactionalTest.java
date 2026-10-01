package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service.EscrituraHistorialService;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraOperacion;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out.EscrituraHistorialRepository;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.adapter.JpaEscrituraHistorialRepositoryAdapter;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper.EscrituraHistorialMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.repository.JpaEscrituraHistorialRepository;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization.JacksonEscrituraValorSerializer;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prueba el uso transaccional del contrato: alta/edición/baja producen eventos
 * ligados a la misma clave; una TX revertida no deja evento.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        EscrituraHistorialService.class,
        JpaEscrituraHistorialRepositoryAdapter.class,
        EscrituraHistorialMapper.class,
        JacksonEscrituraValorSerializer.class
})
class EscrituraHistorialTransactionalTest {

    @Autowired
    private RegistrarEscrituraHistorialUseCase historial;

    @Autowired
    private EscrituraHistorialRepository escrituraHistorialRepository;

    @Autowired
    private JpaEscrituraHistorialRepository jpaRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void altaEdicionYBaja_producenEventosConAntesDespuesCorrectosYMismaClave() {
        historial.registrarAlta("ejercicio", "99", Map.of("nombre", "2026"));
        historial.registrarEdicion("ejercicio", "99",
                Map.of("nombre", "2026"), Map.of("nombre", "2026-bis"));
        historial.registrarBaja("ejercicio", "99", Map.of("nombre", "2026-bis"));

        var eventos = escrituraHistorialRepository
                .findAllByEntidadAndEntidadClaveOrderByFechaAscEscrituraHistorialIdAsc("ejercicio", "99");

        assertThat(eventos).hasSize(3);
        assertThat(eventos).allSatisfy(e -> {
            assertThat(e.getEntidad()).isEqualTo("ejercicio");
            assertThat(e.getEntidadClave()).isEqualTo("99");
            assertThat(e.getFecha()).isNotNull();
            assertThat(e.getEscrituraHistorialId()).isNotNull();
        });

        assertThat(eventos.get(0).getOperacion()).isEqualTo(EscrituraOperacion.ALTA);
        assertThat(eventos.get(0).getValorAnterior()).isEmpty();
        assertThat(eventos.get(0).getValorNuevo()).contains("2026");

        assertThat(eventos.get(1).getOperacion()).isEqualTo(EscrituraOperacion.EDICION);
        assertThat(eventos.get(1).getValorAnterior()).contains("2026");
        assertThat(eventos.get(1).getValorNuevo()).contains("2026-bis");

        assertThat(eventos.get(2).getOperacion()).isEqualTo(EscrituraOperacion.BAJA);
        assertThat(eventos.get(2).getValorAnterior()).contains("2026-bis");
        assertThat(eventos.get(2).getValorNuevo()).isEmpty();
    }

    @Test
    void operacionRevertida_noDejaEvento() {
        var requiresNew = new TransactionTemplate(transactionManager);
        requiresNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        assertThatThrownBy(() -> requiresNew.executeWithoutResult(status -> {
            historial.registrarAlta("cuenta", "rollback-1", Map.of("numero", "1.1.1"));
            throw new IllegalStateException("negocio rechazado");
        })).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("negocio rechazado");

        assertThat(jpaRepository.findAllByEntidadAndEntidadClaveOrderByFechaAscEscrituraHistorialIdAsc(
                "cuenta", "rollback-1")).isEmpty();
    }
}

package um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.context.annotation.Import;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.entity.TipoChequeraEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Traducción clase → tipos de chequera que consume el filtro por asignaciones de
 * `ChequerasPorUsuarioAsignadasService` (nunca cubierta a nivel SQL hasta acá).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaTipoChequeraClaseQueryTest.TestAuditingConfig.class)
class JpaTipoChequeraClaseQueryTest {

    @EnableJpaAuditing
    static class TestAuditingConfig {}

    @Autowired
    private JpaTipoChequeraRepository repository;

    @Test
    void returnsOnlyTheTypesOfTheAssignedClases() {
        repository.save(tipo(1, 1));
        repository.save(tipo(2, 2));
        repository.save(tipo(3, 2));

        var types = repository.findAllByClaseChequeraIdIn(List.of(2));

        assertThat(types).hasSize(2);
        assertThat(types).allSatisfy(t -> assertThat(t.getClaseChequeraId()).isEqualTo(2));
        assertThat(types).extracting(TipoChequeraEntity::getTipoChequeraId).containsExactlyInAnyOrder(2, 3);
    }

    @Test
    void emptyClasesYieldNoTypes() {
        repository.save(tipo(1, 1));

        assertThat(repository.findAllByClaseChequeraIdIn(List.of())).isEmpty();
    }

    private TipoChequeraEntity tipo(int tipoChequeraId, int claseChequeraId) {
        return TipoChequeraEntity.builder()
                .tipoChequeraId(tipoChequeraId)
                .nombre("tipo " + tipoChequeraId)
                .claseChequeraId(claseChequeraId)
                .geograficaId(1)
                .build();
    }
}

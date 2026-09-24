package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaChequeraSerieUsuarioQueryTest.TestAuditingConfig.class)
class JpaChequeraSerieUsuarioQueryTest {

    @EnableJpaAuditing
    static class TestAuditingConfig {}

    @Autowired
    private JpaChequeraSerieRepository repository;

    @Test
    void filtersByAssignedFacultiesAndLectivoBeforePagination() {
        save(2, 2026, 100L, "123", 1);
        save(3, 2026, 101L, "124", 1);
        save(4, 2026, 102L, "125", 1);
        save(2, 2025, 103L, "126", 1);

        var page = repository.findAllByLectivoIdAndFacultadIdIn(2026, List.of(2, 3),
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "chequeraId")));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().getFacultadId()).isEqualTo(3);
    }

    @Test
    void filtersStudentByBothDocumentNumberAndType() {
        save(2, 2026, 100L, "123", 1);
        save(2, 2026, 101L, "123", 2);
        save(2, 2026, 102L, "456", 1);

        var page = repository.findAllByLectivoIdAndFacultadIdInAndPersonaIdAndDocumentoId(
                2026, List.of(2), new BigDecimal("123"), 1,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "chequeraId")));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst().getChequeraSerieId()).isEqualTo(100L);
    }

    private void save(int facultadId, int lectivoId, long serieId, String personaId, int documentoId) {
        repository.save(ChequeraSerieEntity.builder()
                .facultadId(facultadId)
                .tipoChequeraId(1)
                .chequeraSerieId(serieId)
                .personaId(new BigDecimal(personaId))
                .documentoId(documentoId)
                .lectivoId(lectivoId)
                .alternativaId(1)
                .build());
    }
}

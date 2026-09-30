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

    @Test
    void excludesChequerasOfClasesNotAssignedWhenOnlyGradoIsEnabled() {
        // Escenario del bug report: alumno con chequera tipo 1 (PreUniversitario, clase 1) y
        // tipo 2 (Grado, clase 2) en la misma facultad/sede; el usuario tiene sólo clase 2.
        // Distinto documento por fila: el H2 generado pone unique (chs_per_id, chs_doc_id)
        // por el @OneToOne a Persona, que el esquema real de MySQL no tiene.
        saveDimensioned(25, 2026, 11164L, "47661982", 1, 1, 1);
        saveDimensioned(25, 2026, 16960L, "47661982", 2, 2, 1);

        var page = repository.findAllByLectivoIdAndFacultadIdInAndGeograficaIdInAndTipoChequeraIdIn(
                2026, List.of(25), List.of(1), List.of(2),
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "chequeraId")));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).allSatisfy(chequera ->
                assertThat(chequera.getTipoChequeraId()).isEqualTo(2));
    }

    @Test
    void showsBothClasesWhenBothAreAssigned() {
        saveDimensioned(25, 2026, 11164L, "47661982", 1, 1, 1);
        saveDimensioned(25, 2026, 16960L, "47661982", 2, 2, 1);

        var page = repository.findAllByLectivoIdAndFacultadIdInAndGeograficaIdInAndTipoChequeraIdIn(
                2026, List.of(25), List.of(1), List.of(1, 2),
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "chequeraId")));

        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void excludesSedesNotAssignedAndChequerasWithoutSede() {
        saveDimensioned(25, 2026, 11164L, "47661982", 1, 2, 1);
        saveDimensioned(25, 2026, 16960L, "47661982", 2, 2, 5);
        saveDimensioned(25, 2026, 16961L, "47661982", 3, 2, null);

        var assignedSedeOnly = repository.findAllByLectivoIdAndFacultadIdInAndGeograficaIdInAndTipoChequeraIdIn(
                2026, List.of(25), List.of(1), List.of(2),
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "chequeraId")));

        assertThat(assignedSedeOnly.getContent()).hasSize(1);
        assertThat(assignedSedeOnly.getContent().getFirst().getChequeraSerieId()).isEqualTo(11164L);
    }

    @Test
    void studentFilterCombinesAllThreeDimensions() {
        saveDimensioned(25, 2026, 11164L, "47661982", 1, 1, 1);
        saveDimensioned(25, 2026, 16960L, "47661982", 2, 2, 1);
        saveDimensioned(25, 2026, 16962L, "99999999", 1, 2, 1);

        var page = repository
                .findAllByLectivoIdAndFacultadIdInAndGeograficaIdInAndTipoChequeraIdInAndPersonaIdAndDocumentoId(
                        2026, List.of(25), List.of(1), List.of(2), new BigDecimal("47661982"), 2,
                        PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "chequeraId")));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst().getChequeraSerieId()).isEqualTo(16960L);
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

    /** Fila completa de chequera_serie con las tres dimensiones que usa el filtro por asignaciones. */
    private void saveDimensioned(int facultadId, int lectivoId, long serieId, String personaId,
                                 int documentoId, int tipoChequeraId, Integer geograficaId) {
        repository.save(ChequeraSerieEntity.builder()
                .facultadId(facultadId)
                .tipoChequeraId(tipoChequeraId)
                .chequeraSerieId(serieId)
                .personaId(new BigDecimal(personaId))
                .documentoId(documentoId)
                .lectivoId(lectivoId)
                .geograficaId(geograficaId)
                .alternativaId(1)
                .build());
    }
}

package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.PeriodoCursada.*;

class AgrupadorMateriasTest {

    // protects=all control subjects and their ordered groups match the contract;
    // fails_when=a subject, name or group changes;
    // no_claim=live faculty data or period translation.
    @Test
    void matchesControlFixture() throws Exception {
        var mapper = new ObjectMapper();
        JsonNode expected;
        try (var input = getClass().getResourceAsStream("/materiasInscriptas/materias-control.json")) {
            expected = mapper.readTree(input).get(0).get("cursos");
        }
        var materias = new ArrayList<MateriaAgrupable>();
        for (var curso : expected) {
            for (var periodo : curso.get("periodos")) {
                for (var materia : periodo.get("materias")) {
                    materias.add(new MateriaAgrupable(1, 9, materia.get("materiaId").asText(),
                            materia.get("nombre").asText(), curso.get("curso").asInt(),
                            PeriodoCursada.valueOf(periodo.get("periodo").asText())));
                }
            }
        }
        assertThat(materias).hasSize(13);
        Collections.reverse(materias);
        JsonNode actual = mapper.valueToTree(AgrupadorMaterias.agrupar(materias));
        assertThat(actual).isEqualTo(expected);
    }

    // protects=course, period and subject order without empty groups or input mutation;
    // fails_when=input order controls output or unused periods appear;
    // no_claim=faculty mapping.
    @Test
    void sortsCoursesPeriodsAndSubjectIds() {
        var input = List.of(materia("20", 5, SIN_PERIODO), materia("10", 2, SEGUNDO_SEMESTRE),
                materia("09", 5, SEGUNDO_SEMESTRE), materia("08", 5, PRIMER_SEMESTRE),
                materia("02", 5, ANUAL), materia("001", 5, ANUAL));
        assertThat(AgrupadorMaterias.agrupar(input)).containsExactly(
                new CursoMaterias(2, List.of(periodo(SEGUNDO_SEMESTRE, "10"))),
                new CursoMaterias(5, List.of(periodo(ANUAL, "001", "02"),
                        periodo(PRIMER_SEMESTRE, "08"), periodo(SEGUNDO_SEMESTRE, "09"),
                        periodo(SIN_PERIODO, "20"))));
        assertThat(input).extracting(MateriaAgrupable::materiaId).containsExactly("20", "10", "09", "08", "02", "001");
    }

    // protects=missing course or period moves subjects to the final unknown course;
    // fails_when=a known period survives without a course or a null period keeps its course;
    // no_claim=source conversion of null values.
    @Test
    void groupsMissingCourseOrPeriodLast() {
        assertThat(AgrupadorMaterias.agrupar(List.of(materia("03", null, ANUAL),
                materia("02", 4, null), materia("04", null, null), materia("01", 1, ANUAL))))
                .containsExactly(new CursoMaterias(1, List.of(periodo(ANUAL, "01"))),
                        new CursoMaterias(null, List.of(periodo(SIN_PERIODO, "02", "03", "04"))));
    }

    // protects=a translated SIN_PERIODO stays in its course after the known periods;
    // fails_when=SIN_PERIODO moves to the unknown course or precedes a known period;
    // no_claim=translation of faculty code 3.
    @Test
    void keepsSinPeriodoInItsCourse() {
        assertThat(AgrupadorMaterias.agrupar(List.of(materia("02", 4, SIN_PERIODO), materia("01", 4, ANUAL))))
                .containsExactly(new CursoMaterias(4, List.of(periodo(ANUAL, "01"), periodo(SIN_PERIODO, "02"))));
    }

    // protects=a subject remains visible when its name is absent;
    // fails_when=a null name drops the subject or becomes text;
    // no_claim=name lookup.
    @Test
    void preservesNullName() {
        assertThat(AgrupadorMaterias.agrupar(List.of(new MateriaAgrupable(1, 9, "01", null, 1, ANUAL))))
                .containsExactly(new CursoMaterias(1,
                        List.of(new PeriodoMaterias(ANUAL, List.of(new MateriaInscripta("01", null))))));
    }

    // protects=deduplication uses faculty, plan and subject across all groups;
    // fails_when=the same key appears twice or different faculties collapse;
    // no_claim=which duplicate is correct in faculty data.
    @Test
    void deduplicatesFullKeyAcrossGroups() {
        var first = materia("01", 1, ANUAL);
        assertThat(AgrupadorMaterias.agrupar(List.of(first, first, materia("01", 2, SEGUNDO_SEMESTRE),
                new MateriaAgrupable(2, 9, "01", "Subject 01", 1, ANUAL))))
                .containsExactly(new CursoMaterias(1, List.of(periodo(ANUAL, "01", "01"))));
    }

    // protects=the same subject identifier in different plans remains twice;
    // fails_when=deduplication omits the plan;
    // no_claim=plan lookup.
    @Test
    void keepsSameSubjectInDifferentPlans() {
        assertThat(AgrupadorMaterias.agrupar(List.of(materia("01", 1, ANUAL),
                new MateriaAgrupable(1, 10, "01", "Other name", 1, ANUAL))))
                .containsExactly(new CursoMaterias(1, List.of(new PeriodoMaterias(ANUAL,
                        List.of(new MateriaInscripta("01", "Subject 01"), new MateriaInscripta("01", "Other name"))))));
    }

    // protects=empty input produces no groups;
    // fails_when=an empty course or period is created;
    // no_claim=missing enrollment handling.
    @Test
    void returnsEmptyListForEmptyInput() {
        assertThat(AgrupadorMaterias.agrupar(List.of())).isEmpty();
    }

    private static MateriaAgrupable materia(String id, Integer curso, PeriodoCursada periodo) {
        return new MateriaAgrupable(1, 9, id, "Subject " + id, curso, periodo);
    }

    private static PeriodoMaterias periodo(PeriodoCursada periodo, String... ids) {
        return new PeriodoMaterias(periodo, java.util.Arrays.stream(ids)
                .map(id -> new MateriaInscripta(id, "Subject " + id)).toList());
    }
}

package um.tesoreria.core.hexagonal.extern.guarani.cursada.infrastructure.client;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import um.tesoreria.core.client.tesoreria.guarani.*;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FeignCursadaGuaraniAdapterTest {
    private final GuaraniCursadaClient client = mock(GuaraniCursadaClient.class);
    private final FeignCursadaGuaraniAdapter adapter = new FeignCursadaGuaraniAdapter(client);

    // protects=lee el JSON sintético de B1 y conserva campos de inscripción y materia;
    // fails_when=el DTO pierde o cambia un campo del contrato;
    // no_claim=no consulta Guaraní ni prueba el transporte Feign.
    @Test
    void mapsTheB1ContractFixture() throws Exception {
        try (var in = getClass().getResourceAsStream("/guaraniCursada/contrato-b1.json")) {
            var rows = JsonMapper.builder().build().readValue(in, CursadaGuaraniResponse[].class);
            when(client.findByDocumentoAndAnioAcademico("99999999", 2026)).thenReturn(List.of(rows));
            assertThat(adapter.findByDocumentoAndAnioAcademico("99999999", 2026, 12)).singleElement().satisfies(i -> {
                assertThat(i.propuesta()).isEqualTo(345);
                assertThat(i.planVersion()).isEqualTo(7);
                assertThat(i.carrera()).isEqualTo("Propuesta sintética");
                assertThat(i.cursoInscripcion()).isEqualTo(2);
                assertThat(i.materias()).singleElement().satisfies(m -> {
                    assertThat(m.actividadCodigo()).isEqualTo("ACT-1");
                    assertThat(m.nombre()).isEqualTo("Actividad sintética");
                    assertThat(m.curso()).isEqualTo(1);
                    assertThat(m.periodo()).isEqualTo("SEGUNDO_SEMESTRE");
                    assertThat(m.periodoLectivo()).isEqualTo(4321);
                });
            });
        }
    }

    // protects=filtra por igualdad y ordena propuesta y versión sin mezclar inscripciones;
    // fails_when=entran otras responsables o null, o mezcla versiones;
    // no_claim=no verifica las asignaciones reales de facultad.
    @Test
    void filtersAndOrdersByProposalAndVersion() {
        when(client.findByDocumentoAndAnioAcademico("99999999", 2026)).thenReturn(List.of(
                row(12, 20, 3), row(13, 10, 1), row(null, 10, 1), row(12, 10, 2), row(12, 10, 1)));
        assertThat(adapter.findByDocumentoAndAnioAcademico("99999999", 2026, 12))
                .extracting(i -> i.propuesta() + "/" + i.planVersion()).containsExactly("10/1", "10/2", "20/3");
    }

    // protects=sin responsable no llama; sin coincidencias devuelve lista vacía;
    // fails_when=null consulta todas las facultades o crea una inscripción ficticia;
    // no_claim=no verifica el estado SIN_INSCRIPCION del use case.
    @Test
    void absentResponsableOrNoMatchIsEmpty() {
        assertThat(adapter.findByDocumentoAndAnioAcademico("99999999", 2026, null)).isEmpty();
        verifyNoInteractions(client);
        when(client.findByDocumentoAndAnioAcademico("99999999", 2026)).thenReturn(List.of(row(13, 10, 1)));
        assertThat(adapter.findByDocumentoAndAnioAcademico("99999999", 2026, 12)).isEmpty();
    }

    // protects=anioPlan y anioCursada null se conservan; OTRO llega sin inventar un período;
    // fails_when=se rellenan años nulos o se transforma OTRO en anual;
    // no_claim=no prueba el agrupado del consumidor.
    @Test
    void preservesNullableYearsAndOtherPeriod() {
        when(client.findByDocumentoAndAnioAcademico("99999999", 2026)).thenReturn(List.of(row(12, 10, 1)));
        assertThat(adapter.findByDocumentoAndAnioAcademico("99999999", 2026, 12)).singleElement().satisfies(i -> {
            assertThat(i.cursoInscripcion()).isNull();
            assertThat(i.materias()).singleElement().satisfies(m -> {
                assertThat(m.curso()).isNull();
                assertThat(m.periodo()).isEqualTo("OTRO");
            });
        });
    }

    // protects=la falla remota se propaga al límite de la fuente;
    // fails_when=el adaptador la oculta como ausencia de inscripción;
    // no_claim=no prueba fallas reales de red.
    @Test
    void propagatesRemoteFailure() {
        when(client.findByDocumentoAndAnioAcademico("99999999", 2026)).thenThrow(new IllegalStateException("fallo"));
        assertThatThrownBy(() -> adapter.findByDocumentoAndAnioAcademico("99999999", 2026, 12))
                .isInstanceOf(IllegalStateException.class);
    }

    private CursadaGuaraniResponse row(Integer responsable, Integer propuesta, Integer version) {
        return new CursadaGuaraniResponse(responsable, propuesta, "Propuesta sintética", version,
                "ACT-1", "Actividad sintética", null, "OTRO", 4321, "Otro", null);
    }
}

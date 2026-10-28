package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.PeriodoCursada;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.MateriaCursada;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.dto.PendienteInfoFacultadResponse;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.dto.PendienteInfoFacultadResponse.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class PendienteInfoFacultadMapperTest {

    private static final String CONTROL = "/pendienteinfo/control.json";
    private static final String CONTRATO = "/materiasInscriptas/materias-control.json";

    private final JsonMapper json = JsonMapper.builder().build();
    private final PendienteInfoFacultadMapper mapper = new PendienteInfoFacultadMapper();

    private static final NombreMateriaResolver SIN_BUSQUEDA = (fac, plan, materia) -> {
        throw new AssertionError("No debía buscar el nombre de " + plan + "/" + materia);
    };

    // protects=control.json pasa por el mapper y da curso 5, plan, carrera, observaciones y las 13 materias
    //   con el curso, el período y el nombre del contrato publicado en #481;
    // fails_when=falta una materia, o el curso, el período o el nombre no coincide con el contrato;
    // no_claim=que la facultad real responda así hoy: control.json es una copia reducida de una respuesta de dev.
    @Test
    void controlFixtureGivesTheThirteenSubjectsOfTheContract() throws IOException {
        InscripcionMaterias result = mapper.toDomain(read(CONTROL), SIN_BUSQUEDA).orElseThrow();

        assertThat(result.curso()).isEqualTo(5);
        assertThat(result.plan()).isEqualTo("Plan 2016");
        assertThat(result.carrera()).isEqualTo("Ingeniería en Informática");
        assertThat(result.observaciones()).isEqualTo("Texto libre de la inscripción");
        assertThat(result.materias()).hasSize(13);
        assertThat(resumen(result.materias())).isEqualTo(esperadoDelContrato());
    }

    // protects=una materia que no figura en materiascarrera sale con curso y período null, aunque materiascurso
    //   tenga una fila con su clave en otra carrera;
    // fails_when=el mapper toma curso o período de otra materia o de otra carrera;
    // no_claim=el orden de las materias ni su nombre.
    @Test
    void subjectOutsideMateriasCarreraHasNoCursoNorPeriodo() {
        var response = response(5, 1, 2, "obs",
                List.of(detalle(1, 9, "A001"), detalle(1, 9, "B002")),
                List.of(materia(1, 9, "A001", "UNO"), materia(1, 9, "B002", "DOS")),
                List.of(new MateriaCarrera(1, 9, 2, "A001")),
                List.of(new MateriaCurso(1, 9, 2, "A001", 3, 1),
                        new MateriaCurso(1, 9, 2, "B002", 4, 2),
                        new MateriaCurso(1, 9, 7, "A001", 1, 0)));

        var materias = mapper.toDomain(response, SIN_BUSQUEDA).orElseThrow().materias();

        assertThat(materias).extracting(MateriaCursada::materiaId, MateriaCursada::curso, MateriaCursada::periodo)
                .containsExactly(tuple("A001", 3, 1),
                        tuple("B002", null, null));
    }

    // protects=con detalles y materiascurso vacía, todas las materias salen con curso y período null;
    // fails_when=el mapper inventa un curso o falla con la sección vacía o ausente;
    // no_claim=que la facultad mande la sección vacía en la práctica.
    @Test
    void emptyMateriasCursoLeavesEveryCursoAndPeriodoNull() {
        var vacia = response(5, 1, 2, "obs", List.of(detalle(1, 9, "A001"), detalle(1, 9, "B002")),
                List.of(materia(1, 9, "A001", "UNO"), materia(1, 9, "B002", "DOS")),
                List.of(new MateriaCarrera(1, 9, 2, "A001")), List.of());
        var ausente = response(5, 1, 2, "obs", List.of(detalle(1, 9, "A001")),
                List.of(materia(1, 9, "A001", "UNO")), null, null);

        assertThat(mapper.toDomain(vacia, SIN_BUSQUEDA).orElseThrow().materias())
                .hasSize(2).allSatisfy(m -> assertThat(m.curso()).isNull())
                .allSatisfy(m -> assertThat(m.periodo()).isNull());
        assertThat(mapper.toDomain(ausente, SIN_BUSQUEDA).orElseThrow().materias())
                .hasSize(1).allSatisfy(m -> assertThat(m.curso()).isNull());
    }

    // protects=T-a: observaciones vacía o solo espacios sale como null (regla del contrato de #481);
    // fails_when=se deja pasar "", "  " o un salto de línea como texto de la inscripción;
    // no_claim=texto real de la facultad: el fixture tiene texto inventado.
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n\t "})
    void blankObservacionesBecomesNull(String observaciones) {
        var response = response(5, 1, 2, observaciones, List.of(detalle(1, 9, "A001")),
                List.of(materia(1, 9, "A001", "UNO")), List.of(), List.of());

        assertThat(mapper.toDomain(response, SIN_BUSQUEDA).orElseThrow().observaciones()).isNull();
    }

    // protects=observaciones con texto se conserva sin cambios; observaciones null sale null;
    // fails_when=el mapper recorta, cambia o descarta el texto de la inscripción;
    // no_claim=el límite de largo del campo.
    @Test
    void observacionesWithTextIsKeptAsIs() {
        var conTexto = response(5, 1, 2, " Tiene beca ", List.of(detalle(1, 9, "A001")),
                List.of(materia(1, 9, "A001", "UNO")), List.of(), List.of());
        var nula = response(5, 1, 2, null, List.of(detalle(1, 9, "A001")),
                List.of(materia(1, 9, "A001", "UNO")), List.of(), List.of());

        assertThat(mapper.toDomain(conTexto, SIN_BUSQUEDA).orElseThrow().observaciones()).isEqualTo(" Tiene beca ");
        assertThat(mapper.toDomain(nula, SIN_BUSQUEDA).orElseThrow().observaciones()).isNull();
    }

    // protects=sin respuesta, sin inscripción o sin detalles el resultado es vacío;
    // fails_when=el mapper devuelve una inscripción con lista vacía de materias o lanza una excepción;
    // no_claim=que la facultad use null o lista vacía: se cubren las dos formas.
    @Test
    void noEnrollmentOrNoDetailsGivesEmpty() {
        var sinInscripcion = new PendienteInfoFacultadResponse(null, null, null,
                List.of(detalle(1, 9, "A001")), List.of(), List.of(), List.of());
        var sinDetalles = response(5, 1, 2, "obs", List.of(), List.of(), List.of(), List.of());
        var detallesNulos = response(5, 1, 2, "obs", null, List.of(), List.of(), List.of());

        assertThat(mapper.toDomain(null, SIN_BUSQUEDA)).isEmpty();
        assertThat(mapper.toDomain(sinInscripcion, SIN_BUSQUEDA)).isEmpty();
        assertThat(mapper.toDomain(sinDetalles, SIN_BUSQUEDA)).isEmpty();
        assertThat(mapper.toDomain(detallesNulos, SIN_BUSQUEDA)).isEmpty();
    }

    // protects=el nombre sale de materias[] por el plan del detalle, no de una materia con el mismo id en otro plan;
    // fails_when=el mapper busca el nombre solo por materiaId, o usa el plan de la inscripción;
    // no_claim=que el nombre de materias[] esté actualizado.
    @Test
    void nameComesFromMateriasByThePlanOfTheDetail() {
        var response = response(5, 1, 2, "obs", List.of(detalle(1, 9, "A001")),
                List.of(materia(1, 1, "A001", "OTRO PLAN"), materia(1, 9, "A001", "DEL PLAN")),
                List.of(), List.of());

        assertThat(mapper.toDomain(response, SIN_BUSQUEDA).orElseThrow().materias())
                .extracting(MateriaCursada::nombre).containsExactly("DEL PLAN");
    }

    // protects=decisión B: si materias[] no trae la materia, el nombre sale de detalles[].materia y no se busca;
    // fails_when=el mapper llama al resolvedor aunque el detalle ya traiga el nombre;
    // no_claim=que la facultad una el detalle con el plan correcto: lo verificó el equipo con datos reales de dev.
    @Test
    void nameFallsBackToTheDetailWithoutSearching() {
        var detalle = new Detalle(1, 4, "X900", new Materia(1, 4, "X900", "DEL DETALLE"));
        var response = response(5, 1, 2, "obs", List.of(detalle), List.of(), List.of(), List.of());

        assertThat(mapper.toDomain(response, SIN_BUSQUEDA).orElseThrow().materias())
                .extracting(MateriaCursada::nombre).containsExactly("DEL DETALLE");
    }

    // protects=materias[] manda sobre detalles[].materia cuando las dos traen nombre;
    // fails_when=el mapper invierte el orden de las fuentes del nombre;
    // no_claim=cuál de los dos nombres es el correcto en la facultad.
    @Test
    void materiasListWinsOverTheDetailName() {
        var detalle = new Detalle(1, 9, "A001", new Materia(1, 9, "A001", "DEL DETALLE"));
        var response = response(5, 1, 2, "obs", List.of(detalle),
                List.of(materia(1, 9, "A001", "DE MATERIAS")), List.of(), List.of());

        assertThat(mapper.toDomain(response, SIN_BUSQUEDA).orElseThrow().materias())
                .extracting(MateriaCursada::nombre).containsExactly("DE MATERIAS");
    }

    // protects=T-d: detalles[].materia con nombre vacío o en blanco cuenta como faltante y se pide al resolvedor;
    // fails_when=el mapper usa "" o "  " como nombre;
    // no_claim=que el resolvedor consiga el nombre en la práctica: aquí es un stub.
    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void blankDetailNameCountsAsMissing(String nombreVacio) {
        var detalle = new Detalle(1, 4, "X900", new Materia(1, 4, "X900", nombreVacio));
        var response = response(5, 1, 2, "obs", List.of(detalle), List.of(), List.of(), List.of());
        List<String> pedidos = new ArrayList<>();

        var materias = mapper.toDomain(response, (fac, plan, materia) -> {
            pedidos.add(plan + "/" + materia);
            return "BUSCADO";
        }).orElseThrow().materias();

        assertThat(pedidos).containsExactly("4/X900");
        assertThat(materias).extracting(MateriaCursada::nombre).containsExactly("BUSCADO");
    }

    // protects=el resolvedor recibe cada clave faltante una sola vez, en orden de plan y de materia,
    //   y una respuesta null o en blanco deja el nombre en null;
    // fails_when=se repite una clave, se pide en el orden de los detalles, o se guarda "" como nombre;
    // no_claim=el tope de 10 llamadas ni los 3 s: viven en el consumer y los prueba su test.
    @Test
    void resolverGetsEachMissingKeyOnceInPlanAndSubjectOrder() {
        var response = response(5, 1, 2, "obs",
                List.of(detalle(1, 7, "B"), detalle(1, 3, "Z"), detalle(1, 7, "A"), detalle(1, 3, "Z"), detalle(1, 3, "C")),
                List.of(), List.of(), List.of());
        List<String> pedidos = new ArrayList<>();

        var materias = mapper.toDomain(response, (fac, plan, materia) -> {
            pedidos.add(plan + "/" + materia);
            return materia.equals("C") ? "   " : materia.equals("Z") ? null : "N-" + materia;
        }).orElseThrow().materias();

        assertThat(pedidos).containsExactly("3/C", "3/Z", "7/A", "7/B");
        assertThat(materias).extracting(MateriaCursada::materiaId, MateriaCursada::nombre)
                .containsExactly(tuple("B", "N-B"),
                        tuple("Z", null),
                        tuple("A", "N-A"),
                        tuple("Z", null),
                        tuple("C", null));
    }

    // protects=una fila de materiascarrera de otra carrera no habilita el curso de la materia, aunque materiascurso
    //   tenga una fila con la carrera de la inscripción;
    // fails_when=el mapper filtra materiascarrera sin mirar carreraId, o cruza por carrera de otra tabla;
    // no_claim=que la facultad mande filas de otra carrera en la práctica.
    @Test
    void materiasCarreraRowOfAnotherCarreraDoesNotGiveCursoNorPeriodo() {
        var response = response(5, 1, 2, "obs", List.of(detalle(1, 9, "A001")),
                List.of(materia(1, 9, "A001", "UNO")),
                List.of(new MateriaCarrera(1, 9, 7, "A001")),
                List.of(new MateriaCurso(1, 9, 2, "A001", 3, 1)));

        assertThat(mapper.toDomain(response, SIN_BUSQUEDA).orElseThrow().materias())
                .extracting(MateriaCursada::curso, MateriaCursada::periodo).containsExactly(tuple(null, null));
    }

    // protects=una inscripción sin carreraId no cruza filas sin carreraId: curso y período quedan null;
    // fails_when=null coincide con null y el mapper asigna curso y período sin carrera conocida;
    // no_claim=que la facultad mande una inscripción sin carrera en la práctica.
    @Test
    void enrollmentWithoutCarreraIdDoesNotMatchRowsWithoutCarreraId() {
        var response = response(5, 1, null, "obs", List.of(detalle(1, 9, "A001")),
                List.of(materia(1, 9, "A001", "UNO")),
                List.of(new MateriaCarrera(1, 9, null, "A001")),
                List.of(new MateriaCurso(1, 9, null, "A001", 3, 1)));

        assertThat(mapper.toDomain(response, SIN_BUSQUEDA).orElseThrow().materias())
                .extracting(MateriaCursada::curso, MateriaCursada::periodo).containsExactly(tuple(null, null));
    }

    // protects=un elemento null en las listas no borra las materias válidas de la inscripción;
    // fails_when=el mapper lanza NullPointerException con un elemento null en detalles o en las tablas auxiliares;
    // no_claim=que la facultad mande elementos null en la práctica.
    @Test
    void nullElementsInTheListsAreIgnored() {
        var response = response(5, 1, 2, "obs",
                Arrays.asList(null, detalle(1, 9, "A001")),
                Arrays.asList(null, materia(1, 9, "A001", "UNO")),
                Arrays.asList(null, new MateriaCarrera(1, 9, 2, "A001")),
                Arrays.asList(null, new MateriaCurso(1, 9, 2, "A001", 3, 1)));

        assertThat(mapper.toDomain(response, SIN_BUSQUEDA).orElseThrow().materias())
                .extracting(MateriaCursada::materiaId, MateriaCursada::nombre, MateriaCursada::curso, MateriaCursada::periodo)
                .containsExactly(tuple("A001", "UNO", 3, 1));
    }

    private PendienteInfoFacultadResponse read(String path) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            assertThat(in).as("fixture %s", path).isNotNull();
            return json.readValue(in, PendienteInfoFacultadResponse.class);
        }
    }

    /** Aplana el contrato: curso, período, id y nombre de cada materia. */
    private Set<String> esperadoDelContrato() throws IOException {
        Set<String> esperado = new TreeSet<>();
        try (InputStream in = getClass().getResourceAsStream(CONTRATO)) {
            JsonNode raiz = json.readTree(in);
            for (JsonNode curso : raiz.get(0).get("cursos")) {
                for (JsonNode periodo : curso.get("periodos")) {
                    for (JsonNode materia : periodo.get("materias")) {
                        esperado.add(curso.get("curso").asInt() + "|" + periodo.get("periodo").asString() + "|"
                                + materia.get("materiaId").asString() + "|" + materia.get("nombre").asString());
                    }
                }
            }
        }
        return esperado;
    }

    private static Set<String> resumen(List<MateriaCursada> materias) {
        Set<String> resumen = new TreeSet<>();
        materias.forEach(m -> resumen.add(m.curso() + "|" + PeriodoCursada.desde(m.periodo()) + "|"
                + m.materiaId() + "|" + m.nombre()));
        return resumen;
    }

    private static PendienteInfoFacultadResponse response(Integer curso, Integer planId, Integer carreraId,
                                                          String observaciones, List<Detalle> detalles,
                                                          List<Materia> materias, List<MateriaCarrera> materiascarrera,
                                                          List<MateriaCurso> materiascurso) {
        return new PendienteInfoFacultadResponse(new Inscripcion(curso, planId, carreraId, observaciones),
                new Plan("Plan"), new Carrera("Carrera"), detalles, materias, materiascarrera, materiascurso);
    }

    private static Detalle detalle(Integer facultadId, Integer planId, String materiaId) {
        return new Detalle(facultadId, planId, materiaId, null);
    }

    private static Materia materia(Integer facultadId, Integer planId, String materiaId, String nombre) {
        return new Materia(facultadId, planId, materiaId, nombre);
    }
}

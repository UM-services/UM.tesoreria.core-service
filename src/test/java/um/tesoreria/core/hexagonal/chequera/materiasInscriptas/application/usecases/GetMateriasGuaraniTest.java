package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.application.service.PendienteInfoFacultadService;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.MateriaCursada;
import um.tesoreria.core.hexagonal.extern.guarani.cursada.domain.model.*;
import um.tesoreria.core.hexagonal.extern.guarani.cursada.domain.ports.out.FindInscripcionesGuaraniPort;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetMateriasGuaraniTest {
    private final ChequeraSerieService series = mock(ChequeraSerieService.class);
    private final TipoChequeraService tipos = mock(TipoChequeraService.class);
    private final FacultadService facultades = mock(FacultadService.class);
    private final LectivoService lectivos = mock(LectivoService.class);
    private final PendienteInfoFacultadService sium = mock(PendienteInfoFacultadService.class);
    private final FindInscripcionesGuaraniPort guarani = mock(FindInscripcionesGuaraniPort.class);
    private final GetMateriasInscriptasUseCaseImpl useCase =
            new GetMateriasInscriptasUseCaseImpl(series, tipos, sium, facultades, lectivos, guarani);

    @BeforeEach
    void setUp() {
        when(series.findByUnique(1, 2, 3L)).thenReturn(ChequeraSerie.builder().facultadId(1).tipoChequeraId(2)
                .chequeraSerieId(3L).lectivoId(37).personaId(new BigDecimal("99999999")).build());
        when(tipos.findByTipoChequeraId(2)).thenReturn(TipoChequera.builder()
                .claseChequera(ClaseChequera.builder().grado((byte) 1).build()).build());
        when(facultades.findByFacultadId(1)).thenReturn(Facultad.builder().guaraniResponsableAcademica(12).build());
        when(lectivos.findByLectivoId(37)).thenReturn(Lectivo.builder()
                .fechaInicio(OffsetDateTime.parse("2026-03-15T00:00:00-03:00")).build());
        when(sium.findByAlumno(any(), any(), any(), any(), any())).thenReturn(Optional.empty());
        when(guarani.findByDocumentoAndAnioAcademico("99999999", 2026, 12)).thenReturn(List.of());
    }

    // protects=SIUM queda primero e igual, Guaraní sin filas queda SIN_INSCRIPCION;
    // fails_when=se omite Guaraní vacío o se altera SIUM;
    // no_claim=no consulta servicios reales.
    @Test
    void onlySium() {
        configureSium();
        assertThat(result()).containsExactly(siumEsperado(), vacio(EstadoMaterias.SIN_INSCRIPCION));
        verify(guarani).findByDocumentoAndAnioAcademico("99999999", 2026, 12);
    }

    // protects=solo Guaraní da SIUM vacío y GUARANI/OK con metadatos y agrupado;
    // fails_when=se mezclan fuentes, pierde versión, curso de inscripción o período;
    // no_claim=no prueba filas reales de grado.
    @Test
    void onlyGuarani() {
        configureGuarani();
        assertThat(result()).containsExactly(new MateriasEstado(FuenteMaterias.SIUM, EstadoMaterias.SIN_INSCRIPCION,
                null, null, null, null, List.of()), guaraniEsperado());
    }

    // protects=las dos fuentes con la misma actividad conservan sus grupos independientes;
    // fails_when=se deduplica entre fuentes o se copia curso y período de SIUM a Guaraní;
    // no_claim=no prueba la respuesta de los servicios remotos.
    @Test
    void bothSourcesRemainSeparate() {
        configureSium(); configureGuarani();
        assertThat(result()).containsExactly(siumEsperado(), guaraniEsperado());
    }

    // protects=una entrada por propuesta y versión en el orden que entrega el adaptador;
    // fails_when=se colapsan propuestas o versiones en una única entrada;
    // no_claim=el orden de filas remotas lo prueba el adaptador.
    @Test
    void multipleProposalsAndVersions() {
        when(guarani.findByDocumentoAndAnioAcademico("99999999", 2026, 12)).thenReturn(List.of(
                inscripcion(10, 1), inscripcion(10, 2), inscripcion(20, 3)));
        assertThat(result()).extracting(MateriasEstado::plan).containsExactly(null, "Versión 1", "Versión 2", "Versión 3");
        assertThat(result().subList(1, 4)).allSatisfy(m -> assertThat(m.fuente()).isEqualTo(FuenteMaterias.GUARANI));
    }

    // protects=falla Guaraní da NO_DISPONIBLE y conserva SIUM; el warn no lleva datos del fallo;
    // fails_when=sube la excepción, afecta SIUM o registra mensaje, URL o Throwable;
    // no_claim=no prueba una caída de red real ni logs de Feign externos al use case.
    @Test
    void remoteFailureIsIsolatedAndLogIsSafe() {
        configureSium();
        var failure = new IllegalStateException("secreto-documento-99999999");
        when(guarani.findByDocumentoAndAnioAcademico("99999999", 2026, 12)).thenThrow(failure);
        try (var logs = new LogCapture(GetMateriasInscriptasUseCaseImpl.class)) {
            assertThat(result()).containsExactly(siumEsperado(), vacio(EstadoMaterias.NO_DISPONIBLE));
            assertThat(logs.warnings()).singleElement().satisfies(e -> {
                assertThat(e.getFormattedMessage()).contains("GUARANI", "IllegalStateException")
                        .doesNotContain("secreto", "99999999");
                assertThat(e.getThrowableProxy()).isNull();
                assertThat(e.getArgumentArray()).doesNotContain(failure);
            });
        }
    }

    // protects=no grado devuelve [] y no consulta fuentes ni metadatos para Guaraní;
    // fails_when=llama SIUM, Guaraní, facultad o lectivo;
    // no_claim=no verifica el grado de las chequeras reales.
    @Test
    void noGradeSkipsBothSources() {
        when(tipos.findByTipoChequeraId(2)).thenReturn(TipoChequera.builder().build());
        assertThat(result()).isEmpty();
        verifyNoInteractions(sium, guarani, facultades, lectivos);
    }

    // protects=facultad sin responsable devuelve solo SIUM y no consulta Guaraní ni lectivo;
    // fails_when=crea una fuente Guaraní o consulta sin responsable;
    // no_claim=no verifica la configuración real de las facultades.
    @Test
    void missingResponsableSkipsGuarani() {
        when(facultades.findByFacultadId(1)).thenReturn(Facultad.builder().build());
        assertThat(result()).singleElement().satisfies(m -> assertThat(m.fuente()).isEqualTo(FuenteMaterias.SIUM));
        verifyNoInteractions(guarani, lectivos);
    }

    // protects=fallas de SIUM no impiden mostrar Guaraní;
    // fails_when=el catch de SIUM cancela la consulta Guaraní;
    // no_claim=no prueba una caída real de SIUM.
    @Test
    void siumFailureDoesNotBlockGuarani() {
        configureGuarani();
        when(sium.findByAlumno(any(), any(), any(), any(), any())).thenThrow(new IllegalStateException("fallo"));
        assertThat(result()).containsExactly(new MateriasEstado(FuenteMaterias.SIUM, EstadoMaterias.NO_DISPONIBLE,
                null, null, null, null, List.of()), guaraniEsperado());
    }

    // protects=null anioPlan y OTRO van a curso null/SIN_PERIODO; traduce los tres períodos B1;
    // fails_when=OTRO pasa a anual, o un año null se rellena;
    // no_claim=no prueba los códigos internos de Guaraní (responsabilidad de B1).
    @Test
    void translatesPeriodsAndMissingYears() {
        when(guarani.findByDocumentoAndAnioAcademico("99999999", 2026, 12)).thenReturn(List.of(
                new InscripcionMateriasGuarani(10, 1, "Propuesta sintética", null, List.of(
                        materia("A", 1, "ANUAL", 1), materia("B", 1, "PRIMER_SEMESTRE", 2),
                        materia("C", 1, "SEGUNDO_SEMESTRE", 3), materia("D", 2, "OTRO", 4),
                        materia("E", null, "ANUAL", 5)))));
        var m = result().get(1);
        assertThat(m.cursoInscripcion()).isNull();
        assertThat(m.observaciones()).isNull();
        assertThat(m.cursos()).extracting(CursoMaterias::curso).containsExactly(1, null);
        assertThat(m.cursos().getFirst().periodos()).extracting(PeriodoMaterias::periodo)
                .containsExactly(PeriodoCursada.ANUAL, PeriodoCursada.PRIMER_SEMESTRE, PeriodoCursada.SEGUNDO_SEMESTRE);
        assertThat(m.cursos().getLast().periodos()).containsExactly(new PeriodoMaterias(PeriodoCursada.SIN_PERIODO,
                List.of(new MateriaInscripta("D", "Actividad D"), new MateriaInscripta("E", "Actividad E"))));
    }

    // protects=misma actividad en dos períodos lectivos queda dos veces; repetida en el mismo queda una vez;
    // fails_when=la clave ignora el período lectivo o no elimina duplicados;
    // no_claim=no prueba duplicados reales en el productor.
    @Test
    void deduplicatesByPeriodoLectivo() {
        when(guarani.findByDocumentoAndAnioAcademico("99999999", 2026, 12)).thenReturn(List.of(
                new InscripcionMateriasGuarani(10, 1, "Propuesta sintética", 1, List.of(
                        materia("A", 1, "PRIMER_SEMESTRE", 1), materia("A", 1, "PRIMER_SEMESTRE", 1),
                        materia("A", 1, "SEGUNDO_SEMESTRE", 2)))));
        var periodos = result().get(1).cursos().getFirst().periodos();
        assertThat(periodos).hasSize(2).allSatisfy(p -> assertThat(p.materias()).hasSize(1));
        assertThat(periodos).extracting(PeriodoMaterias::periodo)
                .containsExactly(PeriodoCursada.PRIMER_SEMESTRE, PeriodoCursada.SEGUNDO_SEMESTRE);
    }

    // protects=lectivo sin fecha no dispara una consulta con año inventado y solo Guaraní falla;
    // fails_when=usa el año actual o propaga un error;
    // no_claim=no define una regla nueva de calendario.
    @Test
    void missingLectivoDateMakesOnlyGuaraniUnavailable() {
        when(lectivos.findByLectivoId(37)).thenReturn(Lectivo.builder().build());
        assertThat(result()).extracting(MateriasEstado::estado)
                .containsExactly(EstadoMaterias.SIN_INSCRIPCION, EstadoMaterias.NO_DISPONIBLE);
        verifyNoInteractions(guarani);
    }

    // protects=SIUM conserva OK cuando el grupo tiene una sola materia sin código, como antes de B2;
    // fails_when=la integración Guaraní agrega una validación que convierte SIUM en NO_DISPONIBLE;
    // no_claim=no afirma que existan esas filas en la base real; verifica la compatibilidad de la entrada admitida.
    @Test
    void siumPreservesSingleNullCodeAndGuaraniRemainsIndependent() {
        when(sium.findByAlumno(any(), any(), any(), any(), any())).thenReturn(Optional.of(new InscripcionMaterias(
                3, "Plan SIUM", "Carrera SIUM", "obs", List.of(new MateriaCursada(1, 1, null, "Sin código", 3, 0)))));
        configureGuarani();

        assertThat(result()).containsExactly(new MateriasEstado(FuenteMaterias.SIUM, EstadoMaterias.OK,
                3, "Plan SIUM", "Carrera SIUM", "obs", List.of(new CursoMaterias(3, List.of(
                new PeriodoMaterias(PeriodoCursada.ANUAL, List.of(new MateriaInscripta(null, "Sin código"))))))),
                guaraniEsperado());
    }

    // protects=Guaraní admite código null y lo ordena al final, incluso cuando SIUM falla al comparar un null;
    // fails_when=la política de códigos nulos se comparte entre fuentes o se pierde la materia Guaraní sin código;
    // no_claim=no consulta B1 ni SIUM real; son datos sintéticos de sus contratos.
    @Test
    void guaraniAllowsNullCodesWithoutChangingSiumComparisonFailure() {
        when(sium.findByAlumno(any(), any(), any(), any(), any())).thenReturn(Optional.of(new InscripcionMaterias(
                3, "Plan SIUM", "Carrera SIUM", "obs", List.of(
                new MateriaCursada(1, 1, null, "Sin código", 3, 0),
                new MateriaCursada(1, 1, "A", "Actividad A", 3, 0)))));
        when(guarani.findByDocumentoAndAnioAcademico("99999999", 2026, 12)).thenReturn(List.of(
                new InscripcionMateriasGuarani(10, 1, "Propuesta sintética", 2, List.of(
                        materia(null, 1, "ANUAL", 4321), materia("A", 1, "ANUAL", 4321)))));

        var fuentes = result();
        assertThat(fuentes.getFirst()).isEqualTo(new MateriasEstado(FuenteMaterias.SIUM,
                EstadoMaterias.NO_DISPONIBLE, null, null, null, null, List.of()));
        assertThat(fuentes.get(1).estado()).isEqualTo(EstadoMaterias.OK);
        assertThat(fuentes.get(1).cursos().getFirst().periodos().getFirst().materias())
                .containsExactly(new MateriaInscripta("A", "Actividad A"), new MateriaInscripta(null, "Actividad null"));
    }

    private List<MateriasEstado> result() { return useCase.getMateriasInscriptas(1, 2, 3L); }
    private void configureSium() {
        when(sium.findByAlumno(any(), any(), any(), any(), any())).thenReturn(Optional.of(new InscripcionMaterias(
                3, "Plan SIUM", "Carrera SIUM", "obs", List.of(new MateriaCursada(1, 1, "A", "Actividad A", 3, 0)))));
    }
    private void configureGuarani() {
        when(guarani.findByDocumentoAndAnioAcademico("99999999", 2026, 12)).thenReturn(List.of(inscripcion(10, 1)));
    }
    private InscripcionMateriasGuarani inscripcion(int propuesta, int version) {
        return new InscripcionMateriasGuarani(propuesta, version, "Propuesta sintética", 2,
                List.of(materia("A", 1, "SEGUNDO_SEMESTRE", 4321)));
    }
    private MateriaCursadaGuarani materia(String codigo, Integer curso, String periodo, int lectivo) {
        return new MateriaCursadaGuarani(codigo, "Actividad " + codigo, curso, periodo, lectivo);
    }
    private MateriasEstado vacio(EstadoMaterias estado) {
        return new MateriasEstado(FuenteMaterias.GUARANI, estado, null, null, null, null, List.of());
    }
    private MateriasEstado siumEsperado() {
        return new MateriasEstado(FuenteMaterias.SIUM, EstadoMaterias.OK, 3, "Plan SIUM", "Carrera SIUM", "obs",
                List.of(new CursoMaterias(3, List.of(new PeriodoMaterias(PeriodoCursada.ANUAL,
                        List.of(new MateriaInscripta("A", "Actividad A")))))));
    }
    private MateriasEstado guaraniEsperado() {
        return new MateriasEstado(FuenteMaterias.GUARANI, EstadoMaterias.OK, 2, "Versión 1", "Propuesta sintética", null,
                List.of(new CursoMaterias(1, List.of(new PeriodoMaterias(PeriodoCursada.SEGUNDO_SEMESTRE,
                        List.of(new MateriaInscripta("A", "Actividad A")))))));
    }
}

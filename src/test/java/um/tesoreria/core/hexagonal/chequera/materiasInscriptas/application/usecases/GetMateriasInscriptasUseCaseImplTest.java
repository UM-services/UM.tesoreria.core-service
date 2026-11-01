package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.CursoMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.EstadoMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.FuenteMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriaInscripta;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriasEstado;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.PeriodoCursada;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.PeriodoMaterias;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.application.service.PendienteInfoFacultadService;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.MateriaCursada;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GetMateriasInscriptasUseCaseImplTest {

    private static final BigDecimal PERSONA = new BigDecimal("30123456");
    private static final Integer DOCUMENTO = 2;

    private ChequeraSerieService chequeraSerieService;
    private TipoChequeraService tipoChequeraService;
    private PendienteInfoFacultadService facultad;
    private GetMateriasInscriptasUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        chequeraSerieService = mock(ChequeraSerieService.class);
        tipoChequeraService = mock(TipoChequeraService.class);
        facultad = mock(PendienteInfoFacultadService.class);
        useCase = new GetMateriasInscriptasUseCaseImpl(chequeraSerieService, tipoChequeraService, facultad);
        when(chequeraSerieService.findByUnique(1, 2, 12345L)).thenReturn(serie());
        when(tipoChequeraService.findByTipoChequeraId(2)).thenReturn(tipo(grado((byte) 1)));
    }

    // protects=una inscripción con datos da SIUM/OK con curso, plan, carrera y observaciones, y traduce el período
    //   (0 anual, 1 primer semestre, 2 segundo, 3 sin período) sin convertir el null en sin período antes de agrupar;
    // fails_when=el use case pasa el código crudo, traduce null a SIN_PERIODO antes de agrupar o pierde un dato de la inscripción;
    // no_claim=el orden fino de los grupos (lo prueba AgrupadorMateriasTest) ni la facultad real (el servicio es un mock).
    @Test
    void enrollmentBecomesSiumOkWithTranslatedPeriods() {
        when(facultad.findByAlumno(1, 37, 4, PERSONA, DOCUMENTO)).thenReturn(Optional.of(new InscripcionMaterias(
                5, "Plan 2016", "Carrera X", "obs", List.of(
                materia("A", 1, 0), materia("B", 1, 1), materia("C", 1, 2), materia("D", 2, 3),
                materia("E", 3, null), materia("F", null, 1)))));

        List<MateriasEstado> result = useCase.getMateriasInscriptas(1, 2, 12345L);

        assertThat(result).containsExactly(new MateriasEstado(FuenteMaterias.SIUM, EstadoMaterias.OK, 5, "Plan 2016",
                "Carrera X", "obs", List.of(
                new CursoMaterias(1, List.of(
                        new PeriodoMaterias(PeriodoCursada.ANUAL, List.of(new MateriaInscripta("A", "N-A"))),
                        new PeriodoMaterias(PeriodoCursada.PRIMER_SEMESTRE, List.of(new MateriaInscripta("B", "N-B"))),
                        new PeriodoMaterias(PeriodoCursada.SEGUNDO_SEMESTRE, List.of(new MateriaInscripta("C", "N-C"))))),
                new CursoMaterias(2, List.of(
                        new PeriodoMaterias(PeriodoCursada.SIN_PERIODO, List.of(new MateriaInscripta("D", "N-D"))))),
                new CursoMaterias(null, List.of(
                        new PeriodoMaterias(PeriodoCursada.SIN_PERIODO, List.of(
                                new MateriaInscripta("E", "N-E"), new MateriaInscripta("F", "N-F"))))))));
    }

    // protects=la facultad recibe lectivo, geográfica, persona y documento de la chequera, no del tipo ni de la URL;
    // fails_when=se cambian lectivo y geográfica, o se manda otra persona;
    // no_claim=que la facultad acepte esos valores (mock).
    @Test
    void facultyReceivesTheSerieData() {
        when(facultad.findByAlumno(any(), any(), any(), any(), any())).thenReturn(Optional.empty());

        useCase.getMateriasInscriptas(1, 2, 12345L);

        verify(facultad).findByAlumno(1, 37, 4, PERSONA, DOCUMENTO);
    }

    // protects=regla 1: una chequera de otra clase, sin clase o sin grado da lista vacía sin llamar a la facultad;
    // fails_when=el use case consulta a la facultad para una chequera que no es de grado;
    // no_claim=que la clase de chequera de la base tenga el grado correcto.
    @ParameterizedTest
    @MethodSource("notGradeClasses")
    void nonGradeChequeraReturnsEmptyWithoutCallingFaculty(TipoChequera tipo) {
        when(tipoChequeraService.findByTipoChequeraId(2)).thenReturn(tipo);

        assertThat(useCase.getMateriasInscriptas(1, 2, 12345L)).isEmpty();

        verifyNoInteractions(facultad);
    }

    static Stream<TipoChequera> notGradeClasses() {
        return Stream.of(tipo(null), tipo(grado(null)), tipo(grado((byte) 0)), tipo(grado((byte) 2)));
    }

    // protects=regla 4: sin inscripción da SIUM/SIN_INSCRIPCION con cursos vacíos y datos de inscripción null;
    // fails_when=devuelve lista vacía, OK sin materias o rellena los datos de inscripción;
    // no_claim=que la facultad responda vacío en este caso (mock).
    @Test
    void emptyEnrollmentIsSinInscripcion() {
        when(facultad.findByAlumno(any(), any(), any(), any(), any())).thenReturn(Optional.empty());

        assertThat(useCase.getMateriasInscriptas(1, 2, 12345L)).containsExactly(
                new MateriasEstado(FuenteMaterias.SIUM, EstadoMaterias.SIN_INSCRIPCION, null, null, null, null, List.of()));
    }

    // protects=regla 3: cualquier RuntimeException de la facultad da SIUM/NO_DISPONIBLE con cursos vacíos y el resto null;
    // fails_when=la excepción sube al controller o NO_DISPONIBLE trae datos;
    // no_claim=las fallas reales de red (hay un test de ensamblado con servidor real).
    @ParameterizedTest
    @MethodSource("facultyFailures")
    void facultyFailureIsNoDisponible(RuntimeException failure) {
        when(facultad.findByAlumno(any(), any(), any(), any(), any())).thenThrow(failure);

        assertThat(useCase.getMateriasInscriptas(1, 2, 12345L)).containsExactly(
                new MateriasEstado(FuenteMaterias.SIUM, EstadoMaterias.NO_DISPONIBLE, null, null, null, null, List.of()));
    }

    static Stream<RuntimeException> facultyFailures() {
        return Stream.of(
                HttpServerErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "x", HttpHeaders.EMPTY, new byte[0], null),
                new ResourceAccessException("timeout", new IOException("lento")),
                new IllegalArgumentException("Facultad apiserver is null or empty"));
    }

    // protects=D6: un detalle con materiaId null junto a otra materia del mismo grupo no da HTTP 500 sino NO_DISPONIBLE;
    // fails_when=el catch cubre solo la llamada y el agrupado falla con NullPointerException fuera del catch;
    // no_claim=que la facultad mande esos datos en la práctica (son columnas de clave primaria).
    @Test
    void malformedEnrollmentFailingInGroupingIsNoDisponible() {
        when(facultad.findByAlumno(any(), any(), any(), any(), any())).thenReturn(Optional.of(new InscripcionMaterias(
                5, "P", "C", null, List.of(materia(null, 1, 0), materia("B", 1, 0)))));

        assertThat(useCase.getMateriasInscriptas(1, 2, 12345L)).extracting(MateriasEstado::estado)
                .containsExactly(EstadoMaterias.NO_DISPONIBLE);
    }

    // protects=D8/D12: el warn lleva facultad, chequera, clase de la excepción y status HTTP, y no lleva mensaje,
    //   cuerpo, URL, stack trace ni la persona y el documento;
    // fails_when=el warn agrega getMessage(), el cuerpo de la respuesta, la URL, el stack trace o la identidad;
    // no_claim=otros logs de la facultad (consumer) ni el formato en producción.
    @Test
    void warningCarriesTheSafeFieldsOnly() {
        var failure = HttpServerErrorException.create(HttpStatus.BAD_GATEWAY,
                "mensaje-secreto http://10.0.0.7:8080/pendienteinfo/info/1/37/4/30123456/2", HttpHeaders.EMPTY,
                "cuerpo-secreto 30123456".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        when(facultad.findByAlumno(any(), any(), any(), any(), any())).thenThrow(failure);

        try (var logs = new LogCapture(GetMateriasInscriptasUseCaseImpl.class)) {
            useCase.getMateriasInscriptas(1, 2, 12345L);

            assertThat(logs.warnings()).hasSize(1).allSatisfy(event -> {
                assertThat(event.getFormattedMessage())
                        .contains("facultadId=1", "chequeraSerieId=12345", "HttpServerErrorException$BadGateway", "status=502")
                        .doesNotContain("mensaje-secreto", "cuerpo-secreto", "http://", "10.0.0.7", "30123456");
                assertThat(event.getThrowableProxy()).isNull();
                assertThat(event.getArgumentArray()).doesNotContain(failure);
            });
        }
    }

    // protects=sin respuesta HTTP (timeout, red) el warn dice status=null y conserva la clase de la excepción;
    // fails_when=el use case falla al leer el status de una excepción sin respuesta o inventa un código;
    // no_claim=el origen real del timeout (lo prueba el ensamblado).
    @Test
    void warningWithoutHttpResponseHasNullStatus() {
        when(facultad.findByAlumno(any(), any(), any(), any(), any()))
                .thenThrow(new ResourceAccessException("I/O error", new IOException("lento")));

        try (var logs = new LogCapture(GetMateriasInscriptasUseCaseImpl.class)) {
            useCase.getMateriasInscriptas(1, 2, 12345L);

            assertThat(logs.warnings()).singleElement().satisfies(event ->
                    assertThat(event.getFormattedMessage()).contains("ResourceAccessException", "status=null"));
        }
    }

    // protects=una chequera inexistente sube como ChequeraSerieException (el controller la hace 400) y no se consulta nada más;
    // fails_when=el use case devuelve lista vacía o consulta tipo o facultad sin chequera;
    // no_claim=el código HTTP (lo prueba MateriasInscriptasControllerTest).
    @Test
    void missingChequeraPropagatesWithoutFurtherCalls() {
        when(chequeraSerieService.findByUnique(9, 9, 9L)).thenThrow(new ChequeraSerieException(9, 9, 9L));

        assertThatThrownBy(() -> useCase.getMateriasInscriptas(9, 9, 9L)).isInstanceOf(ChequeraSerieException.class);

        verifyNoInteractions(facultad);
        verify(tipoChequeraService, never()).findByTipoChequeraId(any());
    }

    private static ChequeraSerie serie() {
        return ChequeraSerie.builder().facultadId(1).tipoChequeraId(2).chequeraSerieId(12345L).lectivoId(37)
                .geograficaId(4).personaId(PERSONA).documentoId(DOCUMENTO).build();
    }

    private static ClaseChequera grado(Byte grado) {
        return ClaseChequera.builder().claseChequeraId(2).grado(grado).build();
    }

    private static TipoChequera tipo(ClaseChequera clase) {
        return TipoChequera.builder().tipoChequeraId(2).claseChequera(clase).build();
    }

    private static MateriaCursada materia(String id, Integer curso, Integer periodo) {
        return new MateriaCursada(1, 9, id, id == null ? null : "N-" + id, curso, periodo);
    }
}

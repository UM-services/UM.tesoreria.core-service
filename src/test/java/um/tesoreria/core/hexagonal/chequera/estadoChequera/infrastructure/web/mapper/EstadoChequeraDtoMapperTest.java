package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.CursoMaterias;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.EstadoChequera;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.EstadoMaterias;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.FuenteMaterias;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.MateriaInscripta;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.MateriasEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.PeriodoCursada;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.PeriodoMaterias;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.CursoMateriasResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.EstadoChequeraResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.MateriasEstadoResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoChequeraDtoMapperTest {

    private final EstadoChequeraDtoMapper mapper = new EstadoChequeraDtoMapper();

    // protects=cada campo de materias llega a su campo del DTO, sin columnas cruzadas, con curso y nombre null;
    // fails_when=el mapper cruza plan con carrera o materiaId con nombre, o pierde un nivel;
    // no_claim=que el core arme este contenido (en #482 materias siempre es []).
    @Test
    void mapsEveryMateriasFieldToItsOwnDtoField() {
        MateriasEstado ok = new MateriasEstado(FuenteMaterias.SIUM, EstadoMaterias.OK, 5, "Plan A", "Carrera B", "Obs C",
                List.of(
                        new CursoMaterias(3, List.of(new PeriodoMaterias(PeriodoCursada.PRIMER_SEMESTRE,
                                List.of(new MateriaInscripta("0001", "Nombre Uno"))))),
                        new CursoMaterias(null, List.of(new PeriodoMaterias(PeriodoCursada.SIN_PERIODO,
                                List.of(new MateriaInscripta("0002", null)))))));

        MateriasEstadoResponse response = mapper.toResponse(estadoConMaterias(ok)).materias().getFirst();

        assertThat(response.fuente()).isEqualTo(FuenteMaterias.SIUM);
        assertThat(response.estado()).isEqualTo(EstadoMaterias.OK);
        assertThat(response.cursoInscripcion()).isEqualTo(5);
        assertThat(response.plan()).isEqualTo("Plan A");
        assertThat(response.carrera()).isEqualTo("Carrera B");
        assertThat(response.observaciones()).isEqualTo("Obs C");
        assertThat(response.cursos()).hasSize(2);
        CursoMateriasResponse curso = response.cursos().getFirst();
        assertThat(curso.curso()).isEqualTo(3);
        assertThat(curso.periodos().getFirst().periodo()).isEqualTo(PeriodoCursada.PRIMER_SEMESTRE);
        assertThat(curso.periodos().getFirst().materias().getFirst().materiaId()).isEqualTo("0001");
        assertThat(curso.periodos().getFirst().materias().getFirst().nombre()).isEqualTo("Nombre Uno");
        CursoMateriasResponse sinCurso = response.cursos().getLast();
        assertThat(sinCurso.curso()).isNull();
        assertThat(sinCurso.periodos().getFirst().periodo()).isEqualTo(PeriodoCursada.SIN_PERIODO);
        assertThat(sinCurso.periodos().getFirst().materias().getFirst().materiaId()).isEqualTo("0002");
        assertThat(sinCurso.periodos().getFirst().materias().getFirst().nombre()).isNull();
    }

    // protects=un estado sin datos (NO_DISPONIBLE) conserva fuente y estado, deja los datos de inscripción en null y cursos vacío;
    // fails_when=el mapper falla con null o inventa valores para los campos ausentes;
    // no_claim=cuándo el core elige este estado (regla 3 de #481, en #487).
    @Test
    void mapsAnUnavailableSourceWithNullInscriptionDataAndNoCursos() {
        MateriasEstado noDisponible = new MateriasEstado(FuenteMaterias.GUARANI, EstadoMaterias.NO_DISPONIBLE,
                null, null, null, null, List.of());

        MateriasEstadoResponse response = mapper.toResponse(estadoConMaterias(noDisponible)).materias().getFirst();

        assertThat(response.fuente()).isEqualTo(FuenteMaterias.GUARANI);
        assertThat(response.estado()).isEqualTo(EstadoMaterias.NO_DISPONIBLE);
        assertThat(response.cursoInscripcion()).isNull();
        assertThat(response.plan()).isNull();
        assertThat(response.carrera()).isNull();
        assertThat(response.observaciones()).isNull();
        assertThat(response.cursos()).isEmpty();
    }

    private EstadoChequera estadoConMaterias(MateriasEstado... materias) {
        return new EstadoChequera(1, "Facultad", 2, "Tipo", 3L, BigDecimal.ONE, "Apellido", "Nombre", "Arancel",
                "Lectivo", BigDecimal.ZERO, "Impresion", 1, false, List.of(), List.of(), List.of(materias));
    }
}

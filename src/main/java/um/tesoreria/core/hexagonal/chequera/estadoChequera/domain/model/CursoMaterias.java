package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

import java.util.List;

/** Materias de un curso; {@code curso} es {@code null} en el último grupo, el de las materias sin curso. */
public record CursoMaterias(Integer curso, List<PeriodoMaterias> periodos) {
}

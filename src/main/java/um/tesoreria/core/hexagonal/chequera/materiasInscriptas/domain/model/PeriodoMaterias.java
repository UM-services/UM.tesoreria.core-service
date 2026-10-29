package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model;

import java.util.List;

public record PeriodoMaterias(PeriodoCursada periodo, List<MateriaInscripta> materias) {
}

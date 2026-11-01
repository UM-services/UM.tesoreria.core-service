package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Groups subjects from one source. The caller supplies the translated period. */
public final class AgrupadorMaterias {

    private AgrupadorMaterias() {
    }

    /** Keeps the first occurrence of each faculty, plan and subject key. */
    public static List<CursoMaterias> agrupar(List<MateriaAgrupable> materias) {
        Map<Integer, Map<PeriodoCursada, List<MateriaInscripta>>> cursos =
                new TreeMap<>(Comparator.nullsLast(Comparator.naturalOrder()));
        Set<ClaveMateria> vistas = new HashSet<>();
        for (MateriaAgrupable materia : materias) {
            if (!vistas.add(new ClaveMateria(materia.facultadId(), materia.planId(), materia.materiaId()))) {
                continue;
            }
            boolean sinCursada = materia.curso() == null || materia.periodo() == null;
            Integer curso = sinCursada ? null : materia.curso();
            PeriodoCursada periodo = sinCursada ? PeriodoCursada.SIN_PERIODO : materia.periodo();
            cursos.computeIfAbsent(curso, ignored -> new EnumMap<>(PeriodoCursada.class))
                    .computeIfAbsent(periodo, ignored -> new ArrayList<>())
                    .add(new MateriaInscripta(materia.materiaId(), materia.nombre()));
        }
        return cursos.entrySet().stream()
                .map(curso -> new CursoMaterias(curso.getKey(), curso.getValue().entrySet().stream()
                        .map(periodo -> new PeriodoMaterias(periodo.getKey(), periodo.getValue().stream()
                                .sorted(Comparator.comparing(MateriaInscripta::materiaId))
                                .toList()))
                        .toList()))
                .toList();
    }

    private record ClaveMateria(Integer facultadId, Integer planId, String materiaId) {
    }
}

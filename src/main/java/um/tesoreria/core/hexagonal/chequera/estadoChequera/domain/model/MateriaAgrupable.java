package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

/**
 * Entrada del agrupado por curso y período. El período llega ya traducido por cada fuente;
 * {@code curso} o {@code periodo} {@code null} significan materia sin datos de cursada.
 */
public record MateriaAgrupable(
        Integer facultadId,
        Integer planId,
        String materiaId,
        String nombre,
        Integer curso,
        PeriodoCursada periodo) {
}

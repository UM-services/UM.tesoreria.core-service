package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

/**
 * Resultado de consultar las materias en una fuente. {@code OK} trae el detalle por curso;
 * {@code SIN_INSCRIPCION} y {@code NO_DISPONIBLE} dejan {@code cursos} vacío y los datos de la
 * inscripción en {@code null}.
 */
public enum EstadoMaterias {
    OK,
    SIN_INSCRIPCION,
    NO_DISPONIBLE
}

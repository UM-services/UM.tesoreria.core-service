package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

/** Materia a mostrar; {@code nombre} es {@code null} si no se pudo resolver. */
public record MateriaInscripta(String materiaId, String nombre) {
}

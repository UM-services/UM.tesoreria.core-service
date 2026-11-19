package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

/**
 * Estado de una tarea del job de incompletas. Los estados de resultado de la etapa BORRADORES
 * ({@code BORRADOR_CREADO}, {@code BORRADOR_REUTILIZADO}) son los que habilitan la etapa EMISION.
 */
public enum IncompletasTareaEstado {
    PENDIENTE,
    BORRADOR_CREADO,
    BORRADOR_REUTILIZADO,
    EMITIDA,
    OMITIDA,
    ERROR
}

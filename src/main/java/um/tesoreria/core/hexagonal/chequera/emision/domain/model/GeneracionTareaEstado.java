package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

/**
 * Estado de una tarea del job de generación. Los estados de resultado de la etapa BORRADORES son
 * los que habilitan la etapa EMISION.
 */
public enum GeneracionTareaEstado {
    PENDIENTE,
    BORRADOR_CREADO,
    BORRADOR_REUTILIZADO,
    EMITIDA,
    OMITIDA,
    ERROR
}

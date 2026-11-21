package um.tesoreria.core.hexagonal.pagos.entrega.domain.model;

/**
 * Asignación de costos: un detalle de entrega junto con la entrega a la que pertenece.
 * Es la unidad que consume el motor de asientos para las líneas de costos.
 */
public record EntregaAsignacion(
        Entrega entrega,
        EntregaDetalle detalle
) {
}

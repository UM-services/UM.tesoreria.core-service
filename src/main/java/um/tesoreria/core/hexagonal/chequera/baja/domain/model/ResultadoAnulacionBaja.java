package um.tesoreria.core.hexagonal.chequera.baja.domain.model;

public record ResultadoAnulacionBaja(
        int cuotasActualizadas,
        int debitosActualizados,
        boolean bajaEliminada) {
}

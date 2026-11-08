package um.tesoreria.core.hexagonal.chequera.baja.infrastructure.web.dto;

public record AnulacionBajaResponse(
        int cuotasActualizadas,
        int debitosActualizados,
        boolean bajaEliminada) {
}

package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

public record EmitirBorradorRequest(
        Integer version,
        Boolean forzarNueva,
        Boolean forzarDeuda,
        String motivoOverride,
        String claveIdempotencia) {
}

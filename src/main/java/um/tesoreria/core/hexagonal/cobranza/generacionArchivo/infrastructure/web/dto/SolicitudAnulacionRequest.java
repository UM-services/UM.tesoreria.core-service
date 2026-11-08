package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto;

public record SolicitudAnulacionRequest(
        String fechaEnvio,
        Boolean soloSantander,
        Boolean soloOtrosBancos) {
}

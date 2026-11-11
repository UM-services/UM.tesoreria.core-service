package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto;

public record SolicitudReprogramacionRequest(
        String desde,
        String hasta,
        Boolean soloSantander,
        Boolean soloOtrosBancos,
        String fechaVencimiento) {
}

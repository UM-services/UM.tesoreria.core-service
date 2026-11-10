package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model;

import java.time.OffsetDateTime;

public record SolicitudReprogramacion(
        OffsetDateTime desde,
        OffsetDateTime hasta,
        boolean soloSantander,
        boolean soloOtrosBancos,
        OffsetDateTime fechaVencimiento) {
}

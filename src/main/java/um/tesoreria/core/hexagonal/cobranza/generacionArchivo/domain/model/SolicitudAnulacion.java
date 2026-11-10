package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model;

import java.time.OffsetDateTime;

public record SolicitudAnulacion(
        CanalGeneracion canal,
        OffsetDateTime fechaEnvio,
        boolean soloSantander,
        boolean soloOtrosBancos) {
}

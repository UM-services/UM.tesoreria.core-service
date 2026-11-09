package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.dto.AutorizanteEnvioResponse;

import java.util.List;

@Component
public class AutorizanteEnvioDtoMapper {

    public AutorizanteEnvioResponse toResponse(AutorizanteEnvio autorizante) {
        if (autorizante == null) {
            return null;
        }
        return AutorizanteEnvioResponse.builder()
                .usuarioId(autorizante.usuarioId())
                .dependenciaIds(autorizante.dependenciaIdsOrdenadas())
                .build();
    }

    public List<AutorizanteEnvioResponse> toResponse(List<AutorizanteEnvio> autorizantes) {
        if (autorizantes == null) {
            return List.of();
        }
        return autorizantes.stream().map(this::toResponse).toList();
    }

}

package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** Clave compuesta de {@code compra_autorizante_envio_dependencia}. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutorizanteEnvioDependenciaId implements Serializable {

    private Integer usuarioId;

    private Integer dependenciaId;

}

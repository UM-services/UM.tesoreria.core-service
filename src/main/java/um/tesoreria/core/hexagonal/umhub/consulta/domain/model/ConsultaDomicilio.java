package um.tesoreria.core.hexagonal.umhub.consulta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Domicilio + datos de contacto de la persona, con nombres de provincia/localidad
 * resueltos (best-effort) para consumidores externos del hub.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDomicilio {
    private String calle;
    private String puerta;
    private String piso;
    private String dpto;
    private String codigoPostal;
    private Integer provinciaId;
    private String provinciaNombre;
    private Integer localidadId;
    private String localidadNombre;
    private String telefono;
    private String movil;
    private String emailPersonal;
    private String emailInstitucional;
}

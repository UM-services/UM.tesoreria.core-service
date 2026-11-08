package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Cabecera de un archivo de rendición ya procesado ({@code archivo_importado}).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoImportado {

    private Integer id;
    private String nombre;
    private OffsetDateTime fecha;
    private Integer registros;

}

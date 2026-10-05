package um.tesoreria.core.hexagonal.umhub.consulta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Vista de solo lectura para consumidores externos: identidad + tipos registrados
 * bajo el numero de documento + domicilio/contacto.
 * NO expone password, cbu, cuit, uniqueId, hpum ni guaraniPersona.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaPersona {
    private BigDecimal numeroDocumento;
    private String nombre;
    private String apellido;
    private String sexo;
    private String numeroPrefijo;
    private String numeroPosfijo;
    private List<TipoDocumentoConsulta> documentos;
    private ConsultaDomicilio domicilio;
}

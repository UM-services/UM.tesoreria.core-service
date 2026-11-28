package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Fila del padrón de envío masivo tal como la consume la pantalla. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraEnvioAltaResponse {

    private Long chequeraId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer alternativaId;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer lectivoId;
    private Integer geograficaId;
    private String numero;
    private String personaApellidoNombre;
    private String tipoChequera;
    private String arancelTipo;
    private String emailPersonal;
    private String emailInstitucional;
    private Boolean emailPersonalValido;
    private Boolean emailInstitucionalValido;
    private Boolean enviado;
}

package um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.geografica.infrastructure.web.dto.GeograficaResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoChequeraResponse {
    private Integer tipoChequeraId;
    private String nombre;
    private String prefijo;
    private Integer geograficaId;
    private Integer claseChequeraId;
    private Byte imprimir;
    private Byte contado;
    private Byte multiple;
    private String emailCopia;
    private GeograficaResponse geografica;
}

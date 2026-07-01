package um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.web.dto;

import lombok.Data;

@Data
public class TipoChequeraRequest {
    private Integer tipoChequeraId;
    private String nombre;
    private String prefijo;
    private Integer geograficaId;
    private Integer claseChequeraId;
    private Byte imprimir;
    private Byte contado;
    private Byte multiple;
    private String emailCopia;
}

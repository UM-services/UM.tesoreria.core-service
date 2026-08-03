package um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoChequeraSearch {

    private Integer tipoChequeraId;
    private String nombre;
    private String prefijo;
    private Integer geograficaId;
    private Integer claseChequeraId;
    private Byte imprimir;
    private Byte contado;
    private Byte multiple;
    private String emailCopia;
    private String search;
    private LocalDateTime created;
    private LocalDateTime updated;
}

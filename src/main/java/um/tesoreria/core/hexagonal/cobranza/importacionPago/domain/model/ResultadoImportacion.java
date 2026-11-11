package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoImportacion {

    private Long importacionId;
    private String filename;
    private CanalImportacion canal;

    private int registros;
    private int importados;
    private int omitidos;
    private int noEncontrados;
    private int duplicados;
    private int rechazados;

    private BigDecimal total;
    private List<String> errores;

}

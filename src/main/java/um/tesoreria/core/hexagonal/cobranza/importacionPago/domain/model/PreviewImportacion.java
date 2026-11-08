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
public class PreviewImportacion {

    private String filename;
    private CanalImportacion canal;
    private boolean yaImportada;

    private int registros;
    private BigDecimal total;
    private int encontrados;
    private int perdidos;
    private int duplicados;
    private int rechazados;

    private List<LineaImportacion> lineas;

}

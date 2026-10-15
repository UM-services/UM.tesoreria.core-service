package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRecepcionResponse {

    private Long compraRecepcionId;
    private Long compraFormalizacionId;
    private String numero;
    private Integer ejercicioId;
    private LocalDateTime fecha;
    private String remito;
    private String resultado;
    private String patrimonio;
    private String observacion;
    private Integer usuarioId;
    private List<CompraRecepcionItemResponse> items;
    private LocalDateTime created;
    private LocalDateTime updated;

}

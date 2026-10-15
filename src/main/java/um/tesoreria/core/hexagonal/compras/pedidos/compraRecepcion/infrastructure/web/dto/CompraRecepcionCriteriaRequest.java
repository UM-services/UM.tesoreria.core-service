package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Filtros de consulta de recepciones (body de {@code POST /search}). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRecepcionCriteriaRequest {

    private Integer ejercicioId;
    private Long compraFormalizacionId;
    private String resultado;
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;

}

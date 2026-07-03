package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectivoTotalImputacionResponse {
    private Long lectivoTotalImputacionId;
    private Integer facultadId;
    private Integer lectivoId;
    private Integer tipoChequeraId;
    private Integer productoId;
    private BigDecimal numeroCuenta;

    private Facultad facultad;
    private Lectivo lectivo;
    private TipoChequera tipoChequera;
    private Producto producto;
    private Cuenta cuenta;
}

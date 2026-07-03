package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model;

import lombok.*;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectivoTotalImputacion {
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

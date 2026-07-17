package um.tesoreria.core.hexagonal.compras.proveedor.domain.model;

import java.math.BigDecimal;

import lombok.*;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    private Integer proveedorId;
    private String cuit;
    private String nombreFantasia;
    private String razonSocial;
    private String ordenCheque;
    private String domicilio;
    private String telefono;
    private String fax;
    private String celular;
    private String email;
    private String emailInterno;
    private BigDecimal numeroCuenta;
    private Byte habilitado;
    private String cbu;
    private Cuenta cuenta;

}

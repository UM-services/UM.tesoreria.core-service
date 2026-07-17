package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorRequest {
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
}

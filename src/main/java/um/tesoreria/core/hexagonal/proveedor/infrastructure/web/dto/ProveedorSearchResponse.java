package um.tesoreria.core.hexagonal.proveedor.infrastructure.web.dto;

import lombok.*;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorSearchResponse {

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
    private BigDecimal numeroCuenta;
    private Byte habilitado;
    private String cbu;
    private Cuenta cuenta;
    private String search;

    private java.time.OffsetDateTime fechaAuditoria;
    private String usuarioAuditoria;
}

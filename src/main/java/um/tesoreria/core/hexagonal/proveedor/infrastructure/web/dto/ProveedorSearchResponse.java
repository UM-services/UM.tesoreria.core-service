package um.tesoreria.core.hexagonal.proveedor.infrastructure.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
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
    private BigDecimal cuenta;
    private Byte habilitado;
    private String search;
}

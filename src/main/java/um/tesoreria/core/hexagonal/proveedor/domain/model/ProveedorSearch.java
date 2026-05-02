package um.tesoreria.core.hexagonal.proveedor.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorSearch {

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

package um.tesoreria.core.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuotaPeriodoDto {

    private Integer productoId;
    private Integer mes;
    private Integer anho;
    private Long cantidad;

}

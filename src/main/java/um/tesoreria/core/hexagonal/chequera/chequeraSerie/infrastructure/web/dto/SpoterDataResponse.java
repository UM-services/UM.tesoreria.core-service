package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpoterDataResponse {

    private Boolean status = false;
    private String message = "";
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private ChequeraSerieDto chequeraSerie;
}

package um.tesoreria.core.hexagonal.umhub.consulta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaChequeraDto;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.VencimientoDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Deuda agregada de la persona sobre TODOS los tipos de documento registrados bajo
 * el mismo numero (precedente de slice cruzado: ChequeraCuotaController ya importa
 * DeudaChequeraDto del slice personas).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDeuda {
    private BigDecimal numeroDocumento;
    private Integer cuotas;
    private BigDecimal deuda;
    private List<DeudaChequeraDto> deudas;
    private List<VencimientoDto> vencimientos;
}

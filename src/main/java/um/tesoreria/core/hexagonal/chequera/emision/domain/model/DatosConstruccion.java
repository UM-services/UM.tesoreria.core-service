package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;

import java.math.BigDecimal;
import java.util.List;

/**
 * Datos de entrada para construir las cuotas de un borrador.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatosConstruccion {

    private Integer facultadId;
    private Integer lectivoId;
    private Integer tipoChequeraId;
    private Integer arancelTipoIdSeleccionado;
    private Integer arancelTipoIdChequera;
    private BigDecimal becaPorcentaje;
    private boolean calculaBeca;
    private BigDecimal personaId;
    private Integer documentoId;
    private boolean enTerminoAnterior;
    private List<LectivoCuota> diseno;
    private List<AlternativaEmision> alternativas;
}

package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record PreviewGeneracion(
        CanalGeneracion canal,
        int registros,
        BigDecimal total,
        int excluidos,
        List<InconsistenciaGeneracion> inconsistencias,
        List<LineaGeneracion> lineas) {
}

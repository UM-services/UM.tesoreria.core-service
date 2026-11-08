package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.CanalGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.InconsistenciaGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.LineaGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.PreviewGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoOperacionDebito;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto.CanalGeneracionResponse;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto.InconsistenciaGeneracionResponse;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto.LineaGeneracionResponse;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto.PreviewGeneracionResponse;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto.ResultadoOperacionDebitoResponse;

import java.util.List;

@Component
public class GeneracionArchivoDtoMapper {

    public CanalGeneracionResponse toResponse(CanalGeneracion canal) {
        return new CanalGeneracionResponse(canal.name(), canal.getSlug(), nombre(canal));
    }

    public PreviewGeneracionResponse toResponse(PreviewGeneracion preview) {
        if (preview == null) {
            return null;
        }
        return new PreviewGeneracionResponse(
                preview.canal() == null ? null : preview.canal().name(),
                preview.registros(),
                preview.total(),
                preview.excluidos(),
                preview.inconsistencias() == null ? List.of()
                        : preview.inconsistencias().stream().map(this::toResponse).toList(),
                preview.lineas() == null ? List.of()
                        : preview.lineas().stream().map(this::toResponse).toList());
    }

    public LineaGeneracionResponse toResponse(LineaGeneracion linea) {
        if (linea == null) {
            return null;
        }
        return new LineaGeneracionResponse(
                linea.facultadId(), linea.tipoChequeraId(), linea.chequeraSerieId(), linea.productoId(),
                linea.alternativaId(), linea.cuotaId(), linea.mes(), linea.anho(), linea.importe(),
                linea.vencimiento1(), linea.vencimiento2(), linea.vencimiento3(), linea.facultadNombre(),
                linea.tipoChequeraNombre(), linea.geograficaNombre(), linea.personaNombre(),
                linea.productoNombre(), linea.lectivoNombre(), linea.debitoId(), linea.cbu(),
                linea.numeroTarjeta(), linea.fechaVencimiento());
    }

    public InconsistenciaGeneracionResponse toResponse(InconsistenciaGeneracion inconsistencia) {
        if (inconsistencia == null) {
            return null;
        }
        return new InconsistenciaGeneracionResponse(inconsistencia.chequeraCuotaId(),
                inconsistencia.debitoId(), inconsistencia.descripcion());
    }

    public ResultadoOperacionDebitoResponse toResponse(ResultadoOperacionDebito resultado) {
        if (resultado == null) {
            return null;
        }
        return new ResultadoOperacionDebitoResponse(resultado.operacion(), resultado.registros(),
                resultado.omitidos());
    }

    private String nombre(CanalGeneracion canal) {
        return switch (canal) {
            case PMC -> "PagoMisCuentas";
            case CLICK_PAGOS -> "Click de Pagos";
            case DEBITO_DIRECTO -> "Débito Directo";
            case DEBITO_VISA -> "Débito VISA";
            case PAGAR -> "PAGAR";
            case PAY_PER_TIC -> "PayPerTIC";
        };
    }

}

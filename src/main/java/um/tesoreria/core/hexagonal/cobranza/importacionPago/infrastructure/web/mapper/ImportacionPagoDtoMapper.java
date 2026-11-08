package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoBanco;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoImportado;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.PreviewImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.Rechazo;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoEliminacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoReacreditacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.ImportacionResumenResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.LineaImportacionResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.NoRegistradoResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.PreviewImportacionResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.RechazoResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.ResultadoEliminacionResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.ResultadoImportacionResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.ResultadoReacreditacionResponse;

import java.util.List;

@Component
public class ImportacionPagoDtoMapper {

    public PreviewImportacionResponse toResponse(PreviewImportacion preview) {
        if (preview == null) {
            return null;
        }
        return new PreviewImportacionResponse(
                preview.getFilename(),
                preview.getCanal() == null ? null : preview.getCanal().name(),
                preview.isYaImportada(),
                preview.getRegistros(),
                preview.getTotal(),
                preview.getEncontrados(),
                preview.getPerdidos(),
                preview.getDuplicados(),
                preview.getRechazados(),
                preview.getLineas() == null ? List.of() : preview.getLineas().stream().map(this::toResponse).toList());
    }

    public LineaImportacionResponse toResponse(LineaImportacion linea) {
        if (linea == null) {
            return null;
        }
        return new LineaImportacionResponse(
                linea.getNumeroLinea(),
                linea.getReferencia(),
                linea.getFacultadId(),
                linea.getTipoChequeraId(),
                linea.getChequeraSerieId(),
                linea.getProductoId(),
                linea.getAlternativaId(),
                linea.getCuotaId(),
                linea.getMes(),
                linea.getAnho(),
                linea.getImporte(),
                linea.getFechaPago(),
                linea.getFechaAcreditacion(),
                linea.getMarcaAcreditacion(),
                linea.isReemplazo(),
                linea.getEstado() == null ? null : linea.getEstado().name(),
                linea.getMotivo(),
                linea.getFacultadNombre(),
                linea.getTipoChequeraNombre(),
                linea.getGeograficaNombre(),
                linea.getPersonaNombre(),
                linea.getProductoNombre(),
                linea.getVencimiento());
    }

    public ResultadoImportacionResponse toResponse(ResultadoImportacion resultado) {
        if (resultado == null) {
            return null;
        }
        return new ResultadoImportacionResponse(
                resultado.getImportacionId(),
                resultado.getFilename(),
                resultado.getCanal() == null ? null : resultado.getCanal().name(),
                resultado.getRegistros(),
                resultado.getImportados(),
                resultado.getOmitidos(),
                resultado.getNoEncontrados(),
                resultado.getDuplicados(),
                resultado.getRechazados(),
                resultado.getTotal(),
                resultado.getErrores() == null ? List.of() : resultado.getErrores());
    }

    public ResultadoEliminacionResponse toResponse(ResultadoEliminacion resultado) {
        if (resultado == null) {
            return null;
        }
        return new ResultadoEliminacionResponse(
                resultado.getFilename(),
                resultado.getPagosEliminados(),
                resultado.getPagosReemplazoEliminados(),
                resultado.getArchivosBancoEliminados(),
                resultado.getCuotasRecalculadas());
    }

    public ResultadoReacreditacionResponse toResponse(ResultadoReacreditacion resultado) {
        if (resultado == null) {
            return null;
        }
        return new ResultadoReacreditacionResponse(
                resultado.filename(),
                resultado.canal() == null ? null : resultado.canal().name(),
                resultado.registros(),
                resultado.actualizados(),
                resultado.sinPago(),
                resultado.sinRegistro(),
                resultado.errores() == null ? List.of() : resultado.errores());
    }

    public ImportacionResumenResponse toResponse(ArchivoImportado archivo) {
        if (archivo == null) {
            return null;
        }
        return new ImportacionResumenResponse(
                archivo.getId(),
                archivo.getNombre(),
                archivo.getFecha(),
                archivo.getRegistros());
    }

    public NoRegistradoResponse toResponse(ArchivoBanco archivo) {
        if (archivo == null) {
            return null;
        }
        return new NoRegistradoResponse(
                archivo.getId(),
                archivo.getCodigoEmpresa(),
                archivo.getIdentificacion(),
                archivo.getProductoId(),
                archivo.getMesPago(),
                archivo.getAnhoPago(),
                archivo.getCuotaId(),
                archivo.getImporte(),
                archivo.getFechaPago(),
                archivo.getMarcaAcreditacion(),
                archivo.getFechaAcreditacion(),
                archivo.getTipoPagoId());
    }

    public RechazoResponse toResponse(Rechazo rechazo) {
        if (rechazo == null) {
            return null;
        }
        return new RechazoResponse(
                rechazo.debitoId(),
                rechazo.facultadId(),
                rechazo.tipoChequeraId(),
                rechazo.chequeraSerieId(),
                rechazo.productoId(),
                rechazo.alternativaId(),
                rechazo.cuotaId(),
                rechazo.importe(),
                rechazo.fechaPago(),
                rechazo.motivoRechazo(),
                rechazo.archivoBancoId());
    }

}

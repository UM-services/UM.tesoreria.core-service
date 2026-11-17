package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobDetalle;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobError;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobResumenDia;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoLinea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResumen;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResultado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.CuentaFaltante;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoJobDetalleResponse;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoJobErrorItemResponse;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoJobResponse;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoJobResumenDiaResponse;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoLineaResponse;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoResumenResponse;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoResultadoResponse;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.CuentaFaltanteResponse;

import java.util.List;

@Component
public class AsientoIngresoDtoMapper {

    public AsientoIngresoResumenResponse toResponse(AsientoIngresoResumen resumen) {
        return new AsientoIngresoResumenResponse(
                resumen.getFecha(),
                resumen.getTipoPagoId(),
                resumen.getTipoPagoNombre(),
                resumen.isExiste(),
                resumen.getOrdenContable(),
                resumen.getVinculo(),
                resumen.getTotalDebe(),
                resumen.getTotalHaber(),
                resumen.isCuadra(),
                lineas(resumen.getLineas()));
    }

    public AsientoIngresoResultadoResponse toResponse(AsientoIngresoResultado resultado) {
        AsientoIngresoResumen resumen = resultado.getResumen();
        return new AsientoIngresoResultadoResponse(
                resumen.getFecha(),
                resumen.getTipoPagoId(),
                resumen.getTipoPagoNombre(),
                resumen.isExiste(),
                resumen.getOrdenContable(),
                resumen.getVinculo(),
                resumen.getTotalDebe(),
                resumen.getTotalHaber(),
                resumen.isCuadra(),
                lineas(resumen.getLineas()),
                resultado.getAccion().name(),
                faltantes(resultado.getFaltantes()));
    }

    public AsientoIngresoJobResponse toResponse(AsientoIngresoJob job) {
        return new AsientoIngresoJobResponse(
                job.getJobId(),
                job.getEstado() == null ? null : job.getEstado().name(),
                job.getFechaDesde(),
                job.getFechaHasta(),
                job.getTotalTareas(),
                job.getTareasOk(),
                job.getTareasError(),
                job.getTareasSinMovimientos(),
                job.getTareasPendientes(),
                job.porcentaje(),
                job.getFechaActual(),
                job.getTipoPagoActual(),
                job.getUsuarioId(),
                job.getIniciado(),
                job.getFinalizado(),
                job.getMensaje());
    }

    public AsientoIngresoJobDetalleResponse toResponse(AsientoIngresoJobDetalle detalle) {
        AsientoIngresoJob job = detalle.getJob();
        return new AsientoIngresoJobDetalleResponse(
                job.getJobId(),
                job.getEstado() == null ? null : job.getEstado().name(),
                job.getFechaDesde(),
                job.getFechaHasta(),
                job.getTotalTareas(),
                job.getTareasOk(),
                job.getTareasError(),
                job.getTareasSinMovimientos(),
                job.getTareasPendientes(),
                job.porcentaje(),
                job.getFechaActual(),
                job.getTipoPagoActual(),
                job.getUsuarioId(),
                job.getIniciado(),
                job.getFinalizado(),
                job.getMensaje(),
                detalle.getResumenPorFecha() == null ? List.of()
                        : detalle.getResumenPorFecha().stream().map(this::toResponse).toList(),
                detalle.getErrores() == null ? List.of()
                        : detalle.getErrores().stream().map(this::toResponse).toList());
    }

    private List<AsientoIngresoLineaResponse> lineas(List<AsientoIngresoLinea> lineas) {
        return lineas == null ? List.of() : lineas.stream()
                .map(linea -> new AsientoIngresoLineaResponse(
                        linea.getNumeroCuenta(),
                        linea.getNombreCuenta(),
                        linea.getDebita(),
                        linea.getImporte()))
                .toList();
    }

    private List<CuentaFaltanteResponse> faltantes(List<CuentaFaltante> faltantes) {
        return faltantes == null ? List.of() : faltantes.stream()
                .map(faltante -> new CuentaFaltanteResponse(
                        faltante.getFacultadId(),
                        faltante.getFacultadNombre(),
                        faltante.getLectivoId(),
                        faltante.getLectivoNombre(),
                        faltante.getTipoChequeraId(),
                        faltante.getTipoChequeraNombre(),
                        faltante.getProductoId(),
                        faltante.getProductoNombre(),
                        faltante.getGeograficaId(),
                        faltante.getGeograficaNombre()))
                .toList();
    }

    private AsientoIngresoJobResumenDiaResponse toResponse(AsientoIngresoJobResumenDia dia) {
        return new AsientoIngresoJobResumenDiaResponse(
                dia.getFecha(), dia.getOk(), dia.getError(), dia.getSinMovimientos());
    }

    private AsientoIngresoJobErrorItemResponse toResponse(AsientoIngresoJobError error) {
        return new AsientoIngresoJobErrorItemResponse(
                error.getFecha(),
                error.getTipoPagoId(),
                error.getTipoPagoNombre(),
                error.getCodigo(),
                error.getMensaje());
    }

}

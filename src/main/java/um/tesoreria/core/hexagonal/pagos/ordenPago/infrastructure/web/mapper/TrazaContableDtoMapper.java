package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.LineaPropuesta;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrigenAsiento;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionAsiento;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionPlan;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.AsientoLineaResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.AsientoOrigenResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.ReconstruccionAsientoResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.ReconstruccionPlanResponse;

import java.util.List;

/**
 * Traduce el plan de reconstrucción de traza a DTOs de la capa web.
 */
@Component
public class TrazaContableDtoMapper {

    public ReconstruccionPlanResponse toResponse(ReconstruccionPlan plan) {
        if (plan == null) {
            return null;
        }
        return new ReconstruccionPlanResponse(plan.proveedorMovimientoId(),
                plan.prefijo(),
                plan.numeroComprobante(),
                toAsientos(plan.asientos()),
                plan.cantidadCrear(),
                plan.cantidadActualizar(),
                plan.cantidadSinCambios(),
                plan.requiereForzar(),
                plan.sinCambios(),
                plan.errores());
    }

    private List<ReconstruccionAsientoResponse> toAsientos(List<ReconstruccionAsiento> asientos) {
        if (asientos == null) {
            return List.of();
        }
        return asientos.stream().map(this::toResponse).toList();
    }

    private ReconstruccionAsientoResponse toResponse(ReconstruccionAsiento asiento) {
        if (asiento == null) {
            return null;
        }
        return new ReconstruccionAsientoResponse(asiento.fechaContable(),
                asiento.ordenContable(),
                toOrigenes(asiento.origenes()),
                asiento.accion() == null ? null : asiento.accion().name(),
                toLineas(asiento.aAgregar()),
                asiento.idsEliminar(),
                asiento.lineasPropuestas(),
                asiento.lineasExistentes(),
                asiento.compartido());
    }

    private List<AsientoOrigenResponse> toOrigenes(List<OrigenAsiento> origenes) {
        if (origenes == null) {
            return List.of();
        }
        return origenes.stream().map(this::toResponse).toList();
    }

    private AsientoOrigenResponse toResponse(OrigenAsiento origen) {
        if (origen == null) {
            return null;
        }
        return new AsientoOrigenResponse(origen.tipo() == null ? null : origen.tipo().name(),
                origen.referenciaId());
    }

    private List<AsientoLineaResponse> toLineas(List<LineaPropuesta> lineas) {
        if (lineas == null) {
            return List.of();
        }
        return lineas.stream().map(this::toResponse).toList();
    }

    private AsientoLineaResponse toResponse(LineaPropuesta linea) {
        if (linea == null) {
            return null;
        }
        return new AsientoLineaResponse(linea.numeroCuenta(),
                linea.debita(),
                linea.importe(),
                linea.comprobanteId(),
                linea.proveedorId(),
                linea.concepto());
    }

}

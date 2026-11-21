package um.tesoreria.core.hexagonal.pagos.ordenPago.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDetalle;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoPreview;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoResultado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.CreateOrdenPagoDirectaUseCase;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.CreateOrdenPagoUseCase;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.GetOrdenPagoDetalleUseCase;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.PreviewOrdenPagoDirectaUseCase;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.PreviewOrdenPagoUseCase;

@Service("pagosOrdenPagoService")
@RequiredArgsConstructor
public class OrdenPagoService {

    private final PreviewOrdenPagoUseCase previewOrdenPagoUseCase;
    private final CreateOrdenPagoUseCase createOrdenPagoUseCase;
    private final GetOrdenPagoDetalleUseCase getOrdenPagoDetalleUseCase;
    private final PreviewOrdenPagoDirectaUseCase previewOrdenPagoDirectaUseCase;
    private final CreateOrdenPagoDirectaUseCase createOrdenPagoDirectaUseCase;

    public OrdenPagoPreview preview(OrdenPagoSolicitud solicitud) {
        return previewOrdenPagoUseCase.preview(solicitud);
    }

    public OrdenPagoResultado create(OrdenPagoSolicitud solicitud) {
        return createOrdenPagoUseCase.create(solicitud);
    }

    public OrdenPagoDetalle findDetalle(Long proveedorMovimientoId) {
        return getOrdenPagoDetalleUseCase.getDetalle(proveedorMovimientoId);
    }

    public OrdenPagoPreview previewDirecta(OrdenPagoDirectaSolicitud solicitud) {
        return previewOrdenPagoDirectaUseCase.previewDirecta(solicitud);
    }

    public OrdenPagoResultado createDirecta(OrdenPagoDirectaSolicitud solicitud) {
        return createOrdenPagoDirectaUseCase.createDirecta(solicitud);
    }
}

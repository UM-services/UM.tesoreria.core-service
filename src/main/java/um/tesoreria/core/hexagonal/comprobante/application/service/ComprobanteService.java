package um.tesoreria.core.hexagonal.comprobante.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.comprobante.application.exception.ComprobanteException;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.in.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComprobanteService {

    private final GetAllComprobantesUseCase getAllComprobantesUseCase;
    private final GetAllComprobantesByOrdenPagoUseCase getAllComprobantesByOrdenPagoUseCase;
    private final GetAllComprobantesByTipoTransaccionIdUseCase getAllComprobantesByTipoTransaccionIdUseCase;
    private final GetAllComprobantesByOrdenPagoAndTipoTransaccionIdUseCase getAllComprobantesByOrdenPagoAndTipoTransaccionIdUseCase;
    private final GetComprobanteByTipoTransaccionIdUseCase getComprobanteByTipoTransaccionIdUseCase;
    private final GetComprobanteByIdUseCase getComprobanteByIdUseCase;

    public List<Comprobante> findAll() {
        return getAllComprobantesUseCase.getAllComprobantes();
    }

    public List<Comprobante> findAllByOrdenPago() {
        return getAllComprobantesByOrdenPagoUseCase.getAllComprobantesByOrdenPago();
    }

    public List<Comprobante> findAllByTipoTransaccionId(Integer tipoTransaccionId) {
        return getAllComprobantesByTipoTransaccionIdUseCase.getAllComprobantesByTipoTransaccionId(tipoTransaccionId);
    }

    public List<Comprobante> findAllByOrdenPagoAndTipoTransaccionId(Integer tipoTransaccionId) {
        return getAllComprobantesByOrdenPagoAndTipoTransaccionIdUseCase.getAllComprobantesByOrdenPagoAndTipoTransaccionId(tipoTransaccionId);
    }

    public Comprobante findByTipoTransaccionId(Integer tipoTransaccionId) {
        return getComprobanteByTipoTransaccionIdUseCase.getComprobanteByTipoTransaccionId(tipoTransaccionId)
                .orElseThrow(() -> new ComprobanteException());
    }

    public Comprobante findByComprobanteId(Integer comprobanteId) {
        return getComprobanteByIdUseCase.getComprobanteById(comprobanteId)
                .orElseThrow(() -> new ComprobanteException(comprobanteId));
    }
}

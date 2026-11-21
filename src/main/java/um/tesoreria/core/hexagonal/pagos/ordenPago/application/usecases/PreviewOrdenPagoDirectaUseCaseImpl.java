package um.tesoreria.core.hexagonal.pagos.ordenPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.model.OrdenPagoDirectaPreparado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.service.OrdenPagoDirectaPreparador;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.AsientoPropuesto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.AsientoPropuestoBuilder;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoPreview;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.PreviewOrdenPagoDirectaUseCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PreviewOrdenPagoDirectaUseCaseImpl implements PreviewOrdenPagoDirectaUseCase {

    private final OrdenPagoDirectaPreparador preparador;

    @Override
    public OrdenPagoPreview previewDirecta(OrdenPagoDirectaSolicitud solicitud) {
        OrdenPagoDirectaPreparado preparado = preparador.preparar(solicitud);
        List<AsientoPropuesto> asientos = AsientoPropuestoBuilder.buildDirecta(preparado.contexto(), false);
        List<String> errores = new ArrayList<>(preparado.errores());
        BigDecimal totalDebe = asientos.stream().map(AsientoPropuesto::totalDebe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaber = asientos.stream().map(AsientoPropuesto::totalHaber)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean cuadra = totalDebe.compareTo(totalHaber) == 0
                && asientos.stream().allMatch(AsientoPropuesto::cuadra);
        if (!cuadra && !errores.contains("ERROR: Asiento NO CUADRA")) {
            errores.add("ERROR: Asiento NO CUADRA");
        }
        return new OrdenPagoPreview(asientos, totalDebe, totalHaber, cuadra, BigDecimal.ZERO,
                preparado.totalValores(), preparado.prefijo(), preparado.numeroComprobante(), errores);
    }
}

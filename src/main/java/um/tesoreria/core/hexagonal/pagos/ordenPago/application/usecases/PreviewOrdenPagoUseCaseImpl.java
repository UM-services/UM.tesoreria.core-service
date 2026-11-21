package um.tesoreria.core.hexagonal.pagos.ordenPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.model.OrdenPagoPreparado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.service.OrdenPagoPreparador;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.AsientoPropuesto;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.AsientoPropuestoBuilder;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoPreview;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.PreviewOrdenPagoUseCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PreviewOrdenPagoUseCaseImpl implements PreviewOrdenPagoUseCase {

    private final OrdenPagoPreparador preparador;

    @Override
    public OrdenPagoPreview preview(OrdenPagoSolicitud solicitud) {
        OrdenPagoPreparado preparado = preparador.preparar(solicitud);
        List<AsientoPropuesto> asientos = AsientoPropuestoBuilder.buildOrdenPago(preparado.contexto(), false);
        List<String> errores = new ArrayList<>(preparado.errores());

        BigDecimal totalDebe = asientos.stream()
                .map(AsientoPropuesto::totalDebe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaber = asientos.stream()
                .map(AsientoPropuesto::totalHaber)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean cuadra = totalDebe.compareTo(totalHaber) == 0
                && asientos.stream().allMatch(AsientoPropuesto::cuadra);
        if (!cuadra && errores.stream().noneMatch("ERROR: Asiento NO CUADRA"::equals)) {
            errores.add("ERROR: Asiento NO CUADRA");
        }

        return new OrdenPagoPreview(
                asientos,
                totalDebe,
                totalHaber,
                cuadra,
                preparado.totalFacturas(),
                preparado.totalValores(),
                preparado.prefijo(),
                preparado.numeroComprobante(),
                errores);
    }
}

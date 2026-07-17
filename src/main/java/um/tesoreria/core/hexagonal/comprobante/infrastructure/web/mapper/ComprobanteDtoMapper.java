package um.tesoreria.core.hexagonal.comprobante.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.web.dto.ComprobanteResponse;

@Component
public class ComprobanteDtoMapper {

    public ComprobanteResponse toResponse(Comprobante domain) {
        if (domain == null) return null;
        ComprobanteResponse.ComprobanteResponseBuilder builder = ComprobanteResponse.builder()
                .comprobanteId(domain.getComprobanteId())
                .tipoTransaccionId(domain.getTipoTransaccionId())
                .comprobanteAfipId(domain.getComprobanteAfipId())
                .puntoVenta(domain.getPuntoVenta())
                .letraComprobante(domain.getLetraComprobante());

        if (domain.getDescripcion() != null) builder.descripcion(domain.getDescripcion());
        if (domain.getOrdenPago() != null) builder.ordenPago(domain.getOrdenPago());
        if (domain.getAplicaPendiente() != null) builder.aplicaPendiente(domain.getAplicaPendiente());
        if (domain.getCuentaCorriente() != null) builder.cuentaCorriente(domain.getCuentaCorriente());
        if (domain.getDebita() != null) builder.debita(domain.getDebita());
        if (domain.getDiasVigencia() != null) builder.diasVigencia(domain.getDiasVigencia());
        if (domain.getFacturacionElectronica() != null) builder.facturacionElectronica(domain.getFacturacionElectronica());

        return builder.build();
    }
}

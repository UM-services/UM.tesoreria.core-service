package um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.entity.ComprobanteEntity;

@Component
public class ComprobanteMapper {

    public Comprobante toDomainModel(ComprobanteEntity entity) {
        if (entity == null) return null;
        return Comprobante.builder()
                .comprobanteId(entity.getComprobanteId())
                .descripcion(entity.getDescripcion())
                .tipoTransaccionId(entity.getTipoTransaccionId())
                .ordenPago(entity.getOrdenPago())
                .aplicaPendiente(entity.getAplicaPendiente())
                .cuentaCorriente(entity.getCuentaCorriente())
                .debita(entity.getDebita())
                .diasVigencia(entity.getDiasVigencia())
                .facturacionElectronica(entity.getFacturacionElectronica())
                .comprobanteAfipId(entity.getComprobanteAfipId())
                .puntoVenta(entity.getPuntoVenta())
                .letraComprobante(entity.getLetraComprobante())
                .build();
    }

    public ComprobanteEntity toEntity(Comprobante domain) {
        if (domain == null) return null;
        ComprobanteEntity.ComprobanteEntityBuilder builder = ComprobanteEntity.builder()
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

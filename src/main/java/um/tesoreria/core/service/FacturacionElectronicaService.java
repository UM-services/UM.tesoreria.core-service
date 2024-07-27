package um.tesoreria.core.service;

import org.springframework.data.domain.Sort;
import um.tesoreria.core.exception.FacturacionElectronicaException;
import um.tesoreria.core.kotlin.model.FacturacionElectronica;
import um.tesoreria.core.repository.IFacturacionElectronicaRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FacturacionElectronicaService {

    private final IFacturacionElectronicaRepository repository;

    public FacturacionElectronicaService(IFacturacionElectronicaRepository repository) {
        this.repository = repository;
    }

    public List<FacturacionElectronica> findAllByPeriodo(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        return repository.findAllByFechaReciboBetween(fechaDesde, fechaHasta, Sort.by("fechaRecibo").ascending().and(Sort.by("facturacionElectronicaId")));
    }

    public List<FacturacionElectronica> findAllByChequeraPagoIds(List<Long> chequeraPagoIds) {
        return repository.findAllByChequeraPagoIdIn(chequeraPagoIds);
    }

    public FacturacionElectronica findByFacturacionElectronicaId(Long facturacionElectronicaId) {
        return repository.findByFacturacionElectronicaId(facturacionElectronicaId).orElseThrow(() -> new FacturacionElectronicaException(facturacionElectronicaId));
    }

    public FacturacionElectronica findNextPendiente() {
        return repository.findTopByEnviadaAndRetriesLessThanOrderByFacturacionElectronicaId((byte) 0, 3).orElseThrow(FacturacionElectronicaException::new);
    }

    public FacturacionElectronica add(FacturacionElectronica facturacionElectronica) {
        facturacionElectronica = repository.save(facturacionElectronica);
        return facturacionElectronica;
    }

    public FacturacionElectronica update(FacturacionElectronica newFacturacionElectronica, Long facturacionElectronicaId) {
        return repository.findByFacturacionElectronicaId(facturacionElectronicaId).map(facturacionElectronica -> {
            facturacionElectronica = new FacturacionElectronica.Builder()
                    .facturacionElectronicaId(facturacionElectronicaId)
                    .chequeraPagoId(newFacturacionElectronica.getChequeraPagoId())
                    .comprobanteId(newFacturacionElectronica.getComprobanteId())
                    .numeroComprobante(newFacturacionElectronica.getNumeroComprobante())
                    .personaId(newFacturacionElectronica.getPersonaId())
                    .tipoDocumento(newFacturacionElectronica.getTipoDocumento())
                    .apellido(newFacturacionElectronica.getApellido())
                    .nombre(newFacturacionElectronica.getNombre())
                    .cuit(newFacturacionElectronica.getCuit())
                    .condicionIva(newFacturacionElectronica.getCondicionIva())
                    .importe(newFacturacionElectronica.getImporte())
                    .cae(newFacturacionElectronica.getCae())
                    .fechaRecibo(newFacturacionElectronica.getFechaRecibo())
                    .fechaVencimientoCae(newFacturacionElectronica.getFechaVencimientoCae())
                    .enviada(newFacturacionElectronica.getEnviada())
                    .retries(newFacturacionElectronica.getRetries())
                    .build();
            return repository.save(facturacionElectronica);
        }).orElseThrow(() ->new FacturacionElectronicaException(facturacionElectronicaId));
    }

}
